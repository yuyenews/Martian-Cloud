package com.mars.cloud.constant;

/**
 * http状态
 */
public enum HttpStatusConstant {

    SUCCESS(200);

    private int code;

    HttpStatusConstant(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
