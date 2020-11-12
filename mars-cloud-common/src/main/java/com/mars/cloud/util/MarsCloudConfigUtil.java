package com.mars.cloud.util;

import com.mars.cloud.config.MarsCloudConfig;
import com.mars.common.base.config.MarsConfig;
import com.mars.common.util.MarsConfiguration;

/**
 * cloud模块配置文件管理
 */
public class MarsCloudConfigUtil {

    /**
     * 获取cloud配置文件
     *
     * @return 配置
     * @throws Exception 异常
     */
    public static MarsCloudConfig getMarsCloudConfig() throws Exception {
        try {
            MarsConfig config = MarsConfiguration.getConfig();
            return (MarsCloudConfig)config;
        } catch (Exception e) {
            throw new Exception("获取cloud配置失败", e);
        }
    }

    /**
     * 获取cloud配置文件中的服务name
     *
     * @return 配置
     * @throws Exception 异常
     */
    public static String getCloudName() throws Exception {
        try {
            MarsCloudConfig cloudConfig = getMarsCloudConfig();
            return cloudConfig.getCloudConfig().getName();
        } catch (Exception e) {
            throw new Exception("获取cloud配置中的服务name失败", e);
        }
    }

    /**
     * 获取下线票数
     * @return
     * @throws Exception
     */
    public static int getMaxVoteNum() {
        try {
            return getMarsCloudConfig().getCloudConfig().getOfflineVote();
        } catch (Exception e){
            return 5;
        }
    }
}
