package com.mars.cloud.core.notice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知管理，保存已经被通知过的服务
 */
public class NotifiedManager {

    /**
     * 已经被通知过的服务
     */
    private static Map<String, String> notified = new ConcurrentHashMap<>();

    /**
     * 添加已经被通知过的服务
     * @param url
     */
    public static void addNotified(String url){
        notified.put(url, "o");
    }

    /**
     * 删除已经被通知过的服务
     * @param url
     */
    public static void removeNotified(String url){
        notified.remove(url);
    }

    /**
     * 判断此服务有没有被通知过了
     * @param url
     * @return
     */
    public static boolean isNotified(String url){
        return notified.get(url) != null;
    }
}
