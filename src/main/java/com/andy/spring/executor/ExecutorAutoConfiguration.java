package com.andy.spring.executor;

import java.util.concurrent.ExecutorService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 线程池自动装配
 *
 * @author 庞先海 2019-11-17
 */
@EnableConfigurationProperties(ExecutorProperties.class)
public class ExecutorAutoConfiguration {

    @Bean(name = "executorService")
    public ExecutorService getExecutorService(ExecutorProperties executorProperties) {
        return ExecutorUtil.creatPoolExecutor(executorProperties.getThreadName(), executorProperties.getCorePoolSize(),
            executorProperties.getMaximumPoolSize(), executorProperties.getKeepAliveTime(),
            executorProperties.getQueueSize());
    }
}
