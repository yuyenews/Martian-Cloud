package com.mars.cloud.model;

import okhttp3.ResponseBody;

import java.io.InputStream;

/**
 * http请求返回数据
 */
public class HttpResultModel {

    private String fileName;

    private int code;

    private ResponseBody responseBody;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getInputStream() {
        return responseBody.byteStream();
    }

    public String getJSONString() {
        try {
            return responseBody.string();
        } catch (Exception e){
            return null;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
    }
}
