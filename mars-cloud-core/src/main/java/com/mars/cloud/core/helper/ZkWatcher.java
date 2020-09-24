package com.mars.cloud.core.helper;

import com.mars.cloud.core.cache.LoadServerCache;
import com.mars.cloud.core.register.Registered;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * 监视器
 */
public class ZkWatcher implements Watcher {

    private static Logger marsLogger = LoggerFactory.getLogger(ZkWatcher.class);

    private CountDownLatch countDownLatch;

    public ZkWatcher(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    /**
     * 监视zk的状态
     *
     * @param event 事件
     */
    @Override
    public void process(WatchedEvent event) {
        try {
            switch (event.getState()) {
                case Expired:
                    /* 超时重连 */
                    reConnectionZookeeper();
                    return;
                case SyncConnected:
                    /* 连接成功 */
                    countDownLatch.countDown();
                    return;
            }

            /* 注册中心的接口有了变化，就刷新本地缓存 */
            switch (event.getType()) {
                case NodeChildrenChanged:
                case NodeCreated:
                case NodeDeleted:
                case NodeDataChanged:
                    /* 刷新本地缓存 */
                    refreshCacheApi();
                    return;
            }
        } catch (Exception e) {
            marsLogger.error("处理zookeeper的通知时出错", e);
        }
    }

    /**
     * 重连zookeeper
     */
    private void reConnectionZookeeper() {
        while (true) {
            try {
                marsLogger.info("zookeeper连接已断开，正在重新连接并注册接口");

                /* 重连并注册 */
                ZkHelper.openConnection();
                Registered registered = new Registered();
                registered.doRegister();

                /* 刷新本地缓存 */
                refreshCacheApi();
                return;
            } catch (Exception e) {
                /* 重连失败就 再连接一次，务必保证重连成功 */
                marsLogger.error("zookeeper重连失败，即将重试", e);
                try {
                    Thread.sleep(1000);
                } catch (Exception e2){
                }
                continue;
            }
        }
    }

    /**
     * 刷新本地API缓存
     */
    private void refreshCacheApi() {
        try {
            LoadServerCache loadServerCache = new LoadServerCache();
            loadServerCache.doLoad();
            return;
        } catch (Exception e) {
            /*
             * 如果出异常了，由于被捕获，所以程序不会停掉
             * 而且还有一个定时任务在做补偿，15秒后会再刷新一次
             * 所以这里什么都不干，默默的就好
             */
        }
    }
}