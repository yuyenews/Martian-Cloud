package com.mars.cloud.config.model;

import com.mars.cloud.config.model.enums.Protocol;
import com.mars.cloud.config.model.enums.Strategy;
import com.mars.cloud.fuse.FuseDefault;
import com.mars.cloud.fuse.FuseManager;

public class CloudConfig {
    /**
     * 服务名称，同一个服务的负载均衡集群的name必须一致，不同集群之间必须唯一
     */
    private String name;
    /**
     * 尽量长一点，防止接口过多来不及发布
     */
    private Long sessionTimeout = 10000L;
    /**
     * 请求Mars-Cloud接口超时时间
     */
    private Long timeOut = 10000L;
    /**
     * 负载均衡策略 POLLING 轮询，RANDOM 随机（暂时只支持这两种）
     */
    private Strategy strategy = Strategy.POLLING;
    /**
     * zookeeper地址，多个地址用英文逗号分割，并在外面加一个双引号
     */
    private String register;

    /**
     * 熔断器
     */
    private FuseManager fuseManager = new FuseDefault();

    /**
     * 请求协议
     */
    private Protocol protocol = Protocol.HTTP;

    /**
     * 本服务的IP，不设置的话默认为本机内网IP
     */
    private String ip;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(Long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public Long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Long timeOut) {
        this.timeOut = timeOut;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public FuseManager getFuseManager() {
        return fuseManager;
    }

    public void setFuseManager(FuseManager fuseManager) {
        this.fuseManager = fuseManager;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
