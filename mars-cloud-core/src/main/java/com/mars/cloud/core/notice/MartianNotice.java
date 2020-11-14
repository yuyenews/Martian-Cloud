package com.mars.cloud.core.notice;

import com.mars.cloud.constant.MarsCloudConstant;
import com.mars.cloud.core.cache.ServerApiCache;
import com.mars.cloud.core.cache.ServerApiCacheManager;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.core.notice.model.RestApiModel;
import com.mars.cloud.core.util.NoticeUtil;
import com.mars.cloud.util.MarsCloudConfigUtil;
import com.mars.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 通知器
 */
public class MartianNotice {

    private Logger marsLogger = LoggerFactory.getLogger(MartianNotice.class);

    /**
     * 广播接口
     */
    public void notice() throws Exception {
        try {
            marsLogger.info("接口传染中.......");

            /* 获取传染渠道 */
            String contagions = MarsCloudConfigUtil.getMarsCloudConfig().getCloudConfig().getContagions();
            if(StringUtil.isNull(contagions)){
                throw new Exception("传染渠道不可以为空, 否则本服务将被孤立");
            }

            /* 从传染渠道获取所有服务接口 */
            String[] contagionList = contagions.split(",");
            getApis(contagionList);

            /* 发起广播 */
            doNotice();
        } catch (Exception e){
            throw new Exception("接口传染失败", e);
        }
    }

    /**
     * 将自己的接口传染给所有的服务
     * @throws Exception
     */
    private void doNotice() throws Exception {

        /* 获取即将被传染的服务 */
        List<String> contagionList = ServerApiCacheManager.getAllServerList(true);
        if(contagionList == null || contagionList.size() < 1){
            return;
        }

        /* 获取本服务的名称 */
        String serverName = MarsCloudConfigUtil.getCloudName();

        /* 从内存中获取本项目的MarsApi */
        List<RestApiCacheModel> restApiModelList = ServerApiCacheManager.getMarsApis();

        /* 发起广播将自己的接口广播出去 */
        RestApiModel restApiModel = new RestApiModel();
        restApiModel.setServerName(serverName);
        restApiModel.setRestApiCacheModels(restApiModelList);

        for(String contagionUrl : contagionList){
            /* 如果此服务被通知过了，则跳过 */
            if(NotifiedManager.isNotified(contagionUrl)){
                continue;
            }

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(contagionUrl);
            stringBuffer.append("/");
            stringBuffer.append(MarsCloudConstant.ADD_APIS);
            NoticeUtil.addApis(stringBuffer.toString(), restApiModel);

            /* 记录此服务已经被通知过了 */
            NotifiedManager.addNotified(contagionUrl);
        }
    }

    /**
     * 获取所有服务接口
     * @param contagionList
     * @throws Exception
     */
    private void getApis(String[] contagionList) throws Exception {
        List<String> cacheServerList = ServerApiCacheManager.getAllServerList(true);
        if (cacheServerList != null && cacheServerList.size() > 0) {
            /* 优先从自己缓存的服务上获取接口 */
            String url = NoticeUtil.getRandomUrl(cacheServerList);
            for (int i = 0; i < cacheServerList.size(); i++) {
                if(!StringUtil.isNull(url)){
                    String getApisUrl = url + "/" + MarsCloudConstant.GET_APIS;
                    boolean isSuccess = getRemoteApis(getApisUrl);
                    if (isSuccess) {
                        /* 从任意服务器上拉取成功，就停止 */
                        return;
                    }
                }
                url = NoticeUtil.getRandomUrl(cacheServerList);
            }
        }

        /* 如果从自己缓存的服务上没有获取到接口，则从配置的服务商拉取 */
        String contagion = NoticeUtil.getRandomUrl(contagionList);
        for (int i = 0; i < contagionList.length; i++) {
            if(!StringUtil.isNull(contagion)) {
                String getApisUrl = contagion + "/" + MarsCloudConstant.GET_APIS;
                boolean isSuccess = getRemoteApis(getApisUrl);
                if (isSuccess) {
                    /* 从任意服务器上拉取成功，就停止 */
                    return;
                }
            }
            contagion = NoticeUtil.getRandomUrl(contagionList);
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
                    continue;
                }

                RestApiModel restApiModel = new RestApiModel();
                restApiModel.setServerName(ServerApiCache.getServerNameFormKey(entry.getKey()));
                restApiModel.setRestApiCacheModels(restApiCacheModels);
                ServerApiCacheManager.addCacheApi(restApiModel, false);
            }
            return true;
        } catch (Exception e) {
            marsLogger.warn("从[{}]服务拉取接口异常:{}",getApisUrl.substring(0, getApisUrl.lastIndexOf("/")), e.getMessage());
            return false;
        }
    }
}
