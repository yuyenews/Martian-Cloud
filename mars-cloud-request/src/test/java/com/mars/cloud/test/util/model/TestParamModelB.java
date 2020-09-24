package com.mars.cloud.test.util.model;

import java.util.Date;

public class TestParamModelB {

    private String name2 = "张三";

    private Integer age2 = 12;

    private Date date2 = new Date();

    private TestParamModelA testParamModelA = new TestParamModelA();

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public Integer getAge2() {
        return age2;
    }

    public void setAge2(Integer age2) {
        this.age2 = age2;
    }

    public Date getDate2() {
        return date2;
    }

    public void setDate2(Date date2) {
        this.date2 = date2;
    }

    public TestParamModelA getTestParamModelA() {
        return testParamModelA;
    }

    public void setTestParamModelA(TestParamModelA testParamModelA) {
        this.testParamModelA = testParamModelA;
    }
}
