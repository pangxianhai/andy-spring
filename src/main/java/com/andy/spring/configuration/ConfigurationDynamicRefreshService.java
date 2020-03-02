package com.andy.spring.configuration;

import com.andy.spring.executor.ExecutorUtil;
import com.andy.spring.redis.RedisDAO;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.context.ApplicationContext;
import redis.clients.jedis.JedisPubSub;

/**
 * 接受配置配置更新事件广播 刷新本地bean
 *
 * @author 庞先海 2019-12-21
 */
@Slf4j
public class ConfigurationDynamicRefreshService extends JedisPubSub {

    private final ApplicationContext applicationContext;

    private final DynamicConfigurationProperties dynamicConfigurationProperties;

    public ConfigurationDynamicRefreshService(RedisDAO dynamicConfigurationRedisDAO,
        ApplicationContext applicationContext, DynamicConfigurationProperties dynamicConfigurationProperties) {
        this.applicationContext = applicationContext;
        this.dynamicConfigurationProperties = dynamicConfigurationProperties;
        ExecutorService executorService = ExecutorUtil.creatPoolExecutor("dynamic-configuration-thread", 2, 2, 1000L,
            20);
        executorService.execute(
            () -> dynamicConfigurationRedisDAO.subscribe(this, dynamicConfigurationProperties.getRefreshChannel()));
    }

    @Override
    public void onMessage(String channel, String message) {
        log.info("接受 广播 refresh channel " + channel);
        if (! StringUtils.equals(channel, dynamicConfigurationProperties.getRefreshChannel())) {
            return;
        }
        RefreshEndpoint refreshEndpoint = this.applicationContext.getBean(RefreshEndpoint.class);
        refreshEndpoint.refresh();
    }
}
