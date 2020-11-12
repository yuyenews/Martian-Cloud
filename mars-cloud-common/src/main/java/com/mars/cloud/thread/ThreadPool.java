package com.mars.cloud.thread;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 */
public class ThreadPool {

    private static int coreSize = 10;

    private static int maxSize = 1000;

    private static long aliveTime = 5000;

    private static int dequeSize = maxSize - coreSize;

    private static ThreadPoolExecutor poolExecutor;

    /**
     * 获取线程池
     * @return
     */
    public static synchronized ThreadPoolExecutor getThreadPoolExecutor(){
        if(poolExecutor == null){
            poolExecutor = new ThreadPoolExecutor(coreSize, maxSize,aliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(dequeSize));
        }
        return poolExecutor;
    }
}
