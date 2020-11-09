package com.mars.cloud.core.cache;

import com.mars.cloud.core.cache.model.RestApiCacheModel;

import java.util.List;

/**
 * 服务的本地缓存，从naco获取接口列表保存到本地
 */
public class ServerApiCacheManager {

    private static ServerApiCache serverApiCache = new ServerApiCache();

    /**
     * 获取
     * @param serverName
     * @param methodName
     * @return
     */
    public static List<RestApiCacheModel> getRestApiModelForCache(String serverName, String methodName) throws Exception {
        List<RestApiCacheModel> restApiCacheModelList = serverApiCache.getRestApiCacheModelList(serverName,methodName);
        return restApiCacheModelList;
    }
}
