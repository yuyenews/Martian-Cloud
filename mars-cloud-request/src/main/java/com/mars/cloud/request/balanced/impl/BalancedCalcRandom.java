package com.mars.cloud.request.balanced.impl;

import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.request.balanced.BalancedCalc;
import com.mars.cloud.util.RandomUtil;

import java.util.List;

/**
 * 随机
 */
public class BalancedCalcRandom implements BalancedCalc {

    @Override
    public RestApiCacheModel getRestApiCacheModel(String serverName, String methodName, List<RestApiCacheModel> restApiCacheModelList) {
        int index = getRandomIndex(restApiCacheModelList.size());

        return restApiCacheModelList.get(index);
    }

    /**
     * 随机算法
     *
     * @return 下标
     */
    private int getRandomIndex(int size) {
        return RandomUtil.getIndex(size);
    }
}
