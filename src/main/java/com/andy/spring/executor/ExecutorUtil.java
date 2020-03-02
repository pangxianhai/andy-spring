package com.andy.spring.executor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池创建工具
 *
 * @author 庞先海 2019-11-17
 */
public class ExecutorUtil {

    public static ExecutorService creatPoolExecutor(String name, Integer coreSize, Integer maxPoolSize, Long keepTime,
        Integer queueSize) {
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat(name + "-%d").build();
        return new ThreadPoolExecutor(coreSize, maxPoolSize, keepTime, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(queueSize), nameThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
