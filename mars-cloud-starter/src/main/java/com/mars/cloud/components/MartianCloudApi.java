package com.mars.cloud.components;

import com.mars.cloud.constant.MarsCloudConstant;
import com.mars.cloud.core.cache.ServerApiCacheManager;
import com.mars.cloud.core.cache.ServerApiExistManager;
import com.mars.cloud.model.RestApiCacheModel;
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
        return ServerApiCacheManager.getCacheApisMap();
    }

    /**
     * 接收其他服务发送过来的通知
     * @param restApiModel
     * @return
     */
    @RequestMethod(ReqMethod.POST)
    public String addApis(RestApiModel restApiModel){
        try {
            if(restApiModel == null){
                return MarsCloudConstant.RESULT_SUCCESS;
            }

            List<RestApiCacheModel> restApiCacheModelList = restApiModel.getRestApiCacheModels();
            if(restApiCacheModelList == null){
                return MarsCloudConstant.RESULT_SUCCESS;
            }

            RestApiCacheModel restApiCacheModel = restApiCacheModelList.get(0);
            if(restApiCacheModel != null && !ServerApiExistManager.hasExist(restApiCacheModel.getLocalHost())){
                logger.info("受到了来自[name:{},url:{}]服务的接口传染,感染接口数量:[{}]", restApiModel.getServerName(), restApiCacheModel.getLocalHost(), restApiCacheModelList.size());
                ServerApiExistManager.add(restApiCacheModel.getLocalHost());
            }

            /* 将收到的接口存入本地缓存 */
            ServerApiCacheManager.addCacheApi(restApiModel);

            return MarsCloudConstant.RESULT_SUCCESS;
        } catch (Exception e){
            return MarsCloudConstant.RESULT_ERROR;
        }
    }
}
