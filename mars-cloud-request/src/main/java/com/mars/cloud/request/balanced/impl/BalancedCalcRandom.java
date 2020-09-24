package com.mars.cloud.request.balanced.impl;

import com.mars.cloud.core.cache.model.RestApiCacheModel;
import com.mars.cloud.request.balanced.BalancedCalc;

import java.util.List;
import java.util.Random;

/**
 * 随机
 */
public class BalancedCalcRandom implements BalancedCalc {

    private Random random = new Random();


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
        int index = 0;
        if (size > 1) {
            index = random.nextInt(size);
        }

        if (index < 0) {
            index = 0;
        }
        return index;
    }
}
