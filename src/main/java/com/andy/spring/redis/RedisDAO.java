package com.andy.spring.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * redis访问DAO
 *
 * @author 庞先海 2019-11-22
 */
@Repository
public interface RedisDAO {

    /**
     * 设置默认过期时间
     *
     * @param expireTime 默认过期时间
     * @author 庞先海 2017-12-23
     */
    void setDefaultExpireTime(int expireTime);

    /**
     * 设置某个 key 的过期时间，单位是秒
     *
     * @param key           对应的 key 字段
     * @param expireSeconds 过期的秒数
     * @return 1表示成功，0表示不存在或者不能设置过期时间
     */
    Long expire(String key, Integer expireSeconds);

    /**
     * 设置某个 key 的过期时间，为 unix 时间戳的秒钟
     *
     * @param key                    对应的 key
     * @param expireTimestampSeconds 存活到的 Unix 时间戳，单位秒
     * @return 1设置成功
     */
    Long expireat(String key, Integer expireTimestampSeconds);

    /**
     * 设置某个 key 的过期时间，单位是毫秒
     *
     * @param key         对应的 key 字段
     * @param expireMills 过期的毫秒
     * @return 1表示成功，0表示不存在或者不能设置过期时间
     */
    Long pexpire(String key, Long expireMills);

    /**
     * 设置某个 key 的过期时间，为 unix 时间戳的毫秒数
     *
     * @param key                  对应的 key
     * @param expireTimestampMills 存活到的 Unix 时间戳，单位毫秒
     * @return 1表示成功，0表示不存在或者不能设置过期时间
     */
    Long pexpireat(String key, Integer expireTimestampMills);

    /**
     * 设置字符串
     *
     * @param key   缓存key
     * @param value 缓存值
     * @author 庞先海 2017-07-17
     */
    void setString(String key, String value);

    /**
     * 设置字符串 永不失效
     *
     * @param key   缓存key
     * @param value 缓存值
     * @author 庞先海 2017-07-17
     */
    void setStringNever(String key, String value);

    /**
     * 设置字符串
     *
     * @param key     缓存key
     * @param value   缓存值
     * @param seconds 失效时间 单位为秒
     * @author 庞先海 2017-07-17
     */

    void setString(String key, String value, int seconds);

    /**
     * 设置字符串 线程安全
     *
     * @param key   缓存key
     * @param value 缓存值
     * @return 0设置失败 原key有值 1设置成功
     */
    Long setnx(String key, String value);

    /**
     * 设置字符串 线程安全
     *
     * @param key     缓存key
     * @param value   缓存值
     * @param seconds 失效时间 单位为秒
     * @return 0设置失败 原key有值 1设置成功
     */
    Long setnx(String key, String value, int seconds);

    /**
     * 设置字符串 线程安全 key永不失效
     *
     * @param key   缓存key
     * @param value 缓存值
     * @return 0设置失败 原key有值 1设置成功
     */
    Long setnxNever(String key, String value);

    /**
     * 获取字符串
     *
     * @param key 缓存key
     * @return 缓存的value
     * @author 庞先海 2017-07-17
     */
    String getString(String key);

    /**
     * 设置对象
     *
     * @param key   缓存key
     * @param value 可序列化
     * @author 庞先海 2017-07-17
     */
    void setObject(String key, Object value);

    /**
     * 设置对象 永不失效
     *
     * @param key   缓存key
     * @param value 可序列化
     * @author 庞先海 2017-07-17
     */
    void setObjectNever(String key, Object value);

    /**
     * Description:设置对象
     *
     * @param seconds 失效时间
     * @param key     缓存key]
     * @param value   可序列化
     * @author 庞先海 2017-07-17
     */
    void setObject(String key, Object value, int seconds);

    /**
     * 获取对象
     *
     * @param key   缓存key
     * @param clazz 返回的class类型
     * @return 缓存值并序列化为对象
     * @author 庞先海 2017-07-17
     */
    <T> T getObject(String key, Class<T> clazz);

    /**
     * 获取对象列表
     *
     * @param key   缓存key
     * @param clazz 返回的class类型
     * @return 缓存值并序列化为对象列表
     * @author 庞先海 2017-07-17
     */
    <T> List<T> getListObject(String key, Class<T> clazz);


    /**
     * 增加
     *
     * @param key     缓存key
     * @param integer 增加的值
     * @return 增加后的值
     * @author 庞先海 2017-07-17
     */
    Long incrBy(String key, long integer);

    /**
     * 增加
     *
     * @param key     缓存key
     * @param integer 增加的值
     * @param seconds 失效时间
     * @return 增加后的值
     * @author 庞先海 2017-07-17
     */
    Long incrBy(String key, long integer, int seconds);


    /**
     * 增加
     *
     * @param key     缓存key
     * @param integer 增加的值
     * @return 增加后的值
     * @author 庞先海 2017-07-17
     */
    Double incrByFloat(String key, double integer);

    /**
     * 增加
     *
     * @param key     缓存key
     * @param integer 增加的值
     * @param seconds 失效时间
     * @return 增加后的值
     * @author 庞先海 2017-07-17
     */
    Double incrByFloat(String key, double integer, int seconds);

    /**
     * 增加1
     *
     * @param key 缓存key
     * @return 增加后的值
     * @author 庞先海 2017-07-17
     */
    Long incr(String key);

    /**
     * 增加1
     *
     * @param key     缓存key
     * @param seconds 失效时间
     * @return 增加后的值
     * @author 庞先海 2017-07-17
     */
    Long incr(String key, int seconds);

    /**
     * 减
     *
     * @param key     缓存key
     * @param integer 减的值
     * @return 减去后的值
     * @author 庞先海 2017-07-17
     */
    Long decrBy(String key, long integer);

    /**
     * 减
     *
     * @param key     缓存key
     * @param integer 减的值
     * @param seconds 失效时间
     * @return 减去后的值
     * @author 庞先海 2017-07-17
     */
    Long decrBy(String key, long integer, int seconds);

    /**
     * 减1
     *
     * @param key 缓存key
     * @return 减去后的值
     * @author 庞先海 2017-07-17
     */
    Long decr(String key);

    /**
     * 减1
     *
     * @param key     缓存key
     * @param seconds 失效时间
     * @return 减去后的值
     * @author 庞先海 2017-07-17
     */
    Long decr(String key, int seconds);

    /**
     * 对字符串append操作
     *
     * @param key   缓存key
     * @param value append的值
     * @return append后新值的长度
     * @author 庞先海 2017-08-18
     */
    Long append(String key, String value);

    /**
     * 对字符串append操作
     *
     * @param key     缓存key
     * @param value   append的值
     * @param seconds 失效时间
     * @return append后新值的长度
     * @author 庞先海 2017-07-17
     */
    Long append(String key, String value, int seconds);

    /**
     * 对字符串sbustr操作
     *
     * @param key   缓存key
     * @param start 开始位置
     * @param end   结束位置
     * @return 新值
     * @author 庞先海 2017-07-17
     */
    String substr(String key, int start, int end);

    /**
     * 获取Key的类型
     *
     * @param key 缓存key
     * @return Key的类型
     * @author 庞先海 2017-07-17
     */
    String type(String key);

    /**
     * key 是否存在
     *
     * @param key 缓存key
     * @return 是否存在
     * @author 庞先海 2017-07-17
     */
    boolean exists(String key);


    /**
     * 获取KEY 失效时间
     *
     * @param key 缓存key
     * @return 剩余失效时间秒 -1 -2见reids ttl结果描述
     * @author 庞先海 2017-07-17
     */
    Long ttl(String key);

    /**
     * 删除
     *
     * @param key 缓存key
     * @return 同 redis del返回结果
     * @author 庞先海 2017-07-17
     */
    Long del(String key);

    /**
     * hash set
     *
     * @param key   缓存key
     * @param field 缓存key下的field
     * @param value 缓存值
     * @return 0表示更新 1新加
     * @author 庞先海 2017-07-17
     */
    Long hset(String key, String field, String value);

    /**
     * hash set
     *
     * @param key   缓存key
     * @param field 缓存key下的field
     * @param value 缓存值
     * @return 0表示更新 1新加
     * @author 庞先海 2017-08-26
     */
    Long hsetObject(String key, String field, Object value);

    /**
     * hash set 永不失效
     *
     * @param key   缓存key
     * @param field 缓存key下的field
     * @param value 缓存值
     * @return 0表示更新 1新加
     * @author 庞先海 2017-07-17
     */
    Long hsetNever(String key, String field, String value);

    /**
     * hash set
     *
     * @param key   缓存key
     * @param field 缓存key下的field
     * @param value 缓存值
     * @return 0表示更新 1新加
     * @author 庞先海 2017-08-26
     */
    Long hsetObjectNever(String key, String field, Object value);

    /**
     * hash set
     *
     * @param key     缓存key
     * @param field   缓存key下的field
     * @param value   缓存值
     * @param seconds 失效时间
     * @return 0表示更新 1新加
     * @author 庞先海 2017-07-17
     */
    Long hset(String key, String field, String value, int seconds);

    /**
     * hash get
     *
     * @param key   缓存key
     * @param field 缓存key下的field
     * @return 缓存值
     * @author 庞先海 2017-07-17
     */
    String hget(String key, String field);

    /**
     * hash get
     *
     * @param key   缓存key
     * @param field 缓存key下的field
     * @param clazz 值的class类型
     * @return 缓存值
     * @author 庞先海 2017-08-26
     */
    <T> T hgetObject(String key, String field, Class<T> clazz);

    /**
     * 返回哈希表 key 中，所有的域和值
     *
     * @param key 缓存key
     * @return 该key的所有缓存值
     * @author 庞先海 2017-07-17
     */
    Map<String, String> hgetAll(String key);

    /**
     * hset当key中field存在不做任何操作
     *
     * @param key   缓存key
     * @param field 缓存key下的field
     * @param value 缓存值
     * @return 0表示field已经存在 1新加
     * @author 庞先海 2017-07-17
     */
    Long hsetnx(String key, String field, String value);

    /**
     * 同hsetnx操作 key不失效
     *
     * @param key   缓存key
     * @param field 缓存key下的field
     * @param value 缓存值
     * @return 0表示field已经存在 1新加
     * @author 庞先海 2017-07-17
     */
    Long hsetnxNever(String key, String field, String value);

    /**
     * 同hsetnx操作
     *
     * @param key     缓存key
     * @param field   缓存key下的field
     * @param value   缓存值
     * @param seconds 失效时间
     * @return 0表示field已经存在 1新加
     * @author 庞先海 2017-07-17
     */
    Long hsetnx(String key, String field, String value, int seconds);

    /**
     * key下设置hash
     *
     * @param key  缓存key
     * @param hash 缓存hash
     * @return Return OK or Exception if hash is empty
     * @author 庞先海 2017-07-17
     */
    String hmset(String key, Map<String, String> hash);

    /**
     * key下设置hash 不失效
     *
     * @param key  缓存key
     * @param hash 缓存hash
     * @return Return OK or Exception if hash is empty
     * @author 庞先海 2017-07-17
     */
    String hmsetNever(String key, Map<String, String> hash);

    /**
     * key下设置hash 不失效
     *
     * @param key     缓存key
     * @param hash    缓存hash
     * @param seconds 失效时间
     * @return Return OK or Exception if hash is empty
     * @author 庞先海 2017-07-17
     */
    String hmset(String key, Map<String, String> hash, int seconds);

    /**
     * has批量获取
     *
     * @param key    缓存key
     * @param fields fields列表
     * @return fields下所有值
     * @author 庞先海 2017-07-17
     */
    List<String> hmget(String key, String... fields);

    /**
     * hash表加
     *
     * @param key   缓存key
     * @param field field
     * @param value 增加的值
     * @return 增加后的新值
     * @author 庞先海 2017-07-17
     */
    Long hincrBy(String key, String field, long value);

    /**
     * hash表加
     *
     * @param key     缓存key
     * @param field   field
     * @param value   增加的值
     * @param seconds 失效时间
     * @return 增加后的新值
     * @author 庞先海 2017-07-17
     */
    Long hincrBy(String key, String field, long value, int seconds);

    /**
     * hash表加
     *
     * @param key   缓存key
     * @param field field
     * @param value 增加的值
     * @return 增加后的新值
     * @author 庞先海 2017-07-17
     */
    Double hincrByFloat(String key, String field, double value);

    /**
     * hash表加
     *
     * @param key     缓存key
     * @param field   field
     * @param value   增加的值
     * @param seconds 失效时间
     * @return 增加后的新值
     * @author 庞先海 2017-07-17
     */
    Double hincrByFloat(String key, String field, double value, int seconds);

    /**
     * hash表key 是否存在
     *
     * @param key   缓存key
     * @param field field
     * @return 是否存在
     * @author 庞先海 2017-07-17
     */
    boolean hexists(String key, String field);

    /**
     * hash表 删除
     *
     * @param key    缓存key
     * @param fields field列表
     * @return 0未删除 1已删除
     * @author 庞先海 2017-07-17
     */
    Long hdel(String key, String... fields);

    /**
     * 向list 左push 不失效
     *
     * @param key    缓存key
     * @param values push的值
     * @return push的数量
     * @author 庞先海 2017-07-17
     */
    Long lpushNever(String key, String... values);

    /**
     * 向list 左push
     *
     * @param key    缓存key
     * @param values push的值
     * @return push的数量
     * @author 庞先海 2017-07-17
     */
    Long lpush(String key, String... values);

    /**
     * 向list 左push
     *
     * @param key     缓存key
     * @param values  push的值
     * @param seconds 失效时间
     * @return push的数量
     * @author 庞先海 2017-07-17
     */
    Long lpush(String key, int seconds, String... values);

    /**
     * 向list 左push
     *
     * @param key    缓存key
     * @param values push的值
     * @return push的数量
     * @author 庞先海 2017-10-21
     */
    Long rpush(String key, String... values);

    /**
     * 向list 右push
     *
     * @param key    缓存key
     * @param values push的值
     * @return push的数量
     * @author 庞先海 2017-10-21
     */
    Long rpushNever(String key, String... values);

    /**
     * 向list 右push
     *
     * @param key     缓存key
     * @param values  push的值
     * @param seconds 失效时间
     * @return push的数量
     * @author 庞先海 2017-10-21
     */
    Long rpush(String key, int seconds, String... values);

    /**
     * list中左弹出值
     *
     * @param key 缓存key
     * @return 弹出的值
     * @author 庞先海 2017-10-21
     */
    String lpop(String key);

    /**
     * list中右弹出值
     *
     * @param key 缓存key
     * @return 弹出的值
     * @author 庞先海 2017-10-21
     */
    String rpop(String key);


    /**
     * 删除List中的值 详情见reids lrem命令
     *
     * @param key   缓存key
     * @param count 数量
     * @param value 值
     * @return 弹出的值
     */
    Long lrem(String key, int count, String value);

    /**
     * 获取list中的值
     *
     * @param key   缓存key
     * @param start 开始位置
     * @param end   结束
     * @return 取出的值
     * @author 庞先海 2017-07-17
     */
    List<String> lrange(String key, long start, long end);

    /**
     * 获取 list 的长度
     *
     * @param key 缓存key
     * @return list长度值
     */
    Long llen(String key);

    /**
     * 向set中添加值
     *
     * @param key    缓存key
     * @param values 值
     * @return 0新建 1不操作
     * @author 庞先海 2017-07-17
     */
    Long sadd(String key, String... values);

    /**
     * 向set中价值
     *
     * @param key     缓存key
     * @param values  值
     * @param seconds 失效时间
     * @return 0新建 1不操作
     * @author 庞先海 2017-07-17
     */
    Long sadd(String key, int seconds, String... values);

    /**
     * 向set中价值 不失效
     *
     * @param key    缓存key
     * @param values 值
     * @return 0新建 1不操作
     * @author 庞先海 2017-07-17
     */
    Long saddNever(String key, String... values);

    /**
     * 获取set中的值
     *
     * @param key 缓存key
     * @return set中的值
     * @author 庞先海 2017-07-17
     */
    Set<String> smembers(String key);

    /**
     * 删除set中的值
     *
     * @param key    key
     * @param values 删除的值
     * @return 删除的条数
     */
    Long srem(String key, String... values);

    /**
     * redis 发布消息
     *
     * @param channel 管道
     * @param message 消息
     * @return 未知
     */
    Long publish(String channel, String message);

    /**
     * redis订阅消息
     *
     * @param jedisPubSub 接收消息类
     * @param channels    管道
     */
    void subscribe(JedisPubSub jedisPubSub, String... channels);

    /**
     * jedis直接访问
     *
     * @param function 会掉方法
     * @param <T>      返回类型
     * @return 返回结果
     */
    <T> T jedis(Function<Jedis, T> function);

    /**
     * 获取缓存对象 如果缓存中没值 设置缓存
     *
     * @param key      缓存key
     * @param clazz    返回的class类型
     * @param supplier 回调方法 获取需要缓存的数据
     * @return 缓存值并序列化为对象
     * @author 庞先海 2017-12-23
     */
    <T> T cacheObject(String key, Class<T> clazz, Supplier<T> supplier);

    /**
     * 获取缓存对象 如果缓存中没值 设置缓存
     *
     * @param key      缓存key
     * @param clazz    返回的class类型
     * @param seconds  缓存时间
     * @param supplier 回调方法 获取需要缓存的数据
     * @return 缓存值并序列化为对象
     * @author 庞先海 2017-12-23
     */
    <T> T cacheObject(String key, int seconds, Class<T> clazz, Supplier<T> supplier);

    /**
     * 获取缓存对象 如果缓存中没值 设置缓存 缓存不失效
     *
     * @param key      缓存key
     * @param clazz    返回的class类型
     * @param supplier 回调方法 获取需要缓存的数据
     * @return 缓存值并序列化为对象
     * @author 庞先海 2017-12-23
     */
    <T> T cacheObjectNever(String key, Class<T> clazz, Supplier<T> supplier);

    /**
     * 设置缓存
     *
     * @param key     缓存key
     * @param seconds 缓存时间
     * @param value   缓存值
     */
    void setCacheObject(String key, int seconds, Object value);

    /**
     * 设置缓存 默认缓存时间
     *
     * @param key   缓存key
     * @param value 缓存值
     */
    void setCacheObject(String key, Object value);

    /**
     * 设置缓存  缓存不失效
     *
     * @param key   缓存key
     * @param value 缓存值
     */
    void setCacheObjectNever(String key, Object value);

    /**
     * 删除缓存
     *
     * @param key 缓存key
     */
    void deleteCacheObject(String key);

    /**
     * 获取缓存列表对象 如果缓存中没值 设置缓存
     *
     * @param key      缓存key
     * @param clazz    返回的class类型
     * @param supplier 回调方法
     * @return 缓存值并序列化为对象列表
     * @author 庞先海 2017-12-23
     */
    <T> List<T> cacheList(String key, Class<T> clazz, Supplier<List<T>> supplier);

    /**
     * 获取缓存列表对象 如果缓存中没值 设置缓存
     *
     * @param key      缓存key
     * @param seconds  失效时间
     * @param clazz    返回的class类型
     * @param supplier 回调方法
     * @return 缓存值并序列化为对象列表
     * @author 庞先海 2017-12-23
     */
    <T> List<T> cacheList(String key, int seconds, Class<T> clazz, Supplier<List<T>> supplier);

    /**
     * 获取缓存列表对象 如果缓存中没值 设置缓存 永不失效
     *
     * @param key      缓存key
     * @param clazz    返回的class类型
     * @param supplier 回调方法
     * @return 缓存值并序列化为对象列表
     * @author 庞先海 2017-12-23
     */
    <T> List<T> cacheListNever(String key, Class<T> clazz, Supplier<List<T>> supplier);

    /**
     * 添加cache list
     *
     * @param key   缓存key
     * @param value cache的值
     * @param <T>   值类型
     */
    <T> void addCacheList(String key, T value);

    /**
     * 添加cache list
     *
     * @param key     缓存key
     * @param value   cache的值
     * @param seconds 过期时间
     * @param <T>     值类型
     */
    <T> void addCacheList(String key, T value, int seconds);

    /**
     * 添加cache list 永不过期
     *
     * @param key   缓存key
     * @param value cache的值
     * @param <T>   值类型
     */
    <T> void addCacheNever(String key, T value);

    /**
     * 删除cache list 中的值
     *
     * @param key   缓存key
     * @param value 删除的值 用equals判读value与缓存中的值是否相等
     * @param <T>   值类型
     * @param clazz 对象类型
     */
    <T> void deleteCacheList(String key, Class<T> clazz, T value);

    /**
     * 获取缓存Hash列表对象 如果缓存中没值则设置缓存
     *
     * @param key      缓存key
     * @param field    缓存key下的field
     * @param clazz    值的class类型
     * @param supplier 回调方法
     * @return 缓存值
     * @author 庞先海 2017-12-23
     */
    <T> T cacheHgetObject(String key, String field, Class<T> clazz, Supplier<T> supplier);

    /**
     * 获取缓存Hash列表对象 如果缓存中没值则设置缓存
     *
     * @param key      缓存key
     * @param field    缓存key下的field
     * @param seconds  失效时间
     * @param clazz    值的class类型
     * @param supplier 回调方法
     * @return 缓存值
     * @author 庞先海 2017-12-23
     */
    <T> T cacheHgetObject(String key, String field, int seconds, Class<T> clazz, Supplier<T> supplier);

    /**
     * 获取缓存Hash列表对象 如果缓存中没值则设置缓存 永不失效
     *
     * @param key      缓存key
     * @param field    缓存key下的field
     * @param clazz    值的class类型
     * @param supplier 回调方法
     * @return 缓存值
     * @author 庞先海 2017-12-23
     */
    <T> T cacheHgetObjectNever(String key, String field, Class<T> clazz, Supplier<T> supplier);

    /**
     * 获取缓存Hash列表对象 如果缓存中没值则**不设置**缓存
     *
     * @param key      缓存key
     * @param clazz    值的class类型
     * @param field    缓存key下的field
     * @param supplier 回调方法
     * @return 缓存值
     */
    <T> List<T> cacheHmget(String key, Class<T> clazz, Supplier<Map<String, T>> supplier, String... field);

    /**
     * 获取缓存Hash列表对象 如果缓存中没值则**不设置**缓存
     *
     * @param key      缓存key
     * @param clazz    值的class类型
     * @param seconds  缓存时间
     * @param supplier 回调方法
     * @param field    缓存key下的field
     * @return 缓存值
     */
    <T> List<T> cacheHmget(String key, Class<T> clazz, int seconds, Supplier<Map<String, T>> supplier, String... field);

    /**
     * 获取缓存Hash列表对象 如果缓存中没值则**不设置**缓存
     *
     * @param key      缓存key
     * @param clazz    值的class类型
     * @param field    缓存key下的field
     * @param supplier 回调方法
     * @return 缓存值
     */
    <T> List<T> cacheHmgetNerver(String key, Class<T> clazz, Supplier<Map<String, T>> supplier, String... field);

    /**
     * 设置缓存 对象
     *
     * @param key   缓存key
     * @param field 缓存key下的field
     * @param value 值
     */
    void cacheHsetObject(String key, String field, Object value);

    /**
     * 设置缓存 对象
     *
     * @param key     缓存key
     * @param field   缓存key下的field
     * @param seconds 过期时间
     * @param value   值
     */
    void cacheHsetObject(String key, String field, int seconds, Object value);

    /**
     * 设置缓存 对象 不过期
     *
     * @param key   缓存key
     * @param field 缓存key下的field
     * @param value 值
     */
    void cacheHsetObjectNever(String key, String field, Object value);

    /**
     * 删除缓存
     *
     * @param key   缓存key
     * @param filed 缓存key下的field
     */
    void deleteCacheHget(String key, String filed);

    /**
     * 获取缓存Set列表对象 如果缓存中没值则设置缓存
     *
     * @param key      缓存key
     * @param supplier 回调方法
     * @param clazz    缓存值的类型
     * @return 返回缓存的值
     * @author 庞先海 2017-12-25
     */
    <T> Set<T> cacheSet(String key, Class<T> clazz, Supplier<Set<T>> supplier);

    /**
     * 获取缓存Set列表对象 如果缓存中没值则设置缓存
     *
     * @param key      缓存key
     * @param supplier 回调方法
     * @param seconds  缓存时间
     * @param clazz    缓存值的类型
     * @return 返回缓存的值
     * @author 庞先海 2017-12-25
     */
    <T> Set<T> cacheSet(String key, Class<T> clazz, int seconds, Supplier<Set<T>> supplier);

    /**
     * 获取缓存Set列表对象 如果缓存中没值则设置缓存 永不失效
     *
     * @param key      缓存key
     * @param supplier 回调方法
     * @param clazz    缓存值的类型
     * @return 返回缓存的值
     * @author 庞先海 2017-12-25
     */
    <T> Set<T> cacheSetNever(String key, Class<T> clazz, Supplier<Set<T>> supplier);

    /**
     * 利用redis做锁 加锁
     *
     * @param key       锁
     * @param requestId 请求标识
     * @param timeout   超时时间
     * @return 是否成功获取锁
     */
    boolean lock(String key, String requestId, long timeout);


    /**
     * 利用redis做锁 释放锁
     *
     * @param key       锁对象
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    boolean unlock(String key, String requestId);
}
