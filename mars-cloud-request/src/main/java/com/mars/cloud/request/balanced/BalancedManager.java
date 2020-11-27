package com.mars.cloud.request.balanced;

import com.mars.cloud.core.cache.ServerApiCacheManager;
import com.mars.cloud.model.RestApiCacheModel;
import com.mars.cloud.core.offline.OfflineManager;

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

        /* 如果没下线，则直接返回 */
        if(!OfflineManager.isDisable(restApiCacheModel.getLocalHost())){
            return restApiCacheModel;
        }

        /*
            如果下线了，则重试N次，
            这个属于小概率事件，当服务下线了，但是还没被从缓存中清理掉，才会执行到这一步，
            清理缓存的频率为200毫秒一次，所以很快的，几乎不会到走到这里
        */
        for(int i=0; i < restApiCacheModelList.size(); i++){
            restApiCacheModel = balancedCalc.getRestApiCacheModel(serverName,methodName,restApiCacheModelList);
            if(!OfflineManager.isDisable(restApiCacheModel.getLocalHost())){
                return restApiCacheModel;
            }
        }

        throw new UnknownHostException("服务已经下线了:" + restApiCacheModel.getLocalHost());
    }
}
