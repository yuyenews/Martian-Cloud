package com.mars.cloud.request.balanced;

import com.mars.cloud.balanced.BalancedCalc;
import com.mars.cloud.core.cache.ServerApiCacheManager;
import com.mars.cloud.model.RestApiCacheModel;

import java.net.UnknownHostException;
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
        /* 根据服务名和方法名，在本地缓存查找接口集合 */
        List<RestApiCacheModel> restApiCacheModelList = ServerApiCacheManager.getRestApiModelForCache(serverName, methodName);
        if(restApiCacheModelList == null || restApiCacheModelList.size() < 1){
            throw new UnknownHostException("没找到对应的接口,服务名称:" + serverName + ", 接口名称:" + methodName);
        }

        /* 通过负载均衡筛选出具体要用的接口 */
        BalancedCalc balancedCalc = BalancedFactory.getBalancedCalc();
        RestApiCacheModel restApiCacheModel = balancedCalc.getRestApiCacheModel(serverName,methodName,restApiCacheModelList);
        return restApiCacheModel;
    }
}
