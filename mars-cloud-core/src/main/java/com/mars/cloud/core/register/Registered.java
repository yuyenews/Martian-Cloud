package com.mars.cloud.core.register;

import com.alibaba.fastjson.JSON;
import com.mars.cloud.constant.MarsCloudConstant;
import com.mars.cloud.core.helper.ZkHelper;
import com.mars.cloud.core.register.model.RestApiModel;
import com.mars.cloud.util.MarsCloudConfigUtil;
import com.mars.cloud.util.MarsCloudUtil;
import com.mars.common.constant.MarsConstant;
import com.mars.common.constant.MarsSpace;
import com.mars.mvc.load.model.MarsMappingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 往nacos注册服务
 */
public class Registered {

    private Logger marsLogger = LoggerFactory.getLogger(Registered.class);

    /**
     * 获取全局存储空间
     */
    private MarsSpace constants = MarsSpace.getEasySpace();

    /**
     * 注册接口
     */
    public void doRegister() throws Exception {
        try {
            ZkHelper.openConnection();

            marsLogger.info("接口注册中.......");

            /* 获取本服务的名称 */
            String serverName = MarsCloudConfigUtil.getCloudName();
            /* 获取本服务IP */
            String ip = MarsCloudUtil.getLocalIp();
            /* 获取本服务端口 */
            String port = MarsCloudUtil.getPort();

            /* 从内存中获取本项目的MarsApi */
            List<RestApiModel> restApiModelList = getMarsApis();

            for (RestApiModel restApiModel : restApiModelList) {
                String jsonStr = JSON.toJSONString(restApiModel);

                String node = MarsCloudConstant.API_SERVER_NODE
                        .replace("{serverName}", serverName)
                        .replace("{method}", restApiModel.getMethodName())
                        .replace("{ip}", ip)
                        .replace("{port}", port);

                ZkHelper.createNodes(node, jsonStr);

                marsLogger.info("接口[" + restApiModel.getUrl() + "]注册成功");
            }
        } catch (Exception e){
            throw new Exception("注册与发布接口失败", e);
        }
    }

    /**
     * 获取所有API
     * @return
     */
    private List<RestApiModel> getMarsApis() throws Exception {
        /* 从内存中获取本项目的MarsApi */
        Object apiMap = constants.getAttr(MarsConstant.CONTROLLER_OBJECTS);
        if (apiMap == null) {
            return new ArrayList<>();
        }

        List<RestApiModel> restApiModelList = new ArrayList<>();

        Map<String, MarsMappingModel> marsApiObjects = (Map<String, MarsMappingModel>) apiMap;
        for(String methodName : marsApiObjects.keySet()){
            MarsMappingModel marsMappingModel = marsApiObjects.get(methodName);
            if(marsMappingModel == null){
                continue;
            }
            String mName = marsMappingModel.getExeMethod().getName();

            RestApiModel restApiModel = new RestApiModel();
            restApiModel.setUrl(MarsCloudUtil.getLocalHost() + "/" + mName);
            restApiModel.setMethodName(mName);
            restApiModel.setReqMethod(marsMappingModel.getReqMethod());

            restApiModelList.add(restApiModel);
        }

        return restApiModelList;
    }
}
