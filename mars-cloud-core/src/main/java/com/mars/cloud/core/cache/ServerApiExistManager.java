package com.mars.cloud.core.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 记录哪些服务的接口已经感染过了
 */
public class ServerApiExistManager {

    private static Map<String, String> existApiMap = new HashMap<>();

    /**
     * 是否存在
     * @param localHost
     * @return
     */
    public static boolean hasExist(String localHost){
        return existApiMap.get(localHost) != null;
    }

    /**
     * 添加
     * @param localHost
     */
    public static void add(String localHost){
        if(!existApiMap.containsKey(localHost)){
            existApiMap.put(localHost,"ok");
        }

    }

    /**
     * 移除
     * @param localHost
     */
    public static void remove(String localHost){
        if(existApiMap.containsKey(localHost)){
            existApiMap.remove(localHost);
        }
    }
}
