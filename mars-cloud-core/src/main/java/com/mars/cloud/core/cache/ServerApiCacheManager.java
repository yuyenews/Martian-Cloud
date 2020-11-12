package com.mars.cloud.core.cache;

import com.mars.cloud.constant.MarsCloudConstant;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.core.notice.model.RestApiModel;
import com.mars.cloud.core.offline.OfflineManager;
import com.mars.cloud.util.MarsCloudConfigUtil;
import com.mars.cloud.util.MarsCloudUtil;
import com.mars.common.constant.MarsConstant;
import com.mars.common.constant.MarsSpace;
import com.mars.mvc.load.model.MarsMappingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 本地缓存管理
 */
public class ServerApiCacheManager {

    private static Logger logger = LoggerFactory.getLogger(ServerApiCacheManager.class);

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
     */
    public static void addCacheApi(RestApiModel restApiModel) {
        for(RestApiCacheModel restApiCacheModel : restApiModel.getRestApiCacheModels()){
            long oldCreateTime = OfflineManager.getDisableTime(restApiCacheModel.getLocalHost());
            if(oldCreateTime >= restApiCacheModel.getCreateTime()){
                continue;
            }
            serverApiCache.addCache(restApiModel.getServerName(), restApiCacheModel.getMethodName(), restApiCacheModel);
        }
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
     * 获取所有API
     * @return
     */
    public static List<RestApiCacheModel> getMarsApis() throws Exception {
        /* 如果本地接口已经加载过了，则直接返回 */
        Object localApis =  marsSpace.getAttr(MarsCloudConstant.LOCAL_APIS);
        if(localApis != null){
            return (List<RestApiCacheModel>)localApis;
        }

        /* 从内存中获取本项目的MarsApi */
        Object apiMap = MarsSpace.getEasySpace().getAttr(MarsConstant.CONTROLLER_OBJECTS);
        if (apiMap == null) {
            return new ArrayList<>();
        }

        List<RestApiCacheModel> restApiModelList = new ArrayList<>();

        long createTime = System.currentTimeMillis();

        Map<String, MarsMappingModel> marsApiObjects = (Map<String, MarsMappingModel>) apiMap;
        for(String methodName : marsApiObjects.keySet()){
            MarsMappingModel marsMappingModel = marsApiObjects.get(methodName);
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

        return restApiModelList;
    }
}
