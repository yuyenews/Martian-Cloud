package com.mars.cloud.core.cache;

import com.mars.cloud.model.RestApiCacheModel;
import com.mars.common.constant.MarsSpace;

import java.util.*;
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
    public void addCache(String serverName, String methodName, RestApiCacheModel restApiCacheModel){
        String key = getKey(serverName,methodName);
        addCache(key, restApiCacheModel);
    }

    /**
     * 添加服务缓存
     * @param restApiCacheModel
     */
    public synchronized void addCache(String key, RestApiCacheModel restApiCacheModel){
        Map<String, List<RestApiCacheModel>> restApiModelMap = getRestApiModelsByKey();

        List<RestApiCacheModel> restApiCacheModelList = restApiModelMap.get(key);
        if(restApiCacheModelList == null){
            restApiCacheModelList = Collections.synchronizedList(new ArrayList<>());
        }
        RestApiCacheModel item = contains(restApiCacheModelList, restApiCacheModel);
        if(item != null){
            if(item.getCreateTime() < restApiCacheModel.getCreateTime()){
                /* 如果刚拿来的元素，创建时间比较新，那就重置创建时间 */
                item.setCreateTime(restApiCacheModel.getCreateTime());
            }

            /* 只要元素存在，就return，不往下执行 */
            return;
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
    public static String getKey(String serverName, String methodName){
        return serverName + "-" + methodName;
    }

    /**
     * 从key里面提取服务名词
     * @param key
     * @return
     */
    public static String getServerNameFormKey(String key){
        return key.split("-")[0];
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
