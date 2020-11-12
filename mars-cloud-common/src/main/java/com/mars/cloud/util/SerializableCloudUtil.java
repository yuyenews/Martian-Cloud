package com.mars.cloud.util;

import com.alibaba.fastjson.JSONObject;
import com.mars.common.util.SerializableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 序列化与反序列化工具类
 */
public class SerializableCloudUtil {

    private static Logger logger = LoggerFactory.getLogger(SerializableCloudUtil.class);

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
        Object object = null;
        try {
            if(inputStream == null){
                return null;
            }

            if (cls.equals(InputStream.class)) {
                return (T) inputStream;
            }

            object = SerializableUtil.deSerialization(inputStream, Object.class);
            return (T) object;
        } catch (Exception e) {
            String errorMag = "将二进制流反序列化成源对象出现异常";
            if(object != null){
                errorMag = errorMag + ",原数据:" + JSONObject.toJSONString(object);
            }
            logger.error(errorMag, e);
            throw new Exception(errorMag, e);
        }
    }
}
