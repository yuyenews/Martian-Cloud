package com.mars.cloud.request.util;

import com.alibaba.fastjson.JSONObject;
import com.mars.cloud.annotation.enums.ContentType;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.request.rest.model.RequestParamModel;
import com.mars.cloud.request.util.model.HttpResultModel;
import com.mars.cloud.util.MarsCloudConfigUtil;
import com.mars.common.annotation.enums.ReqMethod;
import com.mars.common.constant.MarsConstant;
import com.mars.common.util.StringUtil;
import com.mars.server.server.request.model.MarsFileUpLoad;
import okhttp3.*;

import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP请求工具类
 */
public class HttpUtil {

    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    private static final String FORM_DATA = "multipart/form-data";

    /**
     * 发起请求
     * @param restApiCacheModel
     * @param params
     * @return
     */
    public static HttpResultModel request(RestApiCacheModel restApiCacheModel, Object[] params, ContentType contentType) throws Exception{
        if(contentType == null){
            throw new Exception("必须指定ContentType");
        }
        if(restApiCacheModel.getReqMethod().equals(ReqMethod.GET) && !contentType.equals(ContentType.FORM)){
            throw new Exception("请求的接口，请求方式为GET，所以ContentType只能为FORM，接口名:" + restApiCacheModel.getUrl());
        }
        if(restApiCacheModel.getReqMethod().equals(ReqMethod.GET)){
            return formGet(restApiCacheModel, params);
        } else {
            if(contentType.equals(ContentType.FORM)){
                return formPost(restApiCacheModel,params);
            } else if(contentType.equals(ContentType.FORM_DATA)){
                return formData(restApiCacheModel,params);
            } else if(contentType.equals(ContentType.JSON)){
                return json(restApiCacheModel,params);
            } else {
                throw new Exception("请求的接口ContentType未知，接口名:" + restApiCacheModel.getUrl());
            }
        }
    }

    /**
     * formData提交
     * @param restApiModel
     * @param params
     * @return
     * @throws Exception
     */
    private static HttpResultModel formData(RestApiCacheModel restApiModel, Object[] params) throws Exception {

        OkHttpClient okHttpClient = getOkHttpClient();

        /* 发起post请求 将数据传递过去 */
        MediaType formData = MediaType.parse(FORM_DATA);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(formData);

        /* 将参数转成统一规格的对象，进行下面的传参操作 */
        Map<String, RequestParamModel> requestParamModelMap = ParamConversionUtil.getRequestParamModelList(params);
        for(RequestParamModel requestParamModel : requestParamModelMap.values()){
            if(requestParamModel.isFile()){
                // 如果是文件
                Map<String, MarsFileUpLoad> marsFileUpLoadMap = requestParamModel.getMarsFileUpLoads();

                for(MarsFileUpLoad marsFileUpLoad : marsFileUpLoadMap.values()){
                    byte[] file = ParamConversionUtil.toByteArray(marsFileUpLoad.getInputStream());

                    RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));
                    builder.addFormDataPart(marsFileUpLoad.getName(), marsFileUpLoad.getFileName(), fileBody);
                }
            } else {
                // 如果不是文件
                Object val = requestParamModel.getValue();
                if(val == null){
                    continue;
                }
                if(val instanceof String[]){
                    String[] valStr = (String[])val;
                    for(String str : valStr){
                        builder.addFormDataPart(requestParamModel.getName(), str);
                    }
                } else {
                    builder.addFormDataPart(requestParamModel.getName(), val.toString());
                }
            }
        }
        Request request = getRequestBuilder(restApiModel,builder.build())
                .url(restApiModel.getUrl())
                .build();

        return okCall(okHttpClient, request);
    }

    /**
     * post表单提交
     * @param restApiModel
     * @param params
     * @return
     */
    private static HttpResultModel formPost(RestApiCacheModel restApiModel, Object[] params) throws Exception {
        OkHttpClient okHttpClient = getOkHttpClient();

        JSONObject jsonParam = ParamConversionUtil.conversionToJson(params);

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for(String key : jsonParam.keySet()) {
            Object val = jsonParam.get(key);
            if(StringUtil.isNull(val)){
                continue;
            }

            if(val instanceof String[]){
                String[] paramStr = (String[])val;
                if(paramStr == null){
                    continue;
                }
                for(String str : paramStr){
                    formBodyBuilder.add(key, str);
                }
            } else {
                formBodyBuilder.add(key, val.toString());
            }
        }

        RequestBody formBody = formBodyBuilder.build();
        Request request = getRequestBuilder(restApiModel,formBody)
                .url(restApiModel.getUrl())
                .build();

        return okCall(okHttpClient, request);
    }

    /**
     * get表单提交
     * @param restApiModel
     * @param params
     * @return
     */
    private static HttpResultModel formGet(RestApiCacheModel restApiModel, Object[] params) throws Exception {
        OkHttpClient okHttpClient = getOkHttpClient();

        JSONObject jsonParam = ParamConversionUtil.conversionToJson(params);

        StringBuffer paramStr = new StringBuffer();

        boolean isFirst = true;
        for(String key : jsonParam.keySet()) {
            Object val = jsonParam.get(key);
            if(StringUtil.isNull(val)){
                continue;
            }

            if(isFirst){
                paramStr.append("?");
            } else {
                paramStr.append("&");
            }

            if(val instanceof String[]){
                String[] paramStrings = (String[])val;
                if(paramStrings == null){
                    continue;
                }
                for(int i = 0; i < paramStrings.length; i++){
                    String va = paramStrings[i];
                    if(StringUtil.isNull(va)){
                        continue;
                    }
                    String pStr = paramStr.toString();
                    if(i > 0 && !pStr.endsWith("?") && !pStr.endsWith("&")){
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

        Request request = new Request
                .Builder()
                .get()
                .url(restApiModel.getUrl() + paramStr.toString())
                .build();

        return okCall(okHttpClient, request);
    }

    /**
     * json提交
     * @param restApiModel
     * @param params
     * @return
     */
    private static HttpResultModel json(RestApiCacheModel restApiModel, Object[] params) throws Exception {
        String jsonStrParam = "{}";
        JSONObject jsonParam = ParamConversionUtil.conversionToJson(params);
        if (jsonParam != null) {
            jsonStrParam = jsonParam.toJSONString();
        }

        OkHttpClient okHttpClient = getOkHttpClient();

        MediaType mediaType = MediaType.parse(CONTENT_TYPE_JSON);

        RequestBody requestbody = RequestBody.create(jsonStrParam, mediaType);
        Request request = getRequestBuilder(restApiModel,requestbody)
                .url(restApiModel.getUrl())
                .build();

        return okCall(okHttpClient, request);
    }

    /**
     * 根据接口的请求方式，返回不同的Builder
     * @param restApiModel
     * @param requestBody
     * @return
     */
    private static Request.Builder getRequestBuilder(RestApiCacheModel restApiModel, RequestBody requestBody){
        Request.Builder builder = new Request.Builder();

        switch (restApiModel.getReqMethod()){
            case POST:
                builder.post(requestBody);
                break;
            case PUT:
                builder.put(requestBody);
                break;
            case DELETE:
                builder.delete(requestBody);
                break;
        }

        return builder;
    }

    /**
     * 开始请求
     *
     * @param okHttpClient 客户端
     * @param request      请求
     * @return 结果
     * @throws Exception 异常
     */
    private static HttpResultModel okCall(OkHttpClient okHttpClient, Request request) throws Exception {
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
    private static OkHttpClient getOkHttpClient() throws Exception {
        long timeOut = getTimeOut();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(timeOut, TimeUnit.SECONDS)//设置读取超时时间
                .build();
        return okHttpClient;
    }

    /**
     * 从配置中获取超时时间
     * @return
     */
    private static long getTimeOut(){
        try {
            return MarsCloudConfigUtil.getMarsCloudConfig().getCloudConfig().getTimeOut();
        } catch (Exception e){
            return 100L;
        }
    }
}
