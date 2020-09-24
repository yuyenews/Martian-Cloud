package com.mars.cloud.core.cache;

import com.mars.cloud.core.cache.model.RestApiCacheModel;

import java.util.List;

/**
 * 服务的本地缓存，从naco获取接口列表保存到本地
 */
public class ServerApiCacheManager {

    private static ServerApiCache serverApiCache = new ServerApiCache();

    private static LoadServerCache loadServerCache = new LoadServerCache();

    /**
     * 获取
     * @param serverName
     * @param methodName
     * @return
     */
    public static List<RestApiCacheModel> getRestApiModelForCache(String serverName, String methodName) throws Exception {
        List<RestApiCacheModel> restApiCacheModelList = serverApiCache.getRestApiCacheModelList(serverName,methodName);
        if(restApiCacheModelList == null || restApiCacheModelList.size() < 1){
            /* 如果缓存中没有获取到接口，就去zk里面获取 */
            restApiCacheModelList = loadServerCache.getRestApiCacheModelByServerName(serverName, methodName);
            if(restApiCacheModelList == null || restApiCacheModelList.size() < 1){
                throw new Exception("没有找到服务接口, serverName:" + serverName + ", methodName:" + methodName);
            }

            for(RestApiCacheModel item : restApiCacheModelList){
                serverApiCache.addCache(serverName, methodName, item);
            }
        }

        return restApiCacheModelList;
    }
}
