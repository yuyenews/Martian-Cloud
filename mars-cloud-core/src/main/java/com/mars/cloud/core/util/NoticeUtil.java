package com.mars.cloud.core.util;

import com.alibaba.fastjson.JSON;
import com.mars.cloud.constant.MarsCloudConstant;
import com.mars.cloud.core.cache.ServerApiCache;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.core.notice.model.RestApiModel;
import com.mars.cloud.model.HttpResultModel;
import com.mars.cloud.util.HttpCommons;
import com.mars.cloud.util.RandomUtil;
import com.mars.cloud.util.SerializableCloudUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 广播工具类
 */
public class NoticeUtil {

    private static Logger marsLogger = LoggerFactory.getLogger(NoticeUtil.class);

    private static ServerApiCache serverApiCache = new ServerApiCache();

    /**
     * 获取接口地址
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static Map<String, List<RestApiCacheModel>> getApis(String url) throws Exception {

        OkHttpClient okHttpClient = HttpCommons.getOkHttpClient();

        Request.Builder builder = new Request.Builder();
        builder.get();
        Request request = builder.url(url).build();

        HttpResultModel httpResultModel = HttpCommons.okCall(okHttpClient, request);
        Map<String, List<RestApiCacheModel>> restApiCacheModelMap = SerializableCloudUtil.deSerialization(httpResultModel.getInputStream(), ConcurrentHashMap.class);

        return restApiCacheModelMap;
    }

    /**
     * 发布广播，将接口通知到所有的微服务
     *
     * @param url
     * @param restApiModel
     * @return
     * @throws Exception
     */
    public static boolean addApis(String url, RestApiModel restApiModel) {
        try {
            String jsonStrParam = "{}";
            if (restApiModel != null) {
                jsonStrParam = JSON.toJSONString(restApiModel);
            }
            OkHttpClient okHttpClient = HttpCommons.getOkHttpClient();

            MediaType mediaType = MediaType.parse(MarsCloudConstant.CONTENT_TYPE_JSON);
            RequestBody requestbody = RequestBody.create(jsonStrParam, mediaType);

            Request.Builder builder = new Request.Builder();
            builder.post(requestbody);

            Request request = builder.url(url).build();

            HttpCommons.okCall(okHttpClient, request);
            return true;
        } catch (Exception e) {
            marsLogger.warn("发送广播异常");
            return false;
        }
    }

    /**
     * 获取随机URL
     * @param urlList
     * @return
     */
    public static String getRandomUrl(List<String> urlList){
        int index = RandomUtil.getIndex(urlList.size());
        return urlList.get(index);
    }

    /**
     * 获取随机URL
     * @param urlArray
     * @return
     */
    public static String getRandomUrl(String[] urlArray){
        int index = RandomUtil.getIndex(urlArray.length);
        return urlArray[index];
    }

}
