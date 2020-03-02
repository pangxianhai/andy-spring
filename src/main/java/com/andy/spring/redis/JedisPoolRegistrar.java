package com.andy.spring.redis;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

import com.andy.spring.redis.RedisProperties.RedisHostProperties;
import com.andy.spring.util.BinderUtil;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisPool;

/**
 * jedis bean 注册
 *
 * @author 庞先海 2019-11-22
 */
public class JedisPoolRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private static Logger logger = LoggerFactory.getLogger(JedisPoolRegistrar.class);

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
            annotationMetadata.getAnnotationAttributes(EnableRedis.class.getName()));
        if (null == attributes) {
            return;
        }
        RedisProperties redisProperties = BinderUtil.bind(environment, RedisProperties.class);

        if (null == redisProperties || CollectionUtils.isEmpty(redisProperties.getHostList())) {
            logger.warn("未加载到redis配置");
            return;
        }
        GenericObjectPoolConfig poolConfig = this.buildPoolConfig(redisProperties);
        for (RedisHostProperties hostProperties : redisProperties.getHostList()) {
            JedisPool jedisPool = this.jedisPoolRegister(hostProperties, poolConfig);
            this.redisDaoRegister(jedisPool, hostProperties.getRedisAlias(), registry);
        }
    }

    private void redisDaoRegister(JedisPool jedisPool, String redisAlias, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = rootBeanDefinition(RedisDAOImpl.class);
        builder.addConstructorArgValue(jedisPool);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        String beanName = redisAlias + "RedisDAO";
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    private JedisPool jedisPoolRegister(RedisHostProperties properties, GenericObjectPoolConfig poolConfig) {
        URI uri;
        try {
            uri = new URI(properties.getHostUri());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return new JedisPool(poolConfig, uri, properties.getTimeout());
    }

    private GenericObjectPoolConfig buildPoolConfig(RedisProperties properties) {
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
