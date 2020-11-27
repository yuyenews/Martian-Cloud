package com.mars.cloud.request.balanced;


import com.mars.cloud.config.model.enums.Strategy;
import com.mars.cloud.request.balanced.impl.BalancedCalcPolling;
import com.mars.cloud.request.balanced.impl.BalancedCalcRandom;
import com.mars.cloud.util.MarsCloudConfigUtil;

/**
 * 负载均衡算法工厂
 */
public class BalancedFactory {

    /**
     * 获取负载均衡算法
     * @return
     */
    public static BalancedCalc getBalancedCalc() throws Exception {
        Object strategy = MarsCloudConfigUtil.getMarsCloudConfig().getCloudConfig().getStrategy();
        if(strategy instanceof BalancedCalc){
            return (BalancedCalc)strategy;
        } else {
            Strategy sty = (Strategy)strategy;
            switch (sty){
                case POLLING:
                    return new BalancedCalcPolling();
                case RANDOM:
                    return new BalancedCalcRandom();
            }
        }
        return new BalancedCalcPolling();
    }
}
