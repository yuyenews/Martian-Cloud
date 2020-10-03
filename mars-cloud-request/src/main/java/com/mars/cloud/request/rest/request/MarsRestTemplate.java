package com.mars.cloud.request.rest.request;

import com.mars.cloud.annotation.enums.ContentType;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.fuse.FuseFactory;
import com.mars.cloud.request.balanced.BalancedManager;
import com.mars.cloud.request.util.HttpUtil;
import com.mars.cloud.request.util.model.HttpResultModel;
import com.mars.cloud.request.util.model.MarsHeader;
import com.mars.cloud.util.SerializableCloudUtil;

/**
 * 发起rest请求
 */
public class MarsRestTemplate {

    /**
     * 发起请求,无header
     *
     * @param serverName serverName
     * @param methodName methodName
     * @return 结果
     * @throws Exception 异常
     */
    public static <T> T request(String serverName, String methodName, Class<T> resultType, ContentType contentType) throws Exception {
        return request(serverName, methodName, resultType, contentType, null);
    }

    /**
     * 发起请求,有header
     *
     * @param serverName serverName
     * @param methodName methodName
     * @return 结果
     * @throws Exception 异常
     */
    public static <T> T request(String serverName, String methodName, Class<T> resultType, ContentType contentType, MarsHeader marsHeader) throws Exception {
        return request(serverName, methodName, null, resultType, contentType, marsHeader);
    }

    /**
     * 发起请求,无header
     *
     * @param serverName serverName
     * @param methodName methodName
     * @param params     params
     * @return 结果
     * @throws Exception 异常
     */
    public static <T> T request(String serverName, String methodName, Object[] params, Class<T> resultType, ContentType contentType) throws Exception {
        return request(serverName, methodName, params, resultType, contentType, null);
    }

    /**
     * 发起请求,有header
     *
     * @param serverName serverName
     * @param methodName methodName
     * @param params     params
     * @return 结果
     * @throws Exception 异常
     */
    public static <T> T request(String serverName, String methodName, Object[] params, Class<T> resultType, ContentType contentType, MarsHeader marsHeader) throws Exception {
        HttpResultModel httpResultModel = doRequest(serverName, methodName, params, contentType, marsHeader);
        if (resultType.equals(HttpResultModel.class)) {
            return (T) httpResultModel;
        } else {
            return SerializableCloudUtil.deSerialization(httpResultModel.getInputStream(), resultType);
        }
    }


    /**
     * 发起请求
     *
     * @param serverName serverName
     * @param methodName methodName
     * @param params     params
     * @return 结果
     * @throws Exception 异常
     */
    private static HttpResultModel doRequest(String serverName, String methodName, Object[] params, ContentType contentType, MarsHeader marsHeader) throws Exception {
        RestApiCacheModel restApiCacheModel = null;
        try {

            restApiCacheModel = BalancedManager.getRestApiCacheModel(serverName, methodName);

            if (params == null) {
                params = new Object[0];
            }

            /* 判断是否已经被熔断，如果没被熔断，就请求此接口 */
            boolean isFuse = FuseFactory.getFuseManager().isFuse(serverName, methodName, restApiCacheModel.getUrl());
            if (isFuse) {
                HttpResultModel httpResultModel = HttpUtil.request(restApiCacheModel, params, contentType, marsHeader);
                /* 由于要连续请求失败到一定次数，才会熔断，所以请求成功就清除错误次数 */
                FuseFactory.getFuseManager().clearFailNum(serverName, methodName, restApiCacheModel.getUrl());

                return httpResultModel;
            } else {
                /* 如果熔断了就拒绝请求，并记录拒绝次数，让熔断器来判断是否进入半熔断状态 */
                FuseFactory.getFuseManager().addFuseNum(serverName, methodName, restApiCacheModel.getUrl());
                throw new Exception("此接口已被熔断，一段时间后将会重新开放");
            }
        } catch (Exception e) {
            /* 如果请求失败，就记录次数，用来给熔断器判断 是否进入熔断状态 */
            FuseFactory.getFuseManager().addFailNum(serverName, methodName, restApiCacheModel.getUrl());
            throw new Exception("发起请求出现异常,url:[" + restApiCacheModel.getUrl() + "],", e);
        }
    }
}
