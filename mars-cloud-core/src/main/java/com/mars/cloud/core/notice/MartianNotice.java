package com.mars.cloud.core.notice;

import com.mars.cloud.constant.MarsCloudConstant;
import com.mars.cloud.core.cache.ServerApiCache;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.core.notice.model.RestApiVO;
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
import java.util.List;
import java.util.Map;

/**
 * 往nacos注册服务
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
            marsLogger.info("接口广播中.......");

            /* 获取本服务的名称 */
            String serverName = MarsCloudConfigUtil.getCloudName();

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
                serverApiCache.addCache(serverName, restApiModel.getMethodName(), restApiModel);
            }

            /* 发起广播 */
            doNotice(serverName, restApiModelList);
        } catch (Exception e){
            throw new Exception("注册与发布接口失败", e);
        }
    }

    /**
     * 将本地接口通知到所有的服务
     * @param serverName
     * @param restApiModelList
     * @throws Exception
     */
    private void doNotice(String serverName, List<RestApiCacheModel> restApiModelList) throws Exception {

        Map<String, List<RestApiCacheModel>> restApiCacheModelMap = serverApiCache.getRestApiModelsByKey();

        RestApiVO restApiVO = new RestApiVO();
        restApiVO.setServerName(serverName);
        restApiVO.setRestApiCacheModels(restApiModelList);

        for(List<RestApiCacheModel> restApiCacheModels : restApiCacheModelMap.values()){
            for(RestApiCacheModel restApiCacheModel : restApiCacheModels){
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(restApiCacheModel.getLocalHost());
                stringBuffer.append("/");
                stringBuffer.append(MarsCloudConstant.ADD_APIS);
                NoticeUtil.addApis(stringBuffer.toString(), restApiVO);
            }
        }
    }

    /**
     * 获取所有服务接口
     * @param contagionList
     * @throws Exception
     */
    private void getApis(String[] contagionList) throws Exception {
        for(String contagion : contagionList){
            String getApisUrl = contagion + "/" + MarsCloudConstant.GET_APIS;

            Map<String, List<RestApiCacheModel>> restApiCacheModelMap = NoticeUtil.getApis(getApisUrl);
            if(restApiCacheModelMap == null || restApiCacheModelMap.size() < 1){
                continue;
            }

            for(Map.Entry<String, List<RestApiCacheModel>> entry : restApiCacheModelMap.entrySet()){
                List<RestApiCacheModel> restApiCacheModels = entry.getValue();
                if(restApiCacheModels == null || restApiCacheModels.size() < 1){
                    continue;
                }
                for(RestApiCacheModel restApiCacheModel : restApiCacheModels){
                    serverApiCache.addCache(entry.getKey(), restApiCacheModel);
                }
            }
            break;
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

            restApiModelList.add(restApiModel);
        }

        return restApiModelList;
    }
}
