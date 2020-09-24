package com.mars.cloud.components;

import com.mars.cloud.core.cache.LoadServerCache;
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

    private LoadServerCache loadServerCache = new LoadServerCache();

    /**
     * 10秒刷新一次本地服务缓存
     */
    @MarsTimer(loop = 10000)
    public void doReloadServerCache(){
        try {
            loadServerCache.doLoad();
        } catch (Exception e){
            logger.error("刷新本地服务缓存失败，10秒后将重试", e);
        }
    }
}
