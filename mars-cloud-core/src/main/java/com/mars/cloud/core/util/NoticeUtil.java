package com.mars.cloud.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mars.cloud.constant.HttpStatusConstant;
import com.mars.cloud.constant.MarsCloudConstant;
import com.mars.cloud.model.RestApiCacheModel;
import com.mars.cloud.core.notice.model.NotifiedModel;
import com.mars.cloud.core.notice.model.RestApiModel;
import com.mars.cloud.model.HttpResultModel;
import com.mars.cloud.util.HttpCommons;
import com.mars.cloud.util.RandomUtil;
import com.mars.cloud.util.SerializableCloudUtil;
import com.mars.common.util.StringUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 广播工具类
 */
public class NoticeUtil {

    private static Logger marsLogger = LoggerFactory.getLogger(NoticeUtil.class);

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

        if (httpResultModel.getCode() != HttpStatusConstant.SUCCESS.getCode()){
            return null;
        }

        if(StringUtil.isNull(httpResultModel.getFileName())) {
            /*
                由于微服务中可能会存在一些跟前端直接交互的聚合服务（小型微服务比较常用这种方案来充当网关），
                这些服务不能序列化响应，只能响应json，这里是为了兼容这些服务
            */
            String result = httpResultModel.getJSONString();
            if(!StringUtil.isNull(result)){
                return jsonToMap(result);
            }
        } else {
            /* 正常的微服务都是序列化返回，所以这里直接反序列化即可 */
            return SerializableCloudUtil.deSerialization(httpResultModel.getInputStream(), ConcurrentHashMap.class);
        }

        return null;
    }

    /**
     * 将返回的json字符串转成Map
     * @param result
     * @return
     */
    private static Map<String, List<RestApiCacheModel>> jsonToMap(String result){
        if(result == null){
            return null;
        }
        Map<String, List<RestApiCacheModel>> restApiCacheMap = new ConcurrentHashMap<>();

        JSONObject jsonObject = JSON.parseObject(result);
        for(String key : jsonObject.keySet()){
            JSONArray item = jsonObject.getJSONArray(key);
            if(item == null || item.size() < 1){
                continue;
            }
            List<RestApiCacheModel> restApiCacheModelList = new ArrayList<>();
            for(int i=0;i < item.size(); i++){
                JSONObject jsonItem = item.getJSONObject(i);
                if(jsonItem == null){
                    continue;
                }
                restApiCacheModelList.add(jsonItem.toJavaObject(RestApiCacheModel.class));
            }
            restApiCacheMap.put(key, restApiCacheModelList);
        }
        return restApiCacheMap;
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
            return doNotice(url, jsonStrParam);
        } catch (Exception e) {
            marsLogger.warn("发送广播异常");
            return false;
        }
    }

    /**
     * 通知被下线的服务，让他把我从已广播列表移除，防止误判
     * @param url
     * @param notifiedModel
     * @return
     */
    public static boolean removeNotified(String url, NotifiedModel notifiedModel) {
        try {
            String jsonStrParam = "{}";
            if (notifiedModel != null) {
                jsonStrParam = JSON.toJSONString(notifiedModel);
            }
            return doNotice(url, jsonStrParam);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 发送通知
     * @param url
     * @param jsonStrParam
     * @throws Exception
     */
    private static boolean doNotice(String url, String jsonStrParam) throws Exception {
        OkHttpClient okHttpClient = HttpCommons.getOkHttpClient();

        MediaType mediaType = MediaType.parse(MarsCloudConstant.CONTENT_TYPE_JSON);
        RequestBody requestbody = RequestBody.create(jsonStrParam, mediaType);

        Request.Builder builder = new Request.Builder();
        builder.post(requestbody);

        Request request = builder.url(url).build();

        HttpResultModel httpResultModel = HttpCommons.okCall(okHttpClient, request);
        return isSuccess(httpResultModel);
    }

    /**
     * 是否成功
     * @param httpResultModel
     * @return
     * @throws Exception
     */
    private static boolean isSuccess(HttpResultModel httpResultModel) throws Exception {
        if (httpResultModel.getCode() != HttpStatusConstant.SUCCESS.getCode()){
            return false;
        }

        if(StringUtil.isNull(httpResultModel.getFileName())) {
            /*
                由于微服务中可能会存在一些跟前端直接交互的聚合服务（小型微服务比较常用这种方案来充当网关），
                这些服务不能序列化响应，只能响应json，这里是为了兼容这些服务
            */
            String result = httpResultModel.getJSONString();
            if(!StringUtil.isNull(result) && result.equals(MarsCloudConstant.RESULT_SUCCESS)){
                return true;
            }
        } else {
            /* 正常的微服务都是序列化返回，所以这里直接反序列化即可 */
            String result = SerializableCloudUtil.deSerialization(httpResultModel.getInputStream(), String.class);
            if (result.equals(MarsCloudConstant.RESULT_SUCCESS)) {
                return true;
            }
        }
        return false;
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
