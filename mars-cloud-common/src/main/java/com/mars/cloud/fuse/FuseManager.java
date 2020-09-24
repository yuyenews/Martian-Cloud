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
     * 添加错误次数，只要成功一次就从0开始计算
     * @param serverName
     * @param methodName
     * @param url
     * @throws Exception
     */
    void addFailNum(String serverName, String methodName, String url) throws Exception;

    /**
     * 添加熔断后的请求次数
     * @param serverName
     * @param methodName
     * @param url
     * @throws Exception
     */
    void addFuseNum(String serverName, String methodName, String url) throws Exception;

    /**
     * 清空请求失败次数
     * @param serverName
     * @param methodName
     * @param url
     * @throws Exception
     */
    void clearFailNum(String serverName, String methodName, String url) throws Exception;
}
