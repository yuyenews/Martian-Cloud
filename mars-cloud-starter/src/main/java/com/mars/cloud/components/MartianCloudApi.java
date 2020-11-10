package com.mars.cloud.components;

import com.mars.cloud.core.cache.ServerApiCacheManager;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.core.notice.model.RestApiModel;
import com.mars.common.annotation.api.MarsApi;
import com.mars.common.annotation.api.RequestMethod;
import com.mars.common.annotation.enums.ReqMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 内部接口，用来接收和发送广播的
 */
@MarsApi
public class MartianCloudApi {

    private Logger logger = LoggerFactory.getLogger(MartianCloudApi.class);

    /**
     * 将本服务存储的接口给其他服务
     * @return
     */
    public Map<String, List<RestApiCacheModel>> getApis(){
        logger.info("被拉取了接口");
        return ServerApiCacheManager.getCacheApisMap();
    }

    /**
     * 接口其他服务发送过来的通知
     * @param restApiModel
     * @return
     */
    @RequestMethod(ReqMethod.POST)
    public String addApis(RestApiModel restApiModel){
        if(restApiModel == null || restApiModel.getRestApiCacheModels() == null){
            return "ok";
        }

        logger.info("受到了来自{}服务的接口传染,感染接口数量:{}", restApiModel.getServerName(), restApiModel.getRestApiCacheModels().size());
        ServerApiCacheManager.addCacheApi(restApiModel, true);
        return "ok";
    }
}
