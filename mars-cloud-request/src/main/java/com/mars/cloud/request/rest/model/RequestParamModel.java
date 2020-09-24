package com.mars.cloud.request.rest.model;

import com.mars.server.server.request.model.MarsFileUpLoad;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求参数的统一实体类，
 * 在发起rest请求时，会将参数统一转化成这个实体类（目前只有formData用到）
 */
public class RequestParamModel {

    private String name;

    private Object value;

    private Map<String,MarsFileUpLoad> marsFileUpLoads;

    private boolean isFile = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public Map<String, MarsFileUpLoad> getMarsFileUpLoads() {
        return marsFileUpLoads;
    }

    public void setMarsFileUpLoads(MarsFileUpLoad[] marsFileUpLoadArray) {
        if(marsFileUpLoadArray == null){
            return;
        }

        if(this.marsFileUpLoads == null){
            this.marsFileUpLoads = new HashMap<>();
        }

        for(MarsFileUpLoad marsFileUpLoad : marsFileUpLoadArray){
            if(marsFileUpLoad == null){
                continue;
            }
            marsFileUpLoads.put(marsFileUpLoad.getName() ,marsFileUpLoad);
        }
    }

    public void setMarsFileUpLoad(MarsFileUpLoad marsFileUpLoad) {
        if(marsFileUpLoad == null){
            return;
        }

        if(this.marsFileUpLoads == null){
            this.marsFileUpLoads = new HashMap<>();
        }

        marsFileUpLoads.put(marsFileUpLoad.getName(), marsFileUpLoad);

    }
}
