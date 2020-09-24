package com.mars.cloud.util;

import com.mars.cloud.config.model.enums.Protocol;
import com.mars.common.util.MarsAddressUtil;
import com.mars.common.util.StringUtil;

/**
 * 工具类
 */
public class MarsCloudUtil {

    /**
     * 本机接口的完整请求前缀
     */
    private static String localHost;

    /**
     * 获取本机接口的完整请求前缀
     *
     * @return localhost
     * @throws Exception 异常
     */
    public static String getLocalHost() throws Exception {
        if (localHost == null) {
            Protocol protocol = MarsCloudConfigUtil.getMarsCloudConfig().getCloudConfig().getProtocol();
            localHost = protocol.getCode() + getLocalIp() + ":" + getPort();
        }
        return localHost;
    }

    /**
     * 获取本机的IP
     *
     * @return ip
     * @throws Exception 异常
     */
    public static String getLocalIp() throws Exception {
        String ip = MarsCloudConfigUtil.getMarsCloudConfig().getCloudConfig().getIp();
        if(StringUtil.isNull(ip)){
            return MarsAddressUtil.getLocalIp();
        }
        return ip;
    }

    /**
     * 获取端口号
     *
     * @return 端口号
     */
    public static String getPort() {
        return String.valueOf(MarsAddressUtil.getPort());
    }
}
