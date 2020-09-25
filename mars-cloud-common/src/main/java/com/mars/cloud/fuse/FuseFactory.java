package com.mars.cloud.fuse;

import com.mars.cloud.util.MarsCloudConfigUtil;

/**
 * 熔断器工厂
 */
public class FuseFactory {

    private static FuseManager fuseManager;

    /**
     * 获取熔断器
     * @return
     * @throws Exception
     */
    public static FuseManager getFuseManager() throws Exception {
        if(fuseManager == null){
            fuseManager = MarsCloudConfigUtil.getMarsCloudConfig().getFuseConfig().getFuseManager();
        }
        return fuseManager;
    }
}
