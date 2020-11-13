package com.mars.cloud.components;

import com.mars.cloud.constant.MarsCloudConstant;
import com.mars.cloud.core.cache.ServerApiCacheManager;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.core.notice.NotifiedManager;
import com.mars.cloud.core.notice.model.NotifiedModel;
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
            if(restApiModel == null || restApiModel.getRestApiCacheModels() == null){
                return MarsCloudConstant.RESULT_SUCCESS;
            }

            logger.info("受到了来自[{}]服务的接口传染,感染接口数量:[{}]", restApiModel.getServerName(), restApiModel.getRestApiCacheModels().size());

            /* 将收到的接口存入本地缓存 */
            ServerApiCacheManager.addCacheApi(restApiModel, true);

            return MarsCloudConstant.RESULT_SUCCESS;
        } catch (Exception e){
            return MarsCloudConstant.RESULT_ERROR;
        }
    }

    /**
     * 移除已经被通知过的服务，
     * 在某个服务将此服务下线后，为了防止是误判，所以要给此服务发个通知告诉他
     * @return
     */
    @RequestMethod(ReqMethod.POST)
    public String removeNotified(NotifiedModel notifiedModel){
        NotifiedManager.removeNotified(notifiedModel.getServerInfo());
        return MarsCloudConstant.RESULT_SUCCESS;
    }
}
