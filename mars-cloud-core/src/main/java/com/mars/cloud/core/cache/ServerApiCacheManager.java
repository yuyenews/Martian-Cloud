package com.mars.cloud.core.cache;

import com.alibaba.fastjson.JSON;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.core.notice.model.RestApiModel;
import com.mars.cloud.util.DateUtil;
import com.mars.cloud.util.MarsCloudConfigUtil;
import com.mars.cloud.util.MarsCloudUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 本地缓存管理
 */
public class ServerApiCacheManager {

    private static Logger logger = LoggerFactory.getLogger(ServerApiCacheManager.class);

    private static ServerApiCache serverApiCache = new ServerApiCache();

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
    public static void addCacheApi(RestApiModel restApiModel, boolean updateTime){
        for(RestApiCacheModel restApiCacheModel : restApiModel.getRestApiCacheModels()){
            serverApiCache.addCache(restApiModel.getServerName(), restApiCacheModel.getMethodName(), restApiCacheModel, updateTime);
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
     * 定时清理过期接口
     */
    public static void clearTimeOutApis() throws Exception {

        long apiTimeOut = MarsCloudConfigUtil.getMarsCloudConfig().getCloudConfig().getApiTimeOut();
        String fmt = "yyyy-MM-dd HH:mm:ss";

        Map<String, List<RestApiCacheModel>> restApiCacheMap = serverApiCache.getRestApiModelsByKey();
        if(restApiCacheMap != null){
            int count = 0;
            for(Map.Entry<String, List<RestApiCacheModel>> entry : restApiCacheMap.entrySet()){
                String key = entry.getKey();
                List<RestApiCacheModel> restApiCacheModels = entry.getValue();
                if(restApiCacheModels == null){
                    continue;
                }

                count = count + restApiCacheModels.size();
                List<RestApiCacheModel> removeApis = new ArrayList<>();
                for(RestApiCacheModel restApiCacheModel : restApiCacheModels){
                    if(restApiCacheModel.getLocalHost().equals(MarsCloudUtil.getLocalHost())){
                        /* 自己不再被回收的行列 */
                        continue;
                    }
                    if(!DateUtil.range(restApiCacheModel.getCreateTime(), new Date(), apiTimeOut, fmt)){
                        removeApis.add(restApiCacheModel);
                    }
                }
                restApiCacheModels.removeAll(removeApis);

                if(restApiCacheModels == null || restApiCacheModels.size() < 1){
                    restApiCacheMap.remove(key);
                    continue;
                }
                restApiCacheMap.put(key, restApiCacheModels);
            }
            logger.info("接口总数:{}", count);
        }
    }
}
