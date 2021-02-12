package com.mars.cloud.util;

import com.mars.cloud.constant.HttpStatusConstant;
import com.mars.cloud.model.HttpResultModel;
import okhttp3.*;

import java.util.concurrent.TimeUnit;

public class HttpCommons {

    /**
     * okHttpClient
     */
    private static OkHttpClient okHttpClient;

    /**
     * 开始请求
     *
     * @param okHttpClient 客户端
     * @param request      请求
     * @return 结果
     * @throws Exception 异常
     */
    public static HttpResultModel okCall(OkHttpClient okHttpClient, Request request) throws Exception {
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();

        HttpResultModel httpResultModel = new HttpResultModel();
        httpResultModel.setCode(response.code());

        if (httpResultModel.getCode() != HttpStatusConstant.SUCCESS.getCode()) {
            return httpResultModel;
        }

        ResponseBody responseBody = response.body();
        String head = response.header("Content-Disposition");

        httpResultModel.setFileName(head);
        httpResultModel.setResponseBody(responseBody);
        return httpResultModel;
    }

    /**
     * 获取okHttp客户端
     *
     * @return 客户端
     * @throws Exception 异常
     */
    public static OkHttpClient getOkHttpClient() throws Exception {
        if(okHttpClient == null){
            long timeOut = getTimeOut();
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
                    .readTimeout(timeOut, TimeUnit.MILLISECONDS)
                    .build();
        }
        return okHttpClient;
    }

    /**
     * 从配置中获取超时时间
     *
     * @return
     */
    private static long getTimeOut() {
        try {
            return MarsCloudConfigUtil.getMarsCloudConfig().getCloudConfig().getTimeOut();
        } catch (Exception e) {
            return 5000L;
        }
    }
}
