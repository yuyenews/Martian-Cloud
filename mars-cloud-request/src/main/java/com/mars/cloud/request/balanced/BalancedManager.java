package com.mars.cloud.request.balanced;

import com.mars.cloud.core.cache.ServerApiCacheManager;
import com.mars.cloud.core.cache.model.RestApiCacheModel;

import java.util.List;

/**
 * 负载均衡管理
 */
public class BalancedManager {

    /**
     * 从接口集里面，用负载均衡算法获取一个
     * @param serverName
     * @param methodName
     * @return
     * @throws Exception
     */
    public static RestApiCacheModel getRestApiCacheModel(String serverName, String methodName) throws Exception {
        List<RestApiCacheModel> restApiCacheModelList = ServerApiCacheManager.getRestApiModelForCache(serverName, methodName);

        BalancedCalc balancedCalc = BalancedFactory.getBalancedCalc();
        return balancedCalc.getRestApiCacheModel(serverName,methodName,restApiCacheModelList);
    }
}
