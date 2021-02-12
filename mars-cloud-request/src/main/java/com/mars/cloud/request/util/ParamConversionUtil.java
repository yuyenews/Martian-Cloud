package com.mars.cloud.request.util;

import com.alibaba.fastjson.JSONObject;
import com.mars.cloud.request.rest.model.RequestParamModel;
import com.mars.cloud.request.util.enums.ValueType;
import com.mars.common.util.StringUtil;
import com.mars.server.server.request.model.MarsFileUpLoad;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数转化工具类
 */
public class ParamConversionUtil {

    /**
     * 将Object数组转成一个json对象
     * @param params
     */
    public static JSONObject conversionToJson(Object[] params) throws Exception {
        if(params == null){
            return null;
        }
        JSONObject jsonParam = new JSONObject();
        for(Object param : params){
            if(param instanceof Map){
                /* 如果对象是个map就直接强转然后处理 */
                Map map = (Map) param;
                for(Object key : map.keySet()){
                    jsonParam.put(key.toString(), map.get(key));
                }
            } else {
                // 否则就用反射处理
                Class cls = param.getClass();
                Field[] fields = cls.getDeclaredFields();
                for(Field field : fields){
                    field.setAccessible(true);

                    if(Modifier.isFinal(field.getModifiers())){
                        continue;
                    }
                    Object val = field.get(param);
                    if(StringUtil.isNull(val)){
                        continue;
                    }
                    jsonParam.put(field.getName(), val);
                }
            }
        }
        return jsonParam;
    }

    /**
     * 将参数转成统一规格的对象集合
     * @param params
     * @return
     * @throws Exception
     */
    public static Map<String, RequestParamModel> getRequestParamModelList(Object[] params) throws Exception {
        if(params == null){
            return null;
        }

        Map<String, RequestParamModel> requestParamModelList = new HashMap<>();
        for(Object param : params) {
            if(param instanceof Map){
                /* 如果对象是个map就直接强转然后处理 */
                Map map = (Map) param;
                for(Object key : map.keySet()){
                    Object value = map.get(key);
                    if(value == null){
                        continue;
                    }

                    RequestParamModel requestParamModel = getRequestParamModel(key.toString(), value);
                    if (requestParamModel == null) {
                        continue;
                    }
                    requestParamModelList.put(key.toString(), requestParamModel);
                }
            } else {
                // 否则就用反射处理
                Class cls = param.getClass();
                Field[] fields = cls.getDeclaredFields();

                for (Field field : fields) {
                    field.setAccessible(true);

                    if (Modifier.isFinal(field.getModifiers())) {
                        continue;
                    }

                    RequestParamModel requestParamModel = getRequestParamModel(param, field);
                    if (requestParamModel == null) {
                        continue;
                    }
                    requestParamModelList.put(field.getName(), requestParamModel);
                }
            }
        }
        return requestParamModelList;
    }

    /**
     * 将参数转成统一规格的对象
     * @param field
     * @return
     * @throws Exception
     */
    private static RequestParamModel getRequestParamModel(Object param, Field field) throws Exception {
        if(field == null){
            return null;
        }

        Object val = getFieldValue(param,field);
        if(val == null){
            return null;
        }
        return getRequestParamModel(field.getName(), val);
    }

    /**
     * 将参数转成统一规格的对象
     * @return
     * @throws Exception
     */
    private static RequestParamModel getRequestParamModel(String name, Object value) throws Exception {
        if(value == null){
            return null;
        }

        ValueType valueType = ValueType.getValueType(value);

        RequestParamModel paramModel = new RequestParamModel();

        switch (valueType){
            case FILE:
                MarsFileUpLoad marsFileUpLoad = (MarsFileUpLoad)value;
                paramModel.setMarsFileUpLoad(marsFileUpLoad);
                paramModel.setFile(true);
                return paramModel;
            case FILES:
                MarsFileUpLoad[] marsFileUpLoads = (MarsFileUpLoad[])value;
                paramModel.setMarsFileUpLoads(marsFileUpLoads);
                paramModel.setFile(true);
                return paramModel;
            case OTHER:
                paramModel.setName(name);
                paramModel.setValue(value);
                paramModel.setFile(false);
                return paramModel;
        }
        return paramModel;
    }

    /**
     * 获取字段
     * @param param
     * @param field
     * @return
     */
    private static Object getFieldValue(Object param, Field field){
        try {
            return field.get(param);
        } catch (Exception e){
            return null;
        }
    }

    /**
     * 将inputStream转成byte[]
     * @param input
     * @return
     * @throws Exception
     */
    public static byte[] toByteArray(InputStream input) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024*4];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}
