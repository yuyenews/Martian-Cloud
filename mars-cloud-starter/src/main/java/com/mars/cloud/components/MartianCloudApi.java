package com.mars.cloud.components;

import com.mars.cloud.core.cache.ServerApiCache;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.core.notice.model.RestApiVO;
import com.mars.common.annotation.api.MarsApi;
import com.mars.common.annotation.api.RequestMethod;
import com.mars.common.annotation.enums.ReqMethod;

import java.util.List;
import java.util.Map;

/**
 * 内部接口，用来接收和发送广播的
 */
@MarsApi
public class MartianCloudApi {

    private ServerApiCache serverApiCache = new ServerApiCache();

    /**
     * 将本服务存储的接口给其他服务
     * @return
     */
    @RequestMethod(ReqMethod.POST)
    public Map<String, List<RestApiCacheModel>> getApis(){
        return serverApiCache.getRestApiModelsByKey();
    }

    /**
     * 接口其他服务发送过来的通知
     * @param restApiVO
     * @return
     */
    @RequestMethod(ReqMethod.POST)
    public String addApis(RestApiVO restApiVO){
        for(RestApiCacheModel restApiCacheModel : restApiVO.getRestApiCacheModels()){
            serverApiCache.addCache(restApiVO.getServerName(), restApiCacheModel.getMethodName(), restApiCacheModel);
        }
        return "ok";
    }
}
