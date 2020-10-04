package com.mars.cloud.fuse;

public interface FuseManager {

    /**
     * 是否已经熔断
     * @param serverName
     * @param methodName
     * @param url
     * @return
     * @throws Exception
     */
    boolean isFuse(String serverName, String methodName, String url) throws Exception;

    /**
     * 请求成功
     * @param serverName
     * @param methodName
     * @param url
     * @throws Exception
     */
    void requestSuccess(String serverName, String methodName, String url) throws Exception;

    /**
     * 请求失败
     * @param serverName
     * @param methodName
     * @param url
     * @throws Exception
     */
    void requestFail(String serverName, String methodName, String url) throws Exception;

    /**
     * 接口熔断后
     * @param serverName
     * @param methodName
     * @param url
     * @throws Exception
     */
    void fuseAfter(String serverName, String methodName, String url) throws Exception;
    
}
