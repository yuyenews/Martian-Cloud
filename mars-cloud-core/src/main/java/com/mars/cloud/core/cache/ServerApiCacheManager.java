package com.mars.cloud.core.cache;

import com.mars.cloud.constant.MarsCloudConstant;
import com.mars.cloud.core.blanced.PollingIndexManager;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.core.notice.model.RestApiModel;
import com.mars.cloud.core.offline.OfflineManager;
import com.mars.cloud.core.vote.VoteManager;
import com.mars.cloud.util.MarsCloudConfigUtil;
import com.mars.cloud.util.MarsCloudUtil;
import com.mars.common.constant.MarsConstant;
import com.mars.common.constant.MarsSpace;
import com.mars.mvc.load.model.MarsMappingModel;

import java.util.*;

/**
 * 本地缓存管理
 */
public class ServerApiCacheManager {

    private static ServerApiCache serverApiCache = new ServerApiCache();

    private static MarsSpace marsSpace = MarsSpace.getEasySpace();

    /**
     * 获取本地缓存的所有接口
     * @return
     */
    public static Map<String, List<RestApiCacheModel>> getCacheApisMap(){
        return serverApiCache.getRestApiModelsByKey();
    }

    /**
     * 保存接口到本地缓存
     * @param restApiModel
     * @param isNotice
     */
    public static void addCacheApi(RestApiModel restApiModel, boolean isNotice) throws Exception {
        /* 保存接口至本地缓存 */
        for(RestApiCacheModel restApiCacheModel : restApiModel.getRestApiCacheModels()){

            boolean isDisable = OfflineManager.isDisable(restApiCacheModel.getLocalHost());
            if(isDisable){
                long oldCreateTime = OfflineManager.getDisableTime(restApiCacheModel.getLocalHost());
                if(isNotice){
                    /* 如果此接口是服务主动广播过来的，则说明他活着，那就将下线标识移除 */
                    OfflineManager.removeDisable(restApiCacheModel.getLocalHost());
                } else if(oldCreateTime >= restApiCacheModel.getCreateTime()){
                    /* 如果此服务不是主动广播过来的，而是我拉取过来的，那就要判断他的创建时间是否大于被我下线时的时间 */
                    continue;
                }
            }

            serverApiCache.addCache(restApiModel.getServerName(), restApiCacheModel.getMethodName(), restApiCacheModel);
        }

        /* 重新初始化本地的投票列表 */
        VoteManager.loadVote();

        /* 初始化轮询下标 */
        PollingIndexManager.initPollingMap();
    }

    /**
     * 将自己的接口加载到本地缓存
     * @throws Exception
     */
    public static void loadLocalApis() throws Exception {
        String serverName = MarsCloudConfigUtil.getCloudName();

        List<RestApiCacheModel> restApiModelList = getMarsApis();
        for (RestApiCacheModel restApiModel : restApiModelList) {
            serverApiCache.addCache(serverName, restApiModel.getMethodName(), restApiModel);
        }
    }

    /**
     * 获取本地缓存的所有服务的ip:port
     * @param filterMy 是否过滤掉自己 true是
     * @return
     * @throws Exception
     */
    public static List<String> getAllServerList(boolean filterMy) throws Exception {
        List<String> restApiCacheUrlList = new ArrayList<>();

        Map<String, List<RestApiCacheModel>> restApiCacheModelMap = serverApiCache.getRestApiModelsByKey();
        if(restApiCacheModelMap == null || restApiCacheModelMap.size() < 1){
            return restApiCacheUrlList;
        }

        for(List<RestApiCacheModel> restApiCacheModels : restApiCacheModelMap.values()){
            for(RestApiCacheModel restApiCacheModel : restApiCacheModels){
                if(filterMy && restApiCacheModel.getLocalHost().equals(MarsCloudUtil.getLocalHost())){
                    /* 过滤掉自己 */
                    continue;
                }
                if(restApiCacheUrlList.contains(restApiCacheModel.getLocalHost())){
                    /* 过滤掉已存在的 */
                    continue;
                }
                restApiCacheUrlList.add(restApiCacheModel.getLocalHost());
            }
        }
        return restApiCacheUrlList;
    }

    /**
     * 根据服务名称和方法名获取
     * @param serverName
     * @param methodName
     * @return
     */
    public static List<RestApiCacheModel> getRestApiModelForCache(String serverName, String methodName) {
        List<RestApiCacheModel> restApiCacheModelList = serverApiCache.getRestApiCacheModelList(serverName,methodName);
        return restApiCacheModelList;
    }

    /**
     * 获取下线的host
     * @return
     */
    public static Set<String> getOfflineHost(){
        Set<String> offlineHostList = new HashSet<>();

        Map<String, List<RestApiCacheModel>> restApiCacheMap = ServerApiCacheManager.getCacheApisMap();

        Set<String> onRemoveKeys = new HashSet<>();
        int count = 0;
        for(String key : restApiCacheMap.keySet()){
            List<RestApiCacheModel> restApiCacheModelList = restApiCacheMap.get(key);
            if(restApiCacheModelList == null || restApiCacheModelList.size() < 1){
                continue;
            }

            List<RestApiCacheModel> removeObj = new ArrayList<>();

            /* 寻找已经下线的服务接口 */
            for(RestApiCacheModel restApiCacheModel : restApiCacheModelList){
                if(OfflineManager.isDisable(restApiCacheModel.getLocalHost())){
                    removeObj.add(restApiCacheModel);
                    offlineHostList.add(restApiCacheModel.getLocalHost());
                }
            }

            /* 删除已经下线的服务的接口 */
            if(removeObj.size() > 0){
                restApiCacheModelList.removeAll(removeObj);
            }

            /* 如果某个key下面的list已经空了，则删除这个key */
            if(restApiCacheModelList == null || restApiCacheModelList.size() < 1){
                onRemoveKeys.add(key);
            }
            count = count + restApiCacheModelList.size();
            restApiCacheMap.put(key, restApiCacheModelList);
        }
        /* 清理value长度为0的元素 */
        for(String key : onRemoveKeys){
            restApiCacheMap.remove(key);
            /* 服务被清理了，所以轮询的下标也要清理 */
            PollingIndexManager.removePolling(key);
        }
        System.out.println("接口总数:" + count);
        return offlineHostList;
    }

    /**
     * 获取所有API
     * @return
     */
    public static List<RestApiCacheModel> getMarsApis() throws Exception {
        /* 如果本地接口已经加载过了，则直接返回 */
        Object localApis =  marsSpace.getAttr(MarsCloudConstant.LOCAL_APIS);
        if(localApis != null){
            return (List<RestApiCacheModel>)localApis;
        }

        List<RestApiCacheModel> restApiModelList = new ArrayList<>();

        /* 从内存中获取本项目的MarsApi */
        Object apiMap = marsSpace.getAttr(MarsConstant.CONTROLLER_OBJECTS);
        if (apiMap != null) {
            long createTime = System.currentTimeMillis();

            Map<String, MarsMappingModel> marsApiObjects = (Map<String, MarsMappingModel>) apiMap;
            for(MarsMappingModel marsMappingModel : marsApiObjects.values()){
                if(marsMappingModel == null){
                    continue;
                }
                String mName = marsMappingModel.getExeMethod().getName();

                RestApiCacheModel restApiModel = new RestApiCacheModel();
                restApiModel.setUrl(MarsCloudUtil.getLocalHost() + "/" + mName);
                restApiModel.setMethodName(mName);
                restApiModel.setLocalHost(MarsCloudUtil.getLocalHost());
                restApiModel.setReqMethod(marsMappingModel.getReqMethod());
                restApiModel.setCreateTime(createTime);
                restApiModelList.add(restApiModel);
            }

            marsSpace.setAttr(MarsCloudConstant.LOCAL_APIS, restApiModelList);
        }

        return restApiModelList;
    }
}
