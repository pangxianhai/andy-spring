package com.andy.spring.redis;

import com.andy.spring.util.JsonUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisPool;

/**
 * redis访问DAO 提供非击穿缓存的方法
 *
 * @author 庞先海 2019-11-22
 */
public class RedisDAOImpl extends AbstractRedisDAOImpl implements RedisDAO {

    private static Logger logger = LoggerFactory.getLogger(RedisDAOImpl.class);

    private final static String EMPTY_VALUE = "null";

    private final static int EXPIRE_TIME = 60 * 10;

    public RedisDAOImpl(JedisPool jedisPool) {
        super(jedisPool);
    }

    @Override
    public <T> T cacheObject(String key, Class<T> clazz, Supplier<T> supplier) {
        return cacheObject(key, getDefaultExpireTime(), clazz, supplier);
    }

    @Override
    public <T> T cacheObject(String key, int seconds, Class<T> clazz, Supplier<T> supplier) {
        String value = this.getString(key);
        if (StringUtils.isEmpty(value)) {
            T realValue = supplier.get();
            if (realValue == null) {
                this.setString(key, EMPTY_VALUE, getExpireTime());
            } else {
                this.setString(key, JsonUtil.toJson(realValue), getExpireTime(seconds));
            }
            return realValue;
        } else if (EMPTY_VALUE.equals(value)) {
            return null;
        } else {
            return JsonUtil.fromJson(value, clazz);
        }
    }

    @Override
    public <T> T cacheObjectNever(String key, Class<T> clazz, Supplier<T> supplier) {
        String value = this.getString(key);
        if (StringUtils.isEmpty(value)) {
            T realValue = supplier.get();
            if (realValue == null) {
                this.setString(key, EMPTY_VALUE, getExpireTime());
            } else {
                this.setStringNever(key, JsonUtil.toJson(realValue));
            }
            return realValue;
        } else if (EMPTY_VALUE.equals(value)) {
            return null;
        } else {
            return JsonUtil.fromJson(value, clazz);
        }
    }

    @Override
    public void setCacheObject(String key, int seconds, Object value) {
        if (null == value) {
            return;
        }
        String valueStr = JsonUtil.toJson(value);
        this.setString(key, valueStr, getExpireTime(seconds));
    }

    @Override
    public void setCacheObject(String key, Object value) {
        this.setCacheObject(key, getExpireTime(), value);
    }

    @Override
    public void setCacheObjectNever(String key, Object value) {
        if (null == value) {
            return;
        }
        String valueStr = JsonUtil.toJson(value);
        this.setStringNever(key, valueStr);
    }

    @Override
    public void deleteCacheObject(String key) {
        this.del(key);
    }

    @Override
    public <T> List<T> cacheList(String key, Class<T> clazz, Supplier<List<T>> supplier) {
        return cacheList(key, getDefaultExpireTime(), clazz, supplier);
    }

    @Override
    public <T> List<T> cacheList(String key, int seconds, Class<T> clazz, Supplier<List<T>> supplier) {
        List<T> cacheResult = this.getCacheList(key, clazz);
        if (null != cacheResult) {
            return cacheResult;
        }
        //没有缓存的值
        List<T> realValue = supplier.get();
        if (CollectionUtils.isEmpty(realValue)) {
            this.setString(key, EMPTY_VALUE, getExpireTime());
        } else {
            for (T value : realValue) {
                this.lpush(key, seconds, JsonUtil.toJson(value));
            }
        }
        return realValue;
    }

    @Override
    public <T> List<T> cacheListNever(String key, Class<T> clazz, Supplier<List<T>> supplier) {
        List<T> cacheResult = this.getCacheList(key, clazz);
        if (null != cacheResult) {
            return cacheResult;
        }
        //没有缓存的值
        List<T> realValue = supplier.get();
        if (CollectionUtils.isEmpty(realValue)) {
            this.setString(key, EMPTY_VALUE, this.getExpireTime());
        } else {
            for (T value : realValue) {
                this.lpushNever(key, JsonUtil.toJson(value));
            }
        }
        return realValue;
    }

    @Override
    public <T> void addCacheList(String key, T value) {
        this.addCacheList(key, value, this.getDefaultExpireTime());
    }

    @Override
    public <T> void addCacheList(String key, T value, int seconds) {
        String keyType = this.type(key);
        if (TYPE_STRING.equals(keyType)) {
            this.del(key);
        }
        this.lpush(key, this.getExpireTime(seconds), JsonUtil.toJson(value));
    }

    @Override
    public <T> void addCacheNever(String key, T value) {
        String keyType = this.type(key);
        if (TYPE_STRING.equals(keyType)) {
            this.del(key);
        }
        this.lpushNever(key, JsonUtil.toJson(value));
    }

    @Override
    public <T> void deleteCacheList(String key, Class<T> clazz, T value) {
        List<T> cacheResult = this.getCacheList(key, clazz);
        if (CollectionUtils.isEmpty(cacheResult)) {
            return;
        }
        for (T t : cacheResult) {
            if (t.equals(value)) {
                this.lrem(key, 0, JsonUtil.toJson(t));
            }
        }
    }

    @Override
    public <T> T cacheHgetObject(String key, String field, Class<T> clazz, Supplier<T> supplier) {
        return cacheHgetObject(key, field, getDefaultExpireTime(), clazz, supplier);
    }

    @Override
    public <T> T cacheHgetObject(String key, String field, int seconds, Class<T> clazz, Supplier<T> supplier) {
        String value = this.hget(key, field);
        if (StringUtils.isEmpty(value)) {
            T realValue = supplier.get();
            if (realValue == null) {
                this.hset(key, field, EMPTY_VALUE, this.getExpireTime(seconds));
            } else {
                this.hset(key, field, JsonUtil.toJson(realValue), this.getExpireTime(seconds));
            }
            return realValue;
        } else if (EMPTY_VALUE.equals(value)) {
            return null;
        } else {
            return JsonUtil.fromJson(value, clazz);
        }
    }

    @Override
    public <T> T cacheHgetObjectNever(String key, String field, Class<T> clazz, Supplier<T> supplier) {
        String value = this.hget(key, field);
        if (StringUtils.isEmpty(value)) {
            T realValue = supplier.get();
            if (realValue == null) {
                this.hsetNever(key, field, EMPTY_VALUE);
            } else {
                this.hsetNever(key, field, JsonUtil.toJson(realValue));
            }
            return realValue;
        } else if (EMPTY_VALUE.equals(value)) {
            return null;
        } else {
            return JsonUtil.fromJson(value, clazz);
        }
    }

    @Override
    public <T> List<T> cacheHmget(String key, Class<T> clazz, Supplier<Map<String, T>> supplier, String... field) {
        return this.cacheHmget(key, clazz, getExpireTime(), supplier, field);
    }

    @Override
    public <T> List<T> cacheHmget(String key, Class<T> clazz, int seconds, Supplier<Map<String, T>> supplier,
        String... field) {
        List<String> cacheList = this.hmget(key, field);
        if (CollectionUtils.isEmpty(cacheList)) {
            Map<String, T> valueMap = supplier.get();
            if (CollectionUtils.isEmpty(valueMap)) {
                return new ArrayList<>(0);
            }
            Map<String, String> strValueMap = new HashMap<>(valueMap.size());
            for (String k : valueMap.keySet()) {
                strValueMap.put(k, JsonUtil.toJson(valueMap.get(k)));
            }
            this.hmset(key, strValueMap, getExpireTime(seconds));
            return new ArrayList<>(valueMap.values());
        } else {
            return cacheList.stream().map(cache -> JsonUtil.fromJson(cache, clazz)).collect(Collectors.toList());
        }
    }


    @Override
    public <T> List<T> cacheHmgetNerver(String key, Class<T> clazz, Supplier<Map<String, T>> supplier,
        String... field) {
        List<String> cacheList = this.hmget(key, field);
        if (CollectionUtils.isEmpty(cacheList)) {
            Map<String, T> valueMap = supplier.get();
            if (CollectionUtils.isEmpty(valueMap)) {
                return new ArrayList<>(0);
            }
            Map<String, String> strValueMap = new HashMap<>(valueMap.size());
            for (String k : valueMap.keySet()) {
                strValueMap.put(k, JsonUtil.toJson(valueMap.get(k)));
            }
            this.hmsetNever(key, strValueMap);
            return new ArrayList<>(valueMap.values());
        } else {
            return cacheList.stream().map(cache -> JsonUtil.fromJson(cache, clazz)).collect(Collectors.toList());
        }
    }


    @Override
    public void cacheHsetObject(String key, String field, Object value) {
        this.cacheHsetObject(key, field, getExpireTime(), value);
    }

    @Override
    public void cacheHsetObject(String key, String field, int seconds, Object value) {
        String keyType = this.type(key);
        if (TYPE_STRING.equals(keyType)) {
            this.del(key);
        }
        this.hset(key, field, JsonUtil.toJson(value), seconds);
    }

    @Override
    public void cacheHsetObjectNever(String key, String field, Object value) {
        String keyType = this.type(key);
        if (TYPE_STRING.equals(keyType)) {
            this.del(key);
        }
        this.hsetNever(key, field, JsonUtil.toJson(value));
    }

    @Override
    public void deleteCacheHget(String key, String filed) {
        String keyType = this.type(key);
        if (TYPE_STRING.equals(keyType)) {
            return;
        }
        this.hdel(key, filed);
    }

    @Override
    public <T> Set<T> cacheSet(String key, Class<T> clazz, Supplier<Set<T>> supplier) {
        return cacheSet(key, clazz, getDefaultExpireTime(), supplier);
    }

    @Override
    public <T> Set<T> cacheSet(String key, Class<T> clazz, int seconds, Supplier<Set<T>> supplier) {
        Set<T> cacheResult = this.getCacheSet(key, clazz);
        if (null != cacheResult) {
            return cacheResult;
        }
        //没有缓存的值
        Set<T> realValue = supplier.get();
        if (CollectionUtils.isEmpty(realValue)) {
            this.setString(key, EMPTY_VALUE, getExpireTime());
        } else {
            for (T value : realValue) {
                this.sadd(key, seconds, JsonUtil.toJson(value));
            }
        }
        return realValue;
    }

    @Override
    public <T> Set<T> cacheSetNever(String key, Class<T> clazz, Supplier<Set<T>> supplier) {
        Set<T> cacheResult = this.getCacheSet(key, clazz);
        if (null != cacheResult) {
            return cacheResult;
        }
        //没有缓存的值
        Set<T> realValue = supplier.get();
        if (CollectionUtils.isEmpty(realValue)) {
            this.setString(key, EMPTY_VALUE, getExpireTime());
        } else {
            for (T value : realValue) {
                this.saddNever(key, JsonUtil.toJson(value));
            }
        }
        return realValue;
    }

    @Override
    public boolean lock(String key, String requestId, long timeout) {
        Boolean lock = RedisCaller.call(jedisPool, jedis -> {
            if (jedis == null) {
                return false;
            }
            String result = jedis.set(key, requestId, "NX", "PX", timeout);
            return "OK".equals(result);
        });
        return lock != null && lock;
    }

    @Override
    public boolean unlock(String key, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = RedisCaller.call(jedisPool, jedis -> {
            List<String> keys = Collections.singletonList(key);
            List<String> params = Collections.singletonList(requestId);
            return jedis.eval(script, keys, params);
        });
        return result instanceof Long && (Long)result == 1;
    }

    private int getExpireTime() {
        return getExpireTime(EXPIRE_TIME);
    }

    private <T> Set<T> getCacheSet(String key, Class<T> clazz) {
        String keyType = this.type(key);
        if (TYPE_STRING.equals(keyType)) {
            String valueString = this.getString(key);
            if (EMPTY_VALUE.equals(valueString)) {
                return new HashSet<>(0);
            }
        } else if (TYPE_SET.equals(keyType)) {
            Set<String> valueSet = this.smembers(key);
            if (! CollectionUtils.isEmpty(valueSet)) {
                Set<T> cacheResult = new HashSet<>(valueSet.size());
                for (String value : valueSet) {
                    cacheResult.add(JsonUtil.fromJson(value, clazz));
                }
                return cacheResult;
            }
        }
        return null;
    }

    private <T> List<T> getCacheList(String key, Class<T> clazz) {
        String keyType = this.type(key);
        if (TYPE_STRING.equals(keyType)) {
            String valueString = this.getString(key);
            if (EMPTY_VALUE.equals(valueString)) {
                return new ArrayList<>(0);
            }
        } else if (TYPE_LIST.equals(keyType)) {
            List<String> valueList = this.lrange(key, 0, - 1);
            if (! CollectionUtils.isEmpty(valueList)) {
                List<T> cacheResult = new ArrayList<>(valueList.size());
                for (String value : valueList) {
                    cacheResult.add(JsonUtil.fromJson(value, clazz));
                }
                return cacheResult;
            }
        }
        return null;
    }
}
