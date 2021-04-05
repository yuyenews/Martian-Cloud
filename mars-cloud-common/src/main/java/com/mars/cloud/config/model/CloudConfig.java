package com.mars.cloud.config.model;

import com.mars.cloud.config.model.enums.Protocol;

public class CloudConfig {
    /**
     * 服务名称，同一个服务的负载均衡集群的name必须一致，不同集群之间必须唯一
     */
    private String name;
    /**
     * 请求Mars-Cloud接口超时时间
     */
    private Long timeOut = 10000L;
    /**
     * 传染渠道，多个地址用英文逗号分割，并在外面加一个双引号
     */
    private String contagions;
    /**
     * 请求协议
     */
    private Protocol protocol = Protocol.HTTP;

    /**
     * 本服务的IP，不设置的话默认为本机内网IP
     */
    private String ip;

    /**
     * 接口缓存超时时间
     */
    private int apiCacheTimeout = 5000;

    /**
     * 是否作为一个网关
     */
    private boolean gateWay = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Long timeOut) {
        this.timeOut = timeOut;
    }

    public String getContagions() {
        return contagions;
    }

    public void setContagions(String contagions) {
        this.contagions = contagions;
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

    public int getApiCacheTimeout() {
        return apiCacheTimeout;
    }

    public void setApiCacheTimeout(int apiCacheTimeout) {
        if(apiCacheTimeout < 5000){
            apiCacheTimeout = 5000;
        }
        this.apiCacheTimeout = apiCacheTimeout;
    }

    public boolean isGateWay() {
        return gateWay;
    }

    public void setGateWay(boolean gateWay) {
        this.gateWay = gateWay;
    }
}
