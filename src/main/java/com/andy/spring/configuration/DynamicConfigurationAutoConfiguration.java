package com.andy.spring.configuration;

import com.andy.spring.redis.RedisDAO;
import com.andy.spring.redis.RedisDAOImpl;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.JedisPool;

/**
 * 动态配置自动更新 自动注解
 *
 * @author 庞先海 2019-12-21
 */
@EnableConfigurationProperties(DynamicConfigurationProperties.class)
public class DynamicConfigurationAutoConfiguration {

    private final ApplicationContext applicationContext;

    public DynamicConfigurationAutoConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean(name = "dynamicConfigurationRedisDAO")
    public RedisDAO dynamicConfigurationRedisDAO(DynamicConfigurationProperties properties) {
        GenericObjectPoolConfig poolConfig = this.buildPoolConfig(properties);
        JedisPool jedisPool = this.jedisPoolRegister(properties, poolConfig);
        return new RedisDAOImpl(jedisPool);
    }

    @Bean
    public ConfigurationDynamicRefreshService configurationDynamicRefreshService(RedisDAO dynamicConfigurationRedisDAO,
        DynamicConfigurationProperties properties) {
        return new ConfigurationDynamicRefreshService(dynamicConfigurationRedisDAO, this.applicationContext,
            properties);
    }

    @Bean
    public ConfigurationDynamicRefreshEndpoint configurationDynamicRefreshEndpoint(
        RedisDAO dynamicConfigurationRedisDAO, DynamicConfigurationProperties dynamicConfigurationProperties) {
        return new ConfigurationDynamicRefreshEndpoint(dynamicConfigurationRedisDAO, dynamicConfigurationProperties);
    }

    private JedisPool jedisPoolRegister(DynamicConfigurationProperties properties, GenericObjectPoolConfig poolConfig) {
        URI uri;
        try {
            uri = new URI(properties.getRedisHostUri());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return new JedisPool(poolConfig, uri, properties.getTimeout());
    }

    private GenericObjectPoolConfig buildPoolConfig(DynamicConfigurationProperties properties) {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(properties.getMaxTotal());
        genericObjectPoolConfig.setMaxIdle(properties.getMaxIdle());
        genericObjectPoolConfig.setMinIdle(properties.getMinIdle());
        genericObjectPoolConfig.setBlockWhenExhausted(properties.isBlockWhenExhausted());
        genericObjectPoolConfig.setMaxWaitMillis(properties.getMaxWaitMillis());
        genericObjectPoolConfig.setTestOnBorrow(properties.isTestOnBorrow());
        genericObjectPoolConfig.setTestOnReturn(properties.isTestOnReturn());
        genericObjectPoolConfig.setTestWhileIdle(properties.isTestWhileIdle());
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
        //连接空闲多久后释放(毫秒),
        genericObjectPoolConfig.setSoftMinEvictableIdleTimeMillis(properties.getSoftMinEvictableIdleTimeMillis());
        return genericObjectPoolConfig;
    }
}
