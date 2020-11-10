package com.mars.cloud.components;

import com.mars.cloud.core.notice.MartianNotice;
import com.mars.cloud.request.feign.load.LoadMarsFeign;
import com.mars.cloud.request.util.MarsCloudParamAndResult;
import com.mars.cloud.util.MarsCloudConfigUtil;
import com.mars.common.annotation.bean.MarsOnLoad;
import com.mars.common.base.BaseOnLoad;
import com.mars.iserver.par.factory.ParamAndResultFactory;

import java.util.concurrent.CountDownLatch;

/**
 * 启动时事件
 */
@MarsOnLoad
public class OnLoader implements BaseOnLoad {

    public static CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * 加载feign对象
     * @throws Exception
     */
    @Override
    public void before() throws Exception {
        /* 加载Feign对象 */
        LoadMarsFeign.LoadCloudFeign();
    }

    /**
     * 传染服务接口
     * @throws Exception
     */
    @Override
    public void after() throws Exception {
        boolean isGateWay = MarsCloudConfigUtil.getMarsCloudConfig().getCloudConfig().isGateWay();
        if(!isGateWay){
            /* 如果当前服务不是一个网关，则采用序列化的方式响应数据 */
            ParamAndResultFactory.setBaseParamAndResult(new MarsCloudParamAndResult());
        }

        /* 传染服务接口 */
        MartianNotice martianNotice = new MartianNotice();
        martianNotice.notice();
        countDownLatch.countDown();
    }
}
