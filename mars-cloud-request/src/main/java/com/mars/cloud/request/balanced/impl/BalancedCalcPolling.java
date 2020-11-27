package com.mars.cloud.request.balanced.impl;

import com.mars.cloud.core.blanced.PollingIndexManager;
import com.mars.cloud.core.cache.ServerApiCache;
import com.mars.cloud.model.RestApiCacheModel;
import com.mars.cloud.request.balanced.BalancedCalc;

import java.util.*;

/**
 * 轮询算法
 */
public class BalancedCalcPolling implements BalancedCalc {


    @Override
    public RestApiCacheModel getRestApiCacheModel(String serverName, String methodName, List<RestApiCacheModel> restApiCacheModelList) {
        String key = ServerApiCache.getKey(serverName,methodName);

        int index = PollingIndexManager.getPollingIndex(key, restApiCacheModelList.size());
        RestApiCacheModel restApiCacheModel = restApiCacheModelList.get(index);
        while(restApiCacheModel == null && index > 0){
            index--;
            restApiCacheModel = restApiCacheModelList.get(index);
            if(restApiCacheModel != null){
                return restApiCacheModel;
            }
        }
        return restApiCacheModel;
    }

}
