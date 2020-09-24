package com.mars.cloud.constant;

/**
 * 常量
 */
public class MarsCloudConstant {

    /**
     * 存储api接口的根目录
     */
    public static final String BASE_SERVER_NODE = "/mars-cloud";

    /**
     * 存储api接口的serverName目录
     */
    public static final String SERVER_NODE = BASE_SERVER_NODE + "/{serverName}->{method}";

    /**
     * 存储api接口的节点
     */
    public static final String API_SERVER_NODE = SERVER_NODE + "/{ip}-{port}";

    /**
     * 接受cloud传参的key
     */
    public static final String PARAM = "marsCloudParam";
}
