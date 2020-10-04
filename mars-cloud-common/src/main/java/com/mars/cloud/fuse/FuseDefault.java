package com.mars.cloud.fuse;

/**
 * 默认的熔断器，用于在没配置熔断器的时候用
 * 其实就是不熔断，仅仅只是为了FuseFactory不返回null
 */
public class FuseDefault implements FuseManager {

    @Override
    public boolean isFuse(String serverName, String methodName, String url) throws Exception {
        return true;
    }

    @Override
    public void requestSuccess(String serverName, String methodName, String url) throws Exception {

    }

    @Override
    public void requestFail(String serverName, String methodName, String url) throws Exception {

    }

    @Override
    public void fuseAfter(String serverName, String methodName, String url) throws Exception {

    }
}
