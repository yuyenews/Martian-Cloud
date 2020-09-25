package com.mars.cloud.config.model;

import com.mars.cloud.fuse.FuseDefault;
import com.mars.cloud.fuse.FuseManager;

/**
 * 熔断器配置
 */
public class FuseConfig {

    /**
     * 失败多少次就熔断
     */
    private Long failNum;

    /**
     * 熔断后被请求多少次后，进入半熔断状态
     */
    private Long fuseNum;

    /**
     * 熔断器
     */
    private FuseManager fuseManager = new FuseDefault();

    public Long getFailNum() {
        return failNum;
    }

    public void setFailNum(Long failNum) {
        this.failNum = failNum;
    }

    public Long getFuseNum() {
        return fuseNum;
    }

    public void setFuseNum(Long fuseNum) {
        this.fuseNum = fuseNum;
    }

    public FuseManager getFuseManager() {
        return fuseManager;
    }

    public void setFuseManager(FuseManager fuseManager) {
        this.fuseManager = fuseManager;
    }
}
