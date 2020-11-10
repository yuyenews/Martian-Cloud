package com.mars.cloud.core.notice;

import com.mars.cloud.constant.MarsCloudConstant;
import com.mars.cloud.core.cache.ServerApiCache;
import com.mars.cloud.core.cache.ServerApiCacheManager;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.core.notice.model.RestApiModel;
import com.mars.cloud.core.util.NoticeUtil;
import com.mars.cloud.util.MarsCloudConfigUtil;
import com.mars.cloud.util.MarsCloudUtil;
import com.mars.common.constant.MarsConstant;
import com.mars.common.constant.MarsSpace;
import com.mars.common.util.StringUtil;
import com.mars.mvc.load.model.MarsMappingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 通知器
 */
public class MartianNotice {

    private Logger marsLogger = LoggerFactory.getLogger(MartianNotice.class);

    /**
     * 获取全局存储空间
     */
    private MarsSpace constants = MarsSpace.getEasySpace();

    /**
     * 本地缓存
     */
    private ServerApiCache serverApiCache = new ServerApiCache();

    /**
     * 广播接口
     */
    public void notice() throws Exception {
        try {
            marsLogger.info("接口传染中.......");

            /* 获取本服务的名称 */
            String serverName = MarsCloudConfigUtil.getCloudName();

            /* 获取传染渠道 */
            String contagions = MarsCloudConfigUtil.getMarsCloudConfig().getCloudConfig().getContagions();
            if(StringUtil.isNull(contagions)){
                throw new Exception("传染渠道不可以为空, 否则本服务将被孤立");
            }

            /* 从传染渠道获取所有服务接口 */
            String[] contagionList = contagions.split(",");
            getApis(contagionList);

            /* 从内存中获取本项目的MarsApi */
            List<RestApiCacheModel> restApiModelList = getMarsApis();
            for (RestApiCacheModel restApiModel : restApiModelList) {
                serverApiCache.addCache(serverName, restApiModel.getMethodName(), restApiModel, true);
            }

            /* 发起广播 */
            doNotice(serverName, restApiModelList);
        } catch (Exception e){
            throw new Exception("接口传染失败", e);
        }
    }

    /**
     * 将自己的接口传染给所有的服务
     * @param serverName
     * @param restApiModelList
     * @throws Exception
     */
    private void doNotice(String serverName, List<RestApiCacheModel> restApiModelList) throws Exception {

        /* 获取即将被传染的服务 */
        List<String> contagionList = ServerApiCacheManager.getAllServerList(true);
        if(contagionList == null || contagionList.size() < 1){
            return;
        }

        /* 发起广播将自己的接口广播出去 */
        RestApiModel restApiModel = new RestApiModel();
        restApiModel.setServerName(serverName);
        restApiModel.setRestApiCacheModels(restApiModelList);

        for(String contagionUrl : contagionList){
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(contagionUrl);
            stringBuffer.append("/");
            stringBuffer.append(MarsCloudConstant.ADD_APIS);
            NoticeUtil.addApis(stringBuffer.toString(), restApiModel);
        }
    }

    /**
     * 获取所有服务接口
     * @param contagionList
     * @throws Exception
     */
    private void getApis(String[] contagionList) throws Exception {
        List<String> cacheServerList = ServerApiCacheManager.getAllServerList(true);

        /* 优先从自己缓存的服务上获取接口 */
        for(String url : cacheServerList){
            String getApisUrl = url + "/" + MarsCloudConstant.GET_APIS;
            boolean isSuccess = getRemoteApis(getApisUrl);
            if(isSuccess){
                /* 从任意服务器上拉取成功，就停止 */
                return;
            }
        }

        /* 如果从自己缓存的服务上没有获取到接口，则从配置的服务商拉取 */
        for(String contagion : contagionList){
            String getApisUrl = contagion + "/" + MarsCloudConstant.GET_APIS;
            boolean isSuccess = getRemoteApis(getApisUrl);
            if(isSuccess){
                /* 从任意服务器上拉取成功，就停止 */
                return;
            }
        }
    }

    /**
     * 从其他服务器拉取接口
     * @param getApisUrl
     * @return
     */
    private boolean getRemoteApis(String getApisUrl) {
        try {
            Map<String, List<RestApiCacheModel>> remoteCacheModelMap = NoticeUtil.getApis(getApisUrl);
            if(remoteCacheModelMap == null || remoteCacheModelMap.size() < 1){
                return false;
            }

            for(Map.Entry<String, List<RestApiCacheModel>> entry : remoteCacheModelMap.entrySet()){
                List<RestApiCacheModel> restApiCacheModels = entry.getValue();
                if(restApiCacheModels == null || restApiCacheModels.size() < 1){
                    return false;
                }
                for(RestApiCacheModel restApiCacheModel : restApiCacheModels){
                    serverApiCache.addCache(entry.getKey(), restApiCacheModel, false);
                }
            }
            return true;
        } catch (Exception e) {
            marsLogger.warn("拉取接口异常");
            return false;
        }
    }

    /**
     * 获取所有API
     * @return
     */
    private List<RestApiCacheModel> getMarsApis() throws Exception {
        /* 从内存中获取本项目的MarsApi */
        Object apiMap = constants.getAttr(MarsConstant.CONTROLLER_OBJECTS);
        if (apiMap == null) {
            return new ArrayList<>();
        }

        List<RestApiCacheModel> restApiModelList = new ArrayList<>();

        Map<String, MarsMappingModel> marsApiObjects = (Map<String, MarsMappingModel>) apiMap;
        for(String methodName : marsApiObjects.keySet()){
            MarsMappingModel marsMappingModel = marsApiObjects.get(methodName);
            if(marsMappingModel == null){
                continue;
            }
            String mName = marsMappingModel.getExeMethod().getName();

            RestApiCacheModel restApiModel = new RestApiCacheModel();
            restApiModel.setUrl(MarsCloudUtil.getLocalHost() + "/" + mName);
            restApiModel.setMethodName(mName);
            restApiModel.setLocalHost(MarsCloudUtil.getLocalHost());
            restApiModel.setReqMethod(marsMappingModel.getReqMethod());
            restApiModel.setCreateTime(new Date());
            restApiModelList.add(restApiModel);
        }

        return restApiModelList;
    }
}
