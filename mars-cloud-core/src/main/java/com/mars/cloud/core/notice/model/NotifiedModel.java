package com.mars.cloud.core.notice.model;

/**
 * 接收下线通知的实体
 */
public class NotifiedModel {

    /**
     * 是谁将此服务下线了
     */
    private String serverInfo;

    public String getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }
}
