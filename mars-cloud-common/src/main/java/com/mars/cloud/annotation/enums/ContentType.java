package com.mars.cloud.annotation.enums;

public enum ContentType {

    JSON("JSON"), FORM_DATA("FORM_DATA"), FORM("FORM");

    private String code;

    ContentType(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
