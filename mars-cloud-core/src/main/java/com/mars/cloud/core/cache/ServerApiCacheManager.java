package com.mars.cloud.core.cache;

import com.mars.cloud.core.cache.model.RestApiCacheModel;

import java.util.List;

/**
 * 本地缓存管理
 */
public class ServerApiCacheManager {

    private static ServerApiCache serverApiCache = new ServerApiCache();

    /**
     * 获取
     * @param serverName
     * @param methodName
     * @return
     */
    public static List<RestApiCacheModel> getRestApiModelForCache(String serverName, String methodName) {
        List<RestApiCacheModel> restApiCacheModelList = serverApiCache.getRestApiCacheModelList(serverName,methodName);
        return restApiCacheModelList;
    }
}
