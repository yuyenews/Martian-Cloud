package com.mars.cloud.request.util;

import com.alibaba.fastjson.JSONObject;
import com.mars.cloud.request.rest.model.RequestParamModel;
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
            Class cls = param.getClass();
            Field[] fields = cls.getDeclaredFields();

            for (Object par : params) {
                for (Field field : fields) {
                    field.setAccessible(true);

                    if(Modifier.isFinal(field.getModifiers())){
                        continue;
                    }

                    RequestParamModel requestParamModel = getRequestParamModel(par, field);
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

        RequestParamModel paramModel = new RequestParamModel();

        if(field.getType().equals(MarsFileUpLoad.class)){
            MarsFileUpLoad marsFileUpLoad = (MarsFileUpLoad)val;

            paramModel.setMarsFileUpLoad(marsFileUpLoad);
            paramModel.setFile(true);
        } else if(field.getType().equals(MarsFileUpLoad[].class)) {
            MarsFileUpLoad[] marsFileUpLoads = (MarsFileUpLoad[])val;

            paramModel.setMarsFileUpLoads(marsFileUpLoads);
            paramModel.setFile(true);
        } else {
            paramModel.setName(field.getName());
            paramModel.setValue(val);
            paramModel.setFile(false);
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
