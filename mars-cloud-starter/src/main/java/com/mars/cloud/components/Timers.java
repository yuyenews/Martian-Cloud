package com.mars.cloud.components;

import com.mars.cloud.core.notice.MartianNotice;
import com.mars.cloud.core.offline.OfflineManager;
import com.mars.common.annotation.bean.MarsBean;
import com.mars.common.annotation.bean.MarsTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时器合集
 */
@MarsBean
public class Timers {

    private Logger logger = LoggerFactory.getLogger(Timers.class);

    /**
     * 通知器
     */
    private MartianNotice martianNotice = new MartianNotice();

    /**
     * 3秒传染一次
     */
    @MarsTimer(loop = 3000)
    public void doNotice(){
        try {
            martianNotice.notice();
        } catch (Exception e){
            logger.error("刷新本地服务缓存失败，10秒后将重试", e);
        }
    }

    /**
     * 因为禁用可能存在误判，所以做了个补偿机制
     * 200毫秒通知一次被禁用的服务，让他把我从已广播列表移除，方便下次给我发广播
     */
    @MarsTimer(loop = 200)
    public void offline(){
        try {
            OfflineManager.doOffline();
        } catch (Exception e){
            logger.error("清理本地过期接口异常，200毫秒后将重试", e);
        }
    }
}
