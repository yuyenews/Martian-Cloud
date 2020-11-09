package com.mars.cloud.model;

import java.io.InputStream;

/**
 * http请求返回数据
 */
public class HttpResultModel {

    private String fileName;

    private InputStream inputStream;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
