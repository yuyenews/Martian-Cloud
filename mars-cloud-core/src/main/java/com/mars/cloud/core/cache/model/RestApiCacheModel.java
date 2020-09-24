package com.mars.cloud.core.cache.model;

import com.mars.common.annotation.enums.ReqMethod;

/**
 * rest接口实体类
 */
public class RestApiCacheModel {

    /**
     * url
     */
    private String url;

    /**
     * 请求方式
     */
    private ReqMethod reqMethod;

    /**
     * 方法名称
     */
    private String methodName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
}
