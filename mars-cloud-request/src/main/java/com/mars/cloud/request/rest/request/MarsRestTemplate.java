package com.mars.cloud.request.rest.request;

import com.mars.cloud.annotation.enums.ContentType;
import com.mars.cloud.constant.HttpStatusConstant;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.core.offline.OfflineManager;
import com.mars.cloud.core.vote.VoteManager;
import com.mars.cloud.fuse.FuseFactory;
import com.mars.cloud.model.HttpResultModel;
import com.mars.cloud.request.balanced.BalancedManager;
import com.mars.cloud.request.util.HttpUtil;
import com.mars.cloud.request.util.model.MarsHeader;
import com.mars.cloud.util.SerializableCloudUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * 发起rest请求
 */
public class MarsRestTemplate {

    /**
     * 发起请求,无传参,无header
     *
     * @param serverName serverName
     * @param methodName methodName
     * @return 结果
     * @throws Exception 异常
     */
    public static <T> T request(String serverName, String methodName, Class<T> resultType, ContentType contentType) throws Exception {
        return doRequestResultModel(serverName, methodName, null, resultType, contentType, null);
    }

    /**
     * 发起请求,无传参,有header
     *
     * @param serverName serverName
     * @param methodName methodName
     * @return 结果
     * @throws Exception 异常
     */
    public static <T> T request(String serverName, String methodName, Class<T> resultType, ContentType contentType, MarsHeader marsHeader) throws Exception {
        return doRequestResultModel(serverName, methodName, null, resultType, contentType, marsHeader);
    }

    /**
     * 发起请求,有传参,无header
     *
     * @param serverName serverName
     * @param methodName methodName
     * @param params     params
     * @return 结果
     * @throws Exception 异常
     */
    public static <T> T request(String serverName, String methodName, Object[] params, Class<T> resultType, ContentType contentType) throws Exception {
        return doRequestResultModel(serverName, methodName, params, resultType, contentType, null);
    }

    /**
     * 发起请求,有传参,有header
     *
     * @param serverName serverName
     * @param methodName methodName
     * @param params     params
     * @return 结果
     * @throws Exception 异常
     */
    public static <T> T request(String serverName, String methodName, Object[] params, Class<T> resultType, ContentType contentType, MarsHeader marsHeader) throws Exception {
        return doRequestResultModel(serverName, methodName, params, resultType, contentType, marsHeader);
    }

    /**
     * 发起请求,返回对象
     *
     * @param serverName serverName
     * @param methodName methodName
     * @param params     params
     * @return 结果
     * @throws Exception 异常
     */
    public static <T> T doRequestResultModel(String serverName, String methodName, Object[] params, Class<T> resultType, ContentType contentType, MarsHeader marsHeader) throws Exception {
        HttpResultModel httpResultModel = doRequest(serverName, methodName, params, contentType, marsHeader);
        if (resultType.equals(HttpResultModel.class)) {
            return (T) httpResultModel;
        } else {
            if(httpResultModel.getCode() != HttpStatusConstant.SUCCESS.getCode()){
                throw new Exception("请求失败,状态码:" + httpResultModel.getCode());
            }
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
            /* 获取即将需要的接口 */
            restApiCacheModel = BalancedManager.getRestApiCacheModel(serverName, methodName);

            if (params == null) {
                params = new Object[0];
            }

            /* 判断是否已经被熔断，如果没被熔断，就请求此接口 */
            boolean isFuse = FuseFactory.getFuseManager().isFuse(serverName, methodName, restApiCacheModel.getUrl());
            if (isFuse) {

                /* 发起请求获取响应数据 */
                HttpResultModel httpResultModel = HttpUtil.request(restApiCacheModel, params, contentType, marsHeader);

                /* 如果请求成功，则将下线票清0 */
                OfflineManager.restDisable(restApiCacheModel.getLocalHost());

                /* 请求成功后的熔断器业务逻辑 */
                FuseFactory.getFuseManager().requestSuccess(serverName, methodName, restApiCacheModel.getUrl());

                return httpResultModel;
            } else {
                /* 接口熔断后，如果来请求了，熔断器的业务逻辑 */
                FuseFactory.getFuseManager().fuseAfter(serverName, methodName, restApiCacheModel.getUrl());
                throw new Exception("此接口已被熔断，一段时间后将会重新开放");
            }
        } catch (ConnectException | UnknownHostException | SocketTimeoutException e){
            String eMsg = e.getMessage();
            if(e instanceof SocketTimeoutException
                    && eMsg != null
                    && !eMsg.toUpperCase().startsWith("CONNECT")){
                /* 如果不是connect time out，则忽略，因为如果不是就说明连上了，所以服务是存在的 */
                throw e;
            }
            /* 如果没连接上服务，则投一个下线票 */
            OfflineManager.needOffline(restApiCacheModel.getLocalHost(), restApiCacheModel.getCreateTime());
            throw e;
        } catch (Exception e) {
            /* 请求失败后，熔断器的业务逻辑 */
            FuseFactory.getFuseManager().requestFail(serverName, methodName, restApiCacheModel.getUrl());
            throw e;
        }
    }
}
