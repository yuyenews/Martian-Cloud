package com.mars.cloud.config.model.enums;

/**
 * 负载均衡策略
 */
public enum Strategy {

    POLLING("1","轮询"), RANDOM("2","随机");

    String code;
    String label;

    Strategy(String code,String label){
        this.code = code;
        this.label = label;
    }

    public static Strategy getStrategyByCode(String code){
        Strategy[] strategies = values();
        for(Strategy strategy : strategies){
            if(strategy.code.equals(code)){
                return strategy;
            }
        }
        return null;
    }
}
