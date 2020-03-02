package com.andy.spring.configuration;

import com.andy.spring.redis.RedisDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

/**
 * 配置更新总线 将配置更新事件广播
 * 通过Endpoint注入到actuator 通过http://host:port/actuator/configuration-bus 调用该服务的refresh方法
 *
 * @author 庞先海 2019-12-22
 */
@Endpoint(id = "configuration-bus")
@Slf4j
public class ConfigurationDynamicRefreshEndpoint {

    private final RedisDAO dynamicConfigurationRedisDAO;

    private final DynamicConfigurationProperties dynamicConfigurationProperties;

    public ConfigurationDynamicRefreshEndpoint(RedisDAO dynamicConfigurationRedisDAO,
        DynamicConfigurationProperties dynamicConfigurationProperties) {
        this.dynamicConfigurationRedisDAO = dynamicConfigurationRedisDAO;
        this.dynamicConfigurationProperties = dynamicConfigurationProperties;
    }

    @WriteOperation
    public String refresh() {
        dynamicConfigurationRedisDAO.publish(dynamicConfigurationProperties.getRefreshChannel(), "");
        log.info("广播 refresh channel : " + dynamicConfigurationProperties.getRefreshChannel());
        return "refresh channel : " + dynamicConfigurationProperties.getRefreshChannel();
    }

}
