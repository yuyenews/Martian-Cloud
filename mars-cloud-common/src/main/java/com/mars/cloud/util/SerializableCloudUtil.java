package com.mars.cloud.util;

import com.alibaba.fastjson.JSON;
import com.mars.common.util.SerializableUtil;

import java.io.*;

/**
 * 序列化与反序列化工具类
 */
public class SerializableCloudUtil {

    /**
     * 将对象序列化成二进制流
     * @param obj 对象
     * @return 对象
     * @throws Exception 异常
     */
    public static byte[] serialization(Object obj) throws Exception {
        return SerializableUtil.serialization(obj);
    }

    /**
     * 将二进制流反序列化成对象
     * @param by 对象
     * @param cls 类型
     * @param <T> 对象
     * @return 对象
     * @throws Exception 异常
     */
    public static <T> T deSerialization(byte[] by,Class<T> cls) throws Exception {
        return SerializableUtil.deSerialization(by, cls);
    }

    /**
     * 将二进制流反序列化成对象
     * @param inputStream 对象
     * @param cls 类型
     * @param <T> 对象
     * @return 对象
     * @throws Exception 异常
     */
    public static <T> T deSerialization(InputStream inputStream,Class<T> cls) throws Exception {
        try {
            if (cls.equals(InputStream.class)) {
                return (T) inputStream;
            }

            Object object = SerializableUtil.deSerialization(inputStream, Object.class);
            if (!cls.equals(object.getClass())) {
                throw new Exception("无法将" + object.getClass().getName() + "类型转成" + cls.getName() + "类型，原数据：" + JSON.toJSONString(object));
            }

            return (T) object;
        } catch (Exception e) {
            throw new Exception("将二进制流反序列化成参数，出现异常", e);
        }
    }
}
