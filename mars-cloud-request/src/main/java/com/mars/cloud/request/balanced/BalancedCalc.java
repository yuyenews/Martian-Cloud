package com.mars.cloud.request.balanced;

import com.mars.cloud.core.cache.model.RestApiCacheModel;

import java.util.List;

/**
 * 负载均衡计算
 */
public interface BalancedCalc {

    /**
     * 计算后得出接口
     * @param serverName
     * @param methodName
     * @return
     */
    RestApiCacheModel getRestApiCacheModel(String serverName, String methodName, List<RestApiCacheModel> restApiCacheModelList);
}
