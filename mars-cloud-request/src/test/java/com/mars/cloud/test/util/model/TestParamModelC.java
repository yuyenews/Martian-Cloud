package com.mars.cloud.test.util.model;

import com.mars.server.server.request.model.MarsFileUpLoad;

import java.io.FileInputStream;

public class TestParamModelC {

    private String name3 = "张三";

    private Integer age3 = 12;

    private MarsFileUpLoad marsFileUpLoad;

    private MarsFileUpLoad[] marsFileUpLoads;

    public TestParamModelC(){
        try {
            marsFileUpLoad = new MarsFileUpLoad();
            marsFileUpLoad.setName("fileName");
            marsFileUpLoad.setFileName("fileName.jps");
            marsFileUpLoad.setInputStream(new FileInputStream("/Users/yuye/Downloads/a.pdf"));

            marsFileUpLoads = new MarsFileUpLoad[2];
            marsFileUpLoads[0] = marsFileUpLoad;
            marsFileUpLoads[1] = marsFileUpLoad;

        } catch (Exception e){
        }
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public Integer getAge3() {
        return age3;
    }

    public void setAge3(Integer age3) {
        this.age3 = age3;
    }

    public MarsFileUpLoad getMarsFileUpLoad() {
        return marsFileUpLoad;
    }

    public void setMarsFileUpLoad(MarsFileUpLoad marsFileUpLoad) {
        this.marsFileUpLoad = marsFileUpLoad;
    }

    public MarsFileUpLoad[] getMarsFileUpLoads() {
        return marsFileUpLoads;
    }

    public void setMarsFileUpLoads(MarsFileUpLoad[] marsFileUpLoads) {
        this.marsFileUpLoads = marsFileUpLoads;
    }
}
