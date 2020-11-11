package com.mars.cloud.request.balanced.impl;

import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.request.balanced.BalancedCalc;
import com.mars.common.constant.MarsSpace;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询算法
 */
public class BalancedCalcPolling implements BalancedCalc {

    private MarsSpace marsSpace = MarsSpace.getEasySpace();

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
    private int getPollingIndex(String key, int size) {
        Map<String, AtomicInteger> pollingMap = getPollingMap();

        AtomicInteger nowIndex = getNowIndex(pollingMap, key);

        if(nowIndex.get() >= (size - 1)){
            nowIndex.set(0);
            /*
             * 在项目的运行中，有些服务会被下掉，有些服务会减少或者修改接口
             * 这种变动可能会给这个缓存中造成垃圾数据，所以每经过一轮就清除一下
             * 用来防止产生垃圾数据
             */
            pollingMap.remove(key);
        } else {
            nowIndex.getAndIncrement();
        }
        pollingMap.put(key,nowIndex);
        return nowIndex.get();
    }

    /**
     * 获取当前下标
     * @param key 路径
     * @return
     */
    private synchronized AtomicInteger getNowIndex(Map<String, AtomicInteger> pollingMap, String key){
        AtomicInteger nowIndexCache = pollingMap.get(key);
        if(nowIndexCache == null){
            return new AtomicInteger(0);
        }
        return nowIndexCache;
    }

    /**
     * 获取轮询Map
     * @return
     */
    private Map<String, AtomicInteger> getPollingMap(){
        Map<String, AtomicInteger> pollingMap = new ConcurrentHashMap<>();
        Object obj = marsSpace.getAttr(POLLING_MAP);
        if(obj == null){
            marsSpace.setAttr(POLLING_MAP, pollingMap);
        } else {
            pollingMap = (Map<String, AtomicInteger>)obj;
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
