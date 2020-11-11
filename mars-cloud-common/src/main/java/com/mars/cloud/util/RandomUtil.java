package com.mars.cloud.util;

import java.util.Random;

/**
 * 随机数工具类
 */
public class RandomUtil {

    private static Random random = new Random();

    /**
     * 获取[0-length]的随机数
     * @param length
     * @return
     */
    public static int getIndex(int length){
        int index = random.nextInt(length);
        if(index < 0){
            return 0;
        } else if(index >= length){
            return length - 1;
        }
        return index;
    }
}
