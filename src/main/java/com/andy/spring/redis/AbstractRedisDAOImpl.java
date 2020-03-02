package com.andy.spring.redis;


import com.andy.spring.util.RandomUtil;
import com.andy.spring.util.JsonUtil;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

/**
 * redis访问DAO 实现
 *
 * @author 庞先海 2019-11-22
 */
public abstract class AbstractRedisDAOImpl implements RedisDAO {

    /**
     * redis 链接池
     */
    protected JedisPool jedisPool;

    protected final static String TYPE_NONE = "none";
    protected final static String TYPE_STRING = "string";
    protected final static String TYPE_LIST = "list";
    protected final static String TYPE_SET = "set";

    /**
     * 默认失效时间
     */
    protected static int DEFAULT_EXPIRE_TIME = 3600 * 24 * 30;

    public AbstractRedisDAOImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void setDefaultExpireTime(int expireTime) {
        DEFAULT_EXPIRE_TIME = expireTime;
    }

    @Override
    public Long expire(String key, Integer expireSeconds) {
        return RedisCaller.call(jedisPool, jedis -> jedis.expire(key, expireSeconds));
    }

    @Override
    public Long expireat(String key, Integer expireTimestampSeconds) {
        return RedisCaller.call(jedisPool, jedis -> jedis.expireAt(key, expireTimestampSeconds));
    }

    @Override
    public Long pexpire(String key, Long expireMills) {
        return RedisCaller.call(jedisPool, jedis -> jedis.pexpire(key, expireMills));
    }

    @Override
    public Long pexpireat(String key, Integer expireTimestampMills) {
        return RedisCaller.call(jedisPool, jedis -> jedis.pexpireAt(key, expireTimestampMills));
    }

    @Override
    public void setString(String key, String value) {
        setString(key, value, getDefaultExpireTime());
    }

    @Override
    public void setStringNever(String key, String value) {
        RedisCaller.call(jedisPool, jedis -> jedis.set(key, value));
    }

    @Override
    public void setString(String key, String value, int seconds) {
        if (seconds <= 0) {
            throw new RuntimeException("expire seconds must greater than 0. your setting is: " + seconds);
        }
        RedisCaller.call(jedisPool, jedis -> {
            String result = jedis.set(key, value);
            jedis.expire(key, seconds);
            return result;
        });
    }

    @Override
    public String getString(String key) {
        return RedisCaller.call(jedisPool, jedis -> jedis.get(key));
    }

    @Override
    public Long ttl(String key) {
        return RedisCaller.call(jedisPool, jedis -> jedis.ttl(key));
    }

    @Override
    public void setObject(String key, Object value) {
        this.setObject(key, value, getDefaultExpireTime());
    }

    @Override
    public void setObjectNever(String key, Object value) {
        if (value == null) {
            return;
        }
        String valueStr = JsonUtil.toJson(value);
        this.setStringNever(key, valueStr);
    }

    @Override
    public void setObject(String key, Object value, int seconds) {
        if (value == null) {
            return;
        }
        String valueStr = JsonUtil.toJson(value);
        this.setString(key, valueStr, seconds);
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        String valueStr = this.getString(key);
        if (StringUtils.isBlank(valueStr)) {
            return null;
        }
        return JsonUtil.fromJson(valueStr, clazz);
    }

    @Override
    public <T> List<T> getListObject(String key, Class<T> clazz) {
        String valueStr = this.getString(key);
        if (StringUtils.isBlank(valueStr)) {
            return null;
        }
        return JsonUtil.fromJsonList(valueStr, clazz);
    }

    @Override
    public Long incrBy(String key, long integer) {
        return this.incrBy(key, integer, getDefaultExpireTime());
    }

    @Override
    public Long incrBy(String key, long integer, int seconds) {
        return RedisCaller.call(jedisPool, jedis -> {
            Long result = jedis.incrBy(key, integer);
            jedis.expire(key, seconds);
            return result;
        });
    }


    @Override
    public Double incrByFloat(String key, double integer) {
        return incrByFloat(key, integer, getDefaultExpireTime());
    }

    @Override
    public Double incrByFloat(String key, double integer, int seconds) {
        return RedisCaller.call(jedisPool, jedis -> {
            Double result = jedis.incrByFloat(key, integer);
            jedis.expire(key, seconds);
            return result;
        });
    }

    @Override
    public Long incr(String key) {
        return incr(key, getDefaultExpireTime());
    }

    @Override
    public Long incr(String key, int seconds) {
        return RedisCaller.call(jedisPool, jedis -> {
            Long result = jedis.incr(key);
            jedis.expire(key, seconds);
            return result;
        });
    }

    @Override
    public Long decrBy(String key, long integer) {
        return decrBy(key, integer, getDefaultExpireTime());
    }

    @Override
    public Long decrBy(String key, long integer, int seconds) {
        return RedisCaller.call(jedisPool, jedis -> {
            Long result = jedis.decrBy(key, integer);
            jedis.expire(key, seconds);
            return result;
        });
    }

    @Override
    public Long decr(String key) {
        return decr(key, getDefaultExpireTime());
    }

    @Override
    public Long decr(String key, int seconds) {
        return RedisCaller.call(jedisPool, jedis -> {
            Long result = jedis.decr(key);
            jedis.expire(key, seconds);
            return result;
        });
    }

    @Override
    public Long append(String key, String value) {
        return append(key, value, getDefaultExpireTime());
    }

    @Override
    public Long append(String key, String value, int seconds) {
        return RedisCaller.call(jedisPool, jedis -> {
            Long result = jedis.append(key, value);
            jedis.expire(key, seconds);
            return result;
        });
    }

    @Override
    public String substr(String key, int start, int end) {
        return RedisCaller.call(jedisPool, jedis -> jedis.substr(key, start, end));
    }

    @Override
    public String type(String key) {
        return RedisCaller.call(jedisPool, jedis -> jedis.type(key));
    }

    @Override
    public boolean exists(String key) {
        Boolean exists = RedisCaller.call(jedisPool, jedis -> jedis.exists(key));
        return exists != null && exists;
    }

    @Override
    public Long del(String key) {
        return RedisCaller.call(jedisPool, jedis -> jedis.del(key));
    }

    @Override
    public Long hset(String key, String field, String value) {
        return hset(key, field, value, getDefaultExpireTime());
    }

    @Override
    public Long hsetObject(String key, String field, Object value) {
        return hset(key, field, JsonUtil.toJson(value), getDefaultExpireTime());
    }

    @Override
    public Long hsetNever(String key, String field, String value) {
        return RedisCaller.call(jedisPool, jedis -> jedis.hset(key, field, value));
    }

    @Override
    public Long hsetObjectNever(String key, String field, Object value) {
        return hsetNever(key, field, JsonUtil.toJson(value));
    }

    @Override
    public Long hset(String key, String field, String value, int seconds) {
        return RedisCaller.call(jedisPool, jedis -> {
            Long result = jedis.hset(key, field, value);
            jedis.expire(key, seconds);
            return result;
        });
    }

    @Override
    public String hget(String key, String field) {
        return RedisCaller.call(jedisPool, jedis -> jedis.hget(key, field));
    }

    @Override
    public <T> T hgetObject(String key, String field, Class<T> clazz) {
        String valueStr = this.hget(key, field);
        if (StringUtils.isBlank(valueStr)) {
            return null;
        }
        return JsonUtil.fromJson(valueStr, clazz);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return RedisCaller.call(jedisPool, jedis -> jedis.hgetAll(key));
    }

    @Override
    public Long hsetnx(String key, String field, String value) {
        return hsetnx(key, field, value, getDefaultExpireTime());
    }

    @Override
    public Long hsetnxNever(String key, String field, String value) {
        return RedisCaller.call(jedisPool, jedis -> jedis.hsetnx(key, field, value));
    }

    @Override
    public Long hsetnx(String key, String field, String value, int seconds) {
        return RedisCaller.call(jedisPool, jedis -> {
            Long result = jedis.hsetnx(key, field, value);
            jedis.expire(key, seconds);
            return result;
        });
    }

    @Override
    public String hmset(String key, Map<String, String> hash) {
        return hmset(key, hash, getDefaultExpireTime());
    }

    @Override
    public String hmsetNever(String key, Map<String, String> hash) {
        return RedisCaller.call(jedisPool, jedis -> jedis.hmset(key, hash));
    }

    @Override
    public String hmset(String key, Map<String, String> hash, int seconds) {
        return RedisCaller.call(jedisPool, jedis -> {
            String result = jedis.hmset(key, hash);
            jedis.expire(key, seconds);
            return result;
        });
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        return RedisCaller.call(jedisPool, jedis -> jedis.hmget(key, fields));
    }

    @Override
    public Long hincrBy(String key, String field, long value) {
        return hincrBy(key, field, value, getDefaultExpireTime());
    }

    @Override
    public Long hincrBy(String key, String field, long value, int seconds) {
        return RedisCaller.call(jedisPool, jedis -> {
            Long result = jedis.hincrBy(key, field, value);
            jedis.expire(key, seconds);
            return result;
        });
    }

    @Override
    public Double hincrByFloat(String key, String field, double value) {
        return hincrByFloat(key, field, value, getDefaultExpireTime());
    }

    @Override
    public Double hincrByFloat(String key, String field, double value, int seconds) {
        return RedisCaller.call(jedisPool, jedis -> {
            Double resultValue = jedis.hincrByFloat(key, field, value);
            jedis.expire(key, seconds);
            return resultValue;
        });
    }

    @Override
    public boolean hexists(String key, String field) {
        Boolean exists = RedisCaller.call(jedisPool, jedis -> jedis.hexists(key, field));
        return exists != null && exists;
    }

    @Override
    public Long hdel(String key, String... fields) {
        return RedisCaller.call(jedisPool, jedis -> jedis.hdel(key, fields));
    }

    @Override
    public Long lpushNever(String key, String... values) {
        return RedisCaller.call(jedisPool, jedis -> jedis.lpush(key, values));
    }

    @Override
    public Long lpush(String key, String... values) {
        return lpush(key, getDefaultExpireTime(), values);
    }

    @Override
    public Long lpush(String key, int seconds, String... values) {
        return RedisCaller.call(jedisPool, jedis -> {
            Long result = jedis.lpush(key, values);
            jedis.expire(key, seconds);
            return result;
        });
    }

    @Override
    public Long rpush(String key, String... values) {
        return rpush(key, getDefaultExpireTime(), values);
    }

    @Override
    public Long rpushNever(String key, String... values) {
        return RedisCaller.call(jedisPool, jedis -> jedis.rpush(key, values));
    }

    @Override
    public Long rpush(String key, int seconds, String... values) {
        return RedisCaller.call(jedisPool, jedis -> {
            Long result = jedis.rpush(key, values);
            jedis.expire(key, seconds);
            return result;
        });
    }

    @Override
    public String lpop(String key) {
        return RedisCaller.call(jedisPool, jedis -> jedis.lpop(key));
    }

    @Override
    public String rpop(String key) {
        return RedisCaller.call(jedisPool, jedis -> jedis.rpop(key));
    }

    @Override
    public Long lrem(String key, int count, String value) {
        return RedisCaller.call(jedisPool, jedis -> jedis.lrem(key, count, value));
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        return RedisCaller.call(jedisPool, jedis -> jedis.lrange(key, start, end));
    }

    @Override
    public Long llen(String key) {

        return RedisCaller.call(jedisPool, jedis -> jedis.llen(key));
    }

    @Override
    public Long sadd(String key, String... values) {
        return sadd(key, getDefaultExpireTime(), values);
    }

    @Override
    public Long sadd(String key, int seconds, String... values) {
        return RedisCaller.call(jedisPool, jedis -> {
            Long result = jedis.sadd(key, values);
            jedis.expire(key, seconds);
            return result;
        });
    }

    @Override
    public Long saddNever(String key, String... values) {
        return RedisCaller.call(jedisPool, jedis -> jedis.sadd(key, values));
    }

    @Override
    public Set<String> smembers(String key) {
        return RedisCaller.call(jedisPool, jedis -> jedis.smembers(key));
    }

    @Override
    public Long srem(String key, String... values) {
        return RedisCaller.call(jedisPool, jedis -> jedis.srem(key, values));
    }

    @Override
    public Long setnx(String key, String value, int seconds) {
        return RedisCaller.call(jedisPool, jedis -> {
            Long result = jedis.setnx(key, value);
            jedis.expire(key, seconds);
            return result;
        });
    }

    @Override
    public Long setnx(String key, String value) {
        return setnx(key, value, DEFAULT_EXPIRE_TIME);
    }

    @Override
    public Long setnxNever(String key, String value) {
        return RedisCaller.call(jedisPool, jedis -> jedis.setnx(key, value));
    }

    @Override
    public Long publish(String channel, String message) {
        return RedisCaller.call(jedisPool, jedis -> jedis.publish(channel, message));
    }

    @Override
    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        RedisCaller.call(jedisPool, (jedis) -> {
            jedis.subscribe(jedisPubSub, channels);
            return true;
        });
    }

    @Override
    public <T> T jedis(Function<Jedis, T> function) {

        return RedisCaller.call(jedisPool, function);
    }

    protected int getDefaultExpireTime() {
        return this.getExpireTime(DEFAULT_EXPIRE_TIME);
    }

    protected int getExpireTime(int seconds) {
        if (seconds / 10 <= 0) {
            return seconds + RandomUtil.getRandom(5);
        } else {
            return seconds + RandomUtil.getRandom(seconds / 10);
        }
    }
}
