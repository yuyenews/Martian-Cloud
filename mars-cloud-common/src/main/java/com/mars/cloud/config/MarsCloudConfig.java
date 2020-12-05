package com.mars.cloud.config;

import com.mars.cloud.balanced.BalancedCalc;
import com.mars.cloud.config.model.CloudConfig;
import com.mars.cloud.config.model.FuseConfig;
import com.mars.common.base.config.MarsConfig;

/**
 * Mars-cloud配置
 */
public abstract class MarsCloudConfig extends MarsConfig {

    /**
     * Mars-cloud配置
     * @return 配置
     */
    public abstract CloudConfig getCloudConfig();

    /**
     * 获取负载均衡策略
     * @return
     */
    public BalancedCalc getBalancedCalc(){
        /* 返回null 表示使用默认（普通轮询）策略 */
        return null;
    }

    /**
     * 熔断器配置
     * @return
     */
    public FuseConfig getFuseConfig(){
        return new FuseConfig();
    }
}
