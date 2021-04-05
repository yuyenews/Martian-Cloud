package com.mars.cloud.request.util;

import com.mars.mvc.util.ParamAndResult;

/**
 * 获取Martian的参数和返回处理对象
 */
public class MarsCloudParamAndResultFactory {

    private static ParamAndResult paramAndResult = new ParamAndResult();

    /**
     * 获取Martian的参数和返回处理对象
     * @return
     */
    public static ParamAndResult getParamAndResult(){
        return paramAndResult;
    }
}
