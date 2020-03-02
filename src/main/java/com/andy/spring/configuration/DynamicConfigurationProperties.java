package com.andy.spring.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 动态配置自动更新配置 消息总线通过redis订阅发布实现 主要是redis总线配置
 *
 * @author 庞先海 2019-12-21
 */
@Data
@ConfigurationProperties(prefix = "spring.cloud.config.dynamic.redis")
public class DynamicConfigurationProperties {

    /**
     * 刷新通道
     */
    private String refreshChannel = "dynamic-configuration-refresh";
    /**
     * redis连接地址
     */
    private String redisHostUri;
    /**
     * 超时时间
     */
    private int timeout = 30000;

    /**
     * 最大连接数
     */
    private int maxTotal = 8;
    /**
     * 最大空闲连接数
     */
    private int maxIdle = 8;
    /**
     * 初始化连接数
     */
    private int minIdle = 0;
    /**
     * 当资源池用尽后，调用者是否要等待。只有当为true时，下面的maxWaitMillis才会生效
     */
    private boolean blockWhenExhausted = true;
    /**
     * 最大等待时间
     */
    private long maxWaitMillis = 60000;
    /**
     * 对拿到的connection进行validateObject校验
     */
    private boolean testOnBorrow = false;
    /**
     * 在进行returnObject对返回的connection进行validateObject校验
     */
    private boolean testOnReturn = false;
    /**
     * 定时对线程池中空闲的链接进行validateObject校验
     */
    private boolean testWhileIdle = true;
    /**
     * 空闲资源的检测周期(单位为毫秒) 默认-1 不检测
     */
    private long timeBetweenEvictionRunsMillis = 30000;
    /**
     * 连接空闲多久后释放(毫秒),
     */
    private long softMinEvictableIdleTimeMillis = 10;

}
