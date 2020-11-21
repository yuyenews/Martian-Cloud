package com.mars.cloud.core.blanced;

import com.mars.cloud.core.cache.ServerApiCacheManager;
import com.mars.cloud.core.cache.model.RestApiCacheModel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询下标管理
 */
public class PollingIndexManager {

    private final static Map<String, AtomicInteger> pollingMap = new ConcurrentHashMap<>();

    /**
     * 初始化轮询下标
     */
    public static void initPollingMap(){
        Map<String, List<RestApiCacheModel>> restApiCacheMap = ServerApiCacheManager.getCacheApisMap();
        for(String key : restApiCacheMap.keySet()){
            if(pollingMap.containsKey(key)){
                continue;
            }
            pollingMap.put(key, new AtomicInteger(0));
        }
    }

    /**
     * 移除下标
     * @param key
     */
    public static void removePolling(String key){
        pollingMap.remove(key);
    }

    /**
     * 轮询算法
     *
     * @return 下标
     */
    public static int getPollingIndex(String key, int size) {
        AtomicInteger nowIndex = pollingMap.get(key);
        if(nowIndex == null){
            nowIndex = new AtomicInteger(0);
        }

        if(nowIndex.get() >= (size - 1)){
            nowIndex.set(0);
        } else {
            nowIndex.getAndIncrement();
        }
        pollingMap.put(key,nowIndex);
        return nowIndex.get();
    }
}
