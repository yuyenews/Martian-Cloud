package com.mars.cloud.request.util.enums;

import com.mars.server.server.request.model.MarsFileUpLoad;

import java.lang.reflect.Field;

/**
 * 值类型
 */
public enum ValueType {

    FILE, FILES, OTHER;

    /**
     * 根据字段判断类型
     * @param field
     * @return
     */
    public static ValueType getValueType(Field field){
        if(field.getType().equals(MarsFileUpLoad.class)){
            return FILE;
        } else if(field.getType().equals(MarsFileUpLoad[].class)) {
            return FILES;
        } else {
            return OTHER;
        }
    }

    /**
     * 根据值判断类型
     * @param value
     * @return
     */
    public static ValueType getValueType(Object value){
        if(value instanceof MarsFileUpLoad){
            return FILE;
        } else if(value instanceof MarsFileUpLoad[]) {
            return FILES;
        } else {
            return OTHER;
        }
    }
}
