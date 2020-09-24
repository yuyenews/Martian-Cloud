package com.mars.cloud.request.balanced.impl;

import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.request.balanced.BalancedCalc;
import com.mars.server.server.request.HttpMarsContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 轮询算法
 */
public class BalancedCalcPolling implements BalancedCalc {

    private HttpMarsContext httpMarsContext = HttpMarsContext.getHttpContext();

    private final String POLLING_MAP = "pollingMap";

    @Override
    public RestApiCacheModel getRestApiCacheModel(String serverName, String methodName, List<RestApiCacheModel> restApiCacheModelList) {
        String key = getKey(serverName,methodName);

        int index = getPollingIndex(key, restApiCacheModelList.size());
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

    /**
     * 轮询算法
     *
     * @return 下标
     */
    private synchronized int getPollingIndex(String key, int size) {
        Map<String, Integer> pollingMap = getPollingMap();

        int nowIndex = getNowIndex(key);

        if(nowIndex >= (size - 1)){
            nowIndex = 0;
            /*
             * 在项目的运行中，有些服务会被下掉，有些服务会减少或者修改接口
             * 这种变动可能会给这个缓存中造成垃圾数据，所以每经过一轮就清除一下
             * 用来防止产生垃圾数据
             */
            pollingMap.remove(key);
        } else {
            nowIndex++;
        }
        pollingMap.put(key,nowIndex);
        return nowIndex;
    }

    /**
     * 获取当前下标
     * @param key 路径
     * @return
     */
    private int getNowIndex(String key){
        Integer nowIndexCache = getPollingMap().get(key);
        if(nowIndexCache != null){
            return nowIndexCache;
        }
        return 0;
    }

    private Map<String, Integer> getPollingMap(){
        Map<String, Integer> pollingMap = new ConcurrentHashMap<>();
        Object obj = httpMarsContext.getAttr(POLLING_MAP);
        if(obj == null){
            httpMarsContext.setAttr(POLLING_MAP, pollingMap);
        } else {
            pollingMap = (Map<String, Integer>)obj;
        }
        return pollingMap;
    }

    /**
     * 获取key
     * @param serverName
     * @param methodName
     * @return
     */
    private String getKey(String serverName, String methodName){
        return serverName + "-" + methodName;
    }
}
