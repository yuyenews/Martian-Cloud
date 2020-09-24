package com.mars.cloud.core.cache;

import com.alibaba.fastjson.JSON;
import com.mars.cloud.constant.MarsCloudConstant;
import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.core.helper.ZkHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 刷新本地服务缓存
 */
public class LoadServerCache {

    private ServerApiCache serverApiCache = new ServerApiCache();

    /**
     * 从注册中心读取接口，放入本地缓存
     */
    public void doLoad() throws Exception {
        Map<String, List<RestApiCacheModel>> restApiModelMap = doRefreshCacheApi();
        serverApiCache.saveCache(restApiModelMap);
    }

    /**
     * 根据服务名称和方法名称获取接口集
     * @param serverName
     * @param methodName
     * @return
     * @throws Exception
     */
    public List<RestApiCacheModel> getRestApiCacheModelByServerName(String serverName, String methodName) throws Exception{
        String node = MarsCloudConstant.SERVER_NODE
                .replace("{serverName}", serverName)
                .replace("{method}", methodName);
        return getRestApiCacheModel(node);
    }

    /**
     * 从zookeeper里把所有的接口都拉取下来
     * @return 所有的接口
     * @throws Exception 异常
     */
    public synchronized Map<String,List<RestApiCacheModel>> doRefreshCacheApi() throws Exception {
        /* 如果zk没有打开连接的话 就打开一下 */
        ZkHelper.openConnection();

        Map<String,List<RestApiCacheModel>> urlMap = new ConcurrentHashMap<>();

        /* 获取根节点下的所有子节点 */
        List<String> nodeList = ZkHelper.getChildren(MarsCloudConstant.BASE_SERVER_NODE);
        if(nodeList == null || nodeList.size() < 1){
            return null;
        }

        for(String node : nodeList){
            String childrenNodePath = MarsCloudConstant.BASE_SERVER_NODE+"/"+node;
            List<RestApiCacheModel> restApiCacheModels = getRestApiCacheModel(childrenNodePath);
            if (restApiCacheModels == null || restApiCacheModels.size() < 1) {
                continue;
            }
            urlMap.put(childrenNodePath, restApiCacheModels);
        }
        return urlMap;
    }

    /**
     * 获取节点下的数据
     * @param childrenNodePath
     * @return
     * @throws Exception
     */
    private List<RestApiCacheModel> getRestApiCacheModel(String childrenNodePath) throws Exception {
        /* 获取每一个子节点下的所有子节点 */
        List<String> childrenNodeList = ZkHelper.getChildren(childrenNodePath);
        if(childrenNodeList == null || childrenNodeList.size() < 1){
            return null;
        }

        /* 将获取到的节点的数据取出来 */
        List<RestApiCacheModel> restApiCacheModelList = new ArrayList<>();
        for(String childrenNode : childrenNodeList){
            String cacheNodePath = childrenNodePath + "/" + childrenNode;
            String dataJsonStr = ZkHelper.getData(cacheNodePath);
            if(dataJsonStr == null || dataJsonStr.equals("")){
                continue;
            }
            RestApiCacheModel restApiCacheModel = JSON.parseObject(dataJsonStr).toJavaObject(RestApiCacheModel.class);
            restApiCacheModelList.add(restApiCacheModel);
        }
        return restApiCacheModelList;
    }
}
