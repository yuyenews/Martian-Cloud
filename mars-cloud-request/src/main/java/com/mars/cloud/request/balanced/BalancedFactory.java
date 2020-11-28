package com.mars.cloud.request.balanced;


import com.mars.cloud.balanced.BalancedCalc;
import com.mars.cloud.request.balanced.impl.BalancedCalcPolling;
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
        BalancedCalc strategy = MarsCloudConfigUtil.getMarsCloudConfig().getCloudConfig().getStrategy();
        if(strategy == null){
            /* 默认轮询 */
            return new BalancedCalcPolling();
        }
        return strategy;
    }
}
