package com.mars.cloud.request.util;

import com.alibaba.fastjson.JSONObject;
import com.mars.cloud.annotation.enums.ContentType;
import com.mars.cloud.constant.MarsCloudConstant;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.model.HttpResultModel;
import com.mars.cloud.request.rest.model.RequestParamModel;
import com.mars.cloud.request.util.model.MarsHeader;
import com.mars.cloud.util.HttpCommons;
import com.mars.common.annotation.enums.ReqMethod;
import com.mars.common.constant.MarsConstant;
import com.mars.common.util.StringUtil;
import com.mars.server.server.request.model.MarsFileUpLoad;
import okhttp3.*;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * HTTP请求工具类
 */
public class HttpUtil {

    private static final String CONTENT_TYPE_STR = "Content-type".toUpperCase();

    /**
     * 发起请求
     *
     * @param restApiCacheModel
     * @param params
     * @return
     */
    public static HttpResultModel request(RestApiCacheModel restApiCacheModel, Object[] params, ContentType contentType, MarsHeader marsHeader) throws Exception {
        if (contentType == null) {
            throw new Exception("必须指定ContentType");
        }
        if (restApiCacheModel.getReqMethod().equals(ReqMethod.GET) && !contentType.equals(ContentType.FORM)) {
            throw new Exception("请求的接口，请求方式为GET，所以ContentType只能为FORM，接口名:" + restApiCacheModel.getUrl());
        }
        if (restApiCacheModel.getReqMethod().equals(ReqMethod.GET)) {
            return formGet(restApiCacheModel, params, marsHeader);
        } else {
            if (contentType.equals(ContentType.FORM)) {
                return formPost(restApiCacheModel, params, marsHeader);
            } else if (contentType.equals(ContentType.FORM_DATA)) {
                return formData(restApiCacheModel, params, marsHeader);
            } else if (contentType.equals(ContentType.JSON)) {
                return json(restApiCacheModel, params, marsHeader);
            } else {
                throw new Exception("请求的接口ContentType未知，接口名:" + restApiCacheModel.getUrl());
            }
        }
    }

    /**
     * formData提交
     *
     * @param restApiModel
     * @param params
     * @return
     * @throws Exception
     */
    private static HttpResultModel formData(RestApiCacheModel restApiModel, Object[] params, MarsHeader marsHeader) throws Exception {

        OkHttpClient okHttpClient = HttpCommons.getOkHttpClient();

        /* 发起post请求 将数据传递过去 */
        MediaType formData = MediaType.parse(MarsCloudConstant.FORM_DATA);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(formData);

        /* 将参数转成统一规格的对象，进行下面的传参操作 */
        Map<String, RequestParamModel> requestParamModelMap = ParamConversionUtil.getRequestParamModelList(params);
        for (RequestParamModel requestParamModel : requestParamModelMap.values()) {
            if (requestParamModel.isFile()) {
                // 如果是文件
                Map<String, MarsFileUpLoad> marsFileUpLoadMap = requestParamModel.getMarsFileUpLoads();

                for (MarsFileUpLoad marsFileUpLoad : marsFileUpLoadMap.values()) {
                    byte[] file = ParamConversionUtil.toByteArray(marsFileUpLoad.getInputStream());

                    RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));
                    builder.addFormDataPart(marsFileUpLoad.getName(), marsFileUpLoad.getFileName(), fileBody);
                }
            } else {
                // 如果不是文件
                Object val = requestParamModel.getValue();
                if (val == null) {
                    continue;
                }
                if (val instanceof String[]) {
                    String[] valStr = (String[]) val;
                    for (String str : valStr) {
                        builder.addFormDataPart(requestParamModel.getName(), str);
                    }
                } else {
                    builder.addFormDataPart(requestParamModel.getName(), val.toString());
                }
            }
        }
        Request request = getRequestBuilder(restApiModel, builder.build(), marsHeader)
                .url(restApiModel.getUrl())
                .build();

        return HttpCommons.okCall(okHttpClient, request);
    }

    /**
     * post表单提交
     *
     * @param restApiModel
     * @param params
     * @return
     */
    private static HttpResultModel formPost(RestApiCacheModel restApiModel, Object[] params, MarsHeader marsHeader) throws Exception {
        OkHttpClient okHttpClient = HttpCommons.getOkHttpClient();

        JSONObject jsonParam = ParamConversionUtil.conversionToJson(params);

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (String key : jsonParam.keySet()) {
            Object val = jsonParam.get(key);
            if (StringUtil.isNull(val)) {
                continue;
            }

            if (val instanceof String[]) {
                String[] paramStr = (String[]) val;
                if (paramStr == null) {
                    continue;
                }
                for (String str : paramStr) {
                    formBodyBuilder.add(key, str);
                }
            } else {
                formBodyBuilder.add(key, val.toString());
            }
        }

        RequestBody formBody = formBodyBuilder.build();
        Request request = getRequestBuilder(restApiModel, formBody, marsHeader)
                .url(restApiModel.getUrl())
                .build();

        return HttpCommons.okCall(okHttpClient, request);
    }

    /**
     * get表单提交
     *
     * @param restApiModel
     * @param params
     * @return
     */
    private static HttpResultModel formGet(RestApiCacheModel restApiModel, Object[] params, MarsHeader marsHeader) throws Exception {
        OkHttpClient okHttpClient = HttpCommons.getOkHttpClient();

        JSONObject jsonParam = ParamConversionUtil.conversionToJson(params);

        StringBuffer paramStr = new StringBuffer();

        boolean isFirst = true;
        for (String key : jsonParam.keySet()) {
            Object val = jsonParam.get(key);
            if (StringUtil.isNull(val)) {
                continue;
            }

            if (isFirst) {
                paramStr.append("?");
            } else {
                paramStr.append("&");
            }

            if (val instanceof String[]) {
                String[] paramStrings = (String[]) val;
                if (paramStrings == null) {
                    continue;
                }
                for (int i = 0; i < paramStrings.length; i++) {
                    String va = paramStrings[i];
                    if (StringUtil.isNull(va)) {
                        continue;
                    }
                    String pStr = paramStr.toString();
                    if (i > 0 && !pStr.endsWith("?") && !pStr.endsWith("&")) {
                        paramStr.append("&");
                    }
                    paramStr.append(key);
                    paramStr.append("=");
                    paramStr.append(URLEncoder.encode(va, MarsConstant.ENCODING));
                }
            } else {
                paramStr.append(key);
                paramStr.append("=");
                paramStr.append(URLEncoder.encode(val.toString(), MarsConstant.ENCODING));
            }

            isFirst = false;
        }

        Request request = getRequestBuilder(restApiModel, null, marsHeader)
                .url(restApiModel.getUrl() + paramStr.toString())
                .build();

        return HttpCommons.okCall(okHttpClient, request);
    }

    /**
     * json提交
     *
     * @param restApiModel
     * @param params
     * @return
     */
    private static HttpResultModel json(RestApiCacheModel restApiModel, Object[] params, MarsHeader marsHeader) throws Exception {
        String jsonStrParam = "{}";
        JSONObject jsonParam = ParamConversionUtil.conversionToJson(params);
        if (jsonParam != null) {
            jsonStrParam = jsonParam.toJSONString();
        }

        OkHttpClient okHttpClient = HttpCommons.getOkHttpClient();

        MediaType mediaType = MediaType.parse(MarsCloudConstant.CONTENT_TYPE_JSON);

        RequestBody requestbody = RequestBody.create(jsonStrParam, mediaType);
        Request request = getRequestBuilder(restApiModel, requestbody, marsHeader)
                .url(restApiModel.getUrl())
                .build();

        return HttpCommons.okCall(okHttpClient, request);
    }

    /**
     * 根据接口的请求方式，返回不同的Builder
     *
     * @param restApiModel
     * @param requestBody
     * @return
     */
    private static Request.Builder getRequestBuilder(RestApiCacheModel restApiModel, RequestBody requestBody, MarsHeader marsHeader) {
        Request.Builder builder = new Request.Builder();

        builder = setHeaderForRequestBuilder(builder, marsHeader);

        switch (restApiModel.getReqMethod()) {
            case POST:
                builder.post(requestBody);
                break;
            case PUT:
                builder.put(requestBody);
                break;
            case DELETE:
                builder.delete(requestBody);
                break;
            case GET:
                builder.get();
                break;
        }

        return builder;
    }

    /**
     * 添加请求头
     * @param builder
     * @param marsHeader
     * @return
     */
    private static Request.Builder setHeaderForRequestBuilder(Request.Builder builder, MarsHeader marsHeader){
        if (marsHeader != null && marsHeader.size() > 0) {
            for (String key : marsHeader.keySet()) {
                List<String> value = marsHeader.get(key);
                if (value == null || key == null || key.toUpperCase().equals(CONTENT_TYPE_STR)) {
                    continue;
                }
                for(String val : value){
                    builder.addHeader(key, val);
                }
            }
        }
        return builder;
    }
}
