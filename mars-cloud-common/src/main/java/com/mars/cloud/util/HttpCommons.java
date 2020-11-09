package com.mars.cloud.util;

import com.mars.cloud.model.HttpResultModel;
import okhttp3.*;

import java.util.concurrent.TimeUnit;

public class HttpCommons {

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

        int code = response.code();
        ResponseBody responseBody = response.body();
        if (code != 200) {
            throw new Exception("请求接口出现异常:" + responseBody.string());
        }
        HttpResultModel httpResultModel = new HttpResultModel();
        String head = response.header("Content-Disposition");

        httpResultModel.setFileName(head);
        httpResultModel.setInputStream(responseBody.byteStream());

        return httpResultModel;
    }

    /**
     * 获取okHttp客户端
     *
     * @return 客户端
     * @throws Exception 异常
     */
    public static OkHttpClient getOkHttpClient() throws Exception {
        long timeOut = getTimeOut();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(timeOut, TimeUnit.SECONDS)//设置读取超时时间
                .build();
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
            return 100L;
        }
    }
}
