package com.mars.cloud.core.vote;

import com.mars.cloud.core.cache.ServerApiCacheManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 投票管理
 */
public class VoteManager {

    private static Map<String, AtomicInteger> voteMap = new ConcurrentHashMap<>();

    /**
     * 加载本地投票列表
     */
    public static void loadVote() throws Exception {
        List<String> restApiHostList = ServerApiCacheManager.getAllServerList(true);
        if(restApiHostList == null){
            return;
        }

        for(String host : restApiHostList){
            if(voteMap.containsKey(host)){
                continue;
            }
            voteMap.put(host, new AtomicInteger(0));
        }
    }

    /**
     * 清空投票数
     * @param host
     */
    public static void clearVote(String host){
        AtomicInteger atomicInteger = voteMap.get(host);
        atomicInteger.set(0);
        voteMap.put(host, atomicInteger);
    }

    /**
     * 新增投票
     * @param host
     */
    public static void addVote(String host){
        AtomicInteger atomicInteger = voteMap.get(host);
        atomicInteger.getAndIncrement();
        voteMap.put(host, atomicInteger);
    }

    /**
     * 获取票数
     * @param host
     * @return
     */
    public static int getVoteNum(String host){
        AtomicInteger atomicInteger = voteMap.get(host);
        return atomicInteger.get();
    }

    /**
     * 移除投票列表
     * @param host
     */
    public static void removeVote(String host){
        voteMap.remove(host);
    }
}
