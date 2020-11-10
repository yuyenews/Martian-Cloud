package com.mars.cloud.core.cache;

import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.common.constant.MarsSpace;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 接口缓存
 */
public class ServerApiCache {

    private MarsSpace marsContext = MarsSpace.getEasySpace();

    private final String REST_API_KEY = "restApiKey";

    /**
     * 根据服务名称和方法名称获取接口集
     * @param serverName
     * @param methodName
     * @return
     */
    public List<RestApiCacheModel> getRestApiCacheModelList(String serverName, String methodName){
        Map<String, List<RestApiCacheModel>> restApiModelMap = getRestApiModelsByKey();
        return restApiModelMap.get(getKey(serverName,methodName));
    }

    /**
     * 替换所有接口缓存
     * @param restApiModelMap
     */
    public void saveRestApiCacheModelMap(Map<String, List<RestApiCacheModel>> restApiModelMap){
        marsContext.remove(REST_API_KEY);
        marsContext.setAttr(REST_API_KEY, restApiModelMap);
    }

    /**
     * 添加服务缓存
     * @param serverName
     * @param methodName
     * @param restApiCacheModel
     */
    public void addCache(String serverName, String methodName, RestApiCacheModel restApiCacheModel, boolean updateTime){
        String key = getKey(serverName,methodName);
        addCache(key, restApiCacheModel, updateTime);
    }

    /**
     * 添加服务缓存
     * @param restApiCacheModel
     */
    public synchronized void addCache(String key, RestApiCacheModel restApiCacheModel, boolean updateTime){
        Map<String, List<RestApiCacheModel>> restApiModelMap = getRestApiModelsByKey();

        List<RestApiCacheModel> restApiCacheModelList = restApiModelMap.get(key);
        if(restApiCacheModelList == null){
            restApiCacheModelList = new ArrayList<>();
        }
        RestApiCacheModel item = contains(restApiCacheModelList, restApiCacheModel);
        if(item != null){
            restApiCacheModelList.remove(item);
        }
        if(updateTime){
            restApiCacheModel.setCreateTime(new Date());
        }
        restApiCacheModelList.add(restApiCacheModel);

        restApiModelMap.put(key, restApiCacheModelList);
        marsContext.setAttr(REST_API_KEY, restApiModelMap);

    }

    /**
     * 获取本地的所有服务缓存
     * @return
     */
    public Map<String, List<RestApiCacheModel>> getRestApiModelsByKey(){
        Map<String, List<RestApiCacheModel>> restApiModelMap = new ConcurrentHashMap<>();
        Object objs = marsContext.getAttr(REST_API_KEY);
        if(objs != null){
            restApiModelMap = (Map<String, List<RestApiCacheModel>>)objs;
        }
        return restApiModelMap;
    }

    /**
     * 获取key
     * @param serverName
     * @param methodName
     * @return
     */
    public String getKey(String serverName, String methodName){
        return serverName + "-" + methodName;
    }

    /**
     * 判断本地是否已经有这个缓存了
     * @param restApiCacheModelList
     * @param restApiCacheModel
     * @return
     */
    private RestApiCacheModel contains(List<RestApiCacheModel> restApiCacheModelList, RestApiCacheModel restApiCacheModel){
        if(restApiCacheModelList == null || restApiCacheModelList.size() < 1){
            return null;
        }
        for(RestApiCacheModel item : restApiCacheModelList){
            if(item.getUrl().equals(restApiCacheModel.getUrl()) && item.getMethodName().equals(restApiCacheModel.getMethodName())){
                return item;
            }
        }
        return null;
    }
}
