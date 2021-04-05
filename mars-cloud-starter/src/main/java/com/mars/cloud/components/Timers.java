package com.mars.cloud.components;

import com.mars.cloud.core.cache.ServerApiCacheManager;
import com.mars.cloud.core.notice.MartianNotice;
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
     * 200毫秒清理一次过期的接口
     */
    @MarsTimer(loop = 200)
    public void offline(){
        try {
            ServerApiCacheManager.doOffline();
        } catch (Exception e){
            logger.error("清理本地过期接口异常，200毫秒后将重试", e);
        }
    }
}
