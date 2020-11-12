package com.mars.cloud.core.offline;

import com.mars.cloud.constant.MarsCloudConstant;
import com.mars.cloud.core.cache.ServerApiCacheManager;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.core.notice.NotifiedManager;
import com.mars.cloud.core.notice.model.NotifiedModel;
import com.mars.cloud.core.vote.VoteManager;
import com.mars.cloud.thread.ThreadPool;
import com.mars.cloud.util.MarsCloudConfigUtil;
import com.mars.cloud.util.MarsCloudUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 下线管理
 */
public class OfflineManager {

    private static Map<String, Long> disableMap = new ConcurrentHashMap<>();

    /**
     * 是否需要下线
     * @param host
     */
    public static void needOffline(String host, Long createTime){
        VoteManager.addVote(host);
        int voteNum = VoteManager.getVoteNum(host);
        int maxVoteNum = MarsCloudConfigUtil.getMaxVoteNum();

        if(voteNum >= maxVoteNum){
            disableMap.put(host, createTime);
        }
    }

    /**
     * 重置禁用状态
     * @param host
     */
    public static void restDisable(String host){
        VoteManager.clearVote(host);
        disableMap.remove(host);
    }

    /**
     * 是否禁用 true禁用
     * @param host
     * @return
     */
    public static boolean isDisable(String host){
        Long disAble = disableMap.get(host);
        if(disAble == null){
            return false;
        }
        return true;
    }

    /**
     * 获取被禁用的api的创建时间
     * @param host
     * @return
     */
    public static Long getDisableTime(String host){
        Long disAble = disableMap.get(host);
        if(disAble == null){
            return 0L;
        }
        return disAble;
    }

    /**
     * 下线已超票数的服务
     * @throws Exception
     */
    public static void doOffline() throws Exception {
        /* 筛选已经下线的host，从本地删除后返回 */
        Set<String> offlineHostList = new HashSet<>();

        Map<String, List<RestApiCacheModel>> restApiCacheMap = ServerApiCacheManager.getCacheApisMap();

        Set<String> onRemoveKeys = new HashSet<>();
        for(String key : restApiCacheMap.keySet()){
            List<RestApiCacheModel> restApiCacheModelList = restApiCacheMap.get(key);
            if(restApiCacheModelList == null || restApiCacheModelList.size() < 1){
                continue;
            }

            List<RestApiCacheModel> removeObj = new ArrayList<>();
            for(RestApiCacheModel restApiCacheModel : restApiCacheModelList){
                if(OfflineManager.isDisable(restApiCacheModel.getLocalHost())){
                    removeObj.add(restApiCacheModel);
                    offlineHostList.add(restApiCacheModel.getLocalHost());

                    NotifiedManager.removeNotified(restApiCacheModel.getLocalHost());
                    VoteManager.removeVote(restApiCacheModel.getLocalHost());
                }
            }
            if(removeObj.size() > 0){
                restApiCacheModelList.removeAll(removeObj);
            }

            if(restApiCacheModelList == null || restApiCacheModelList.size() < 1){
                onRemoveKeys.add(key);
            }
            restApiCacheMap.put(key, restApiCacheModelList);
        }
        /* 清理value长度为0的元素 */
        for(String key : onRemoveKeys){
            restApiCacheMap.remove(key);
        }

        /* 给下线的服务发通知 */
        for(String offlineHost : offlineHostList){

            NotifiedModel notifiedModel = new NotifiedModel();
            notifiedModel.setServerInfo(MarsCloudUtil.getLocalHost());
            noticeOfflineServer(offlineHost, notifiedModel);
        }
    }

    /**
     * 通知已下线的服务
     * 因为可能是误判，所以将服务下线后，需要通知他，让他把我从已通知列表移除
     * 只有让他把我从已通知列表移除，那么如果真的是误判的话，那么他下次还会给我发广播，让我知道他
     * @param offlineHost
     * @param notifiedModel
     */
    private static void noticeOfflineServer(String offlineHost, NotifiedModel notifiedModel){
        String key = MarsCloudConstant.OFFLINE_NOTICE + offlineHost;

        if(NotifiedManager.isNotified(key)){
            return;
        }

        OfflineNoticeThread offlineNoticeThread = new OfflineNoticeThread();
        offlineNoticeThread.setOfflineHost(offlineHost);
        offlineNoticeThread.setNotifiedModel(notifiedModel);
        offlineNoticeThread.setKey(key);
        ThreadPool.getThreadPoolExecutor().execute(offlineNoticeThread);

    }
}
