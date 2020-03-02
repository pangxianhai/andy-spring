package com.andy.spring.redis;

import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis调用器
 *
 * @author 庞先海 2019-11-22
 */

public class RedisCaller {

    private static Logger logger = LoggerFactory.getLogger(RedisCaller.class);

    private final static int RETRY_TIMES = 5;

    public static <T> T call(JedisPool jedisPool, Function<Jedis, T> function) {
        for (int i = 0; i < RETRY_TIMES; ++ i) {
            try (Jedis jedis = jedisPool.getResource()) {
                try {
                    return function.apply(jedis);
                } catch (Exception ee) {
                    //这里是执行命令失败
                    throw new RuntimeException(ee);
                }
            } catch (Exception e) {
                //获取连接失败  发生异常时重试  最多RETRY_TIMES次
                logger.error("connection redis failed", e);
            }
        }
        return null;
    }

    public static void setLogger(Logger logger1) {
        if (logger != null) {
            logger = logger1;
        }
    }
}


