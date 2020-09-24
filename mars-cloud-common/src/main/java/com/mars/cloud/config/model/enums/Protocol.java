package com.mars.cloud.config.model.enums;

/**
 * 请求协议
 */
public enum Protocol {

    HTTP("http://"),
    /* 下面两个为预留属性，暂不支持 */
    HTTPS("https://"),
    MARS("mars://");

    String code;

    Protocol(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
