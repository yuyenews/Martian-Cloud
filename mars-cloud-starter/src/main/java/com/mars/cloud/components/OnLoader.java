package com.mars.cloud.components;

import com.mars.cloud.core.register.Registered;
import com.mars.cloud.request.feign.load.LoadMarsFeign;
import com.mars.cloud.request.util.MarsCloudParamAndResult;
import com.mars.common.annotation.bean.MarsOnLoad;
import com.mars.common.base.BaseOnLoad;
import com.mars.iserver.par.factory.ParamAndResultFactory;

/**
 * 启动时事件
 */
@MarsOnLoad
public class OnLoader implements BaseOnLoad {

    /**
     * 加载feign
     * @throws Exception
     */
    @Override
    public void before() throws Exception {
        /* 加载Feign对象 */
        LoadMarsFeign.LoadCloudFeign();
    }

    /**
     * 注册服务
     * @throws Exception
     */
    @Override
    public void after() throws Exception {
        /* 指定 处理参数和响应的对象实例 */
        ParamAndResultFactory.setBaseParamAndResult(new MarsCloudParamAndResult());

        /* 注册服务 */
        Registered registered = new Registered();
        registered.doRegister();
    }
}
