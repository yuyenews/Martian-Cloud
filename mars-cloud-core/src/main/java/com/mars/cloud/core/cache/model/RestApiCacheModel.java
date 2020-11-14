package com.mars.cloud.core.cache.model;

import com.mars.common.annotation.enums.ReqMethod;

import java.io.Serializable;

/**
 * rest接口实体类
 */
public class RestApiCacheModel implements Serializable {

    private static final long serialVersionUID = 1668642658421887547L;

    /**
     * url
     */
    private String url;

    /**
     * ip:port
     */
    private String localHost;

    /**
     * 请求方式
     */
    private ReqMethod reqMethod;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 创建时间
     */
    private long createTime;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalHost() {
        return localHost;
    }

    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    public ReqMethod getReqMethod() {
        return reqMethod;
    }

    public void setReqMethod(ReqMethod reqMethod) {
        this.reqMethod = reqMethod;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
