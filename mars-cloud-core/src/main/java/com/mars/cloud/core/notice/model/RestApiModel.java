package com.mars.cloud.core.notice.model;

import com.mars.cloud.model.RestApiCacheModel;

import java.util.List;

/**
 * 接收广播的实体类
 */
public class RestApiModel {

    private String serverName;

    private List<RestApiCacheModel> restApiCacheModels;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public List<RestApiCacheModel> getRestApiCacheModels() {
        return restApiCacheModels;
    }

    public void setRestApiCacheModels(List<RestApiCacheModel> restApiCacheModels) {
        this.restApiCacheModels = restApiCacheModels;
    }
}
