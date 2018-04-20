package com.king.common.utils.redis;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * @author King chen
 * @date 2017年12月25日
 */
@Order(2)
@Component
public class RedisUtils {
    @SuppressWarnings("rawtypes")
	@Autowired
    private  RedisTemplate redisTemplate;
    @Resource(name="redisTemplate")
    private ValueOperations<String, String> valueOperations;
    @Resource(name="redisTemplate")
    private  HashOperations<String, String, Object> hashOperations;
    @Resource(name="redisTemplate")
    private ListOperations<String, Object> listOperations;
    @Resource(name="redisTemplate")
    private SetOperations<String, Object> setOperations;
    @Resource(name="redisTemplate")
    private ZSetOperations<String, Object> zSetOperations;
    /**  默认过期时长，单位：秒 */
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24;
    /**  不设置过期时长 */
    public final static long NOT_EXPIRE = -1;
    private final static Gson gson = new Gson();

    @SuppressWarnings("unchecked")
	public void set(String key, Object value, long expire){
        valueOperations.set(key, toJson(value));
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    public void set(String key, Object value){
        set(key, value, DEFAULT_EXPIRE);
    }

    @SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(key);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    @SuppressWarnings("unchecked")
	public String get(String key, long expire) {
        String value = valueOperations.get(key);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    public String get(String key) {
        return get(key, NOT_EXPIRE);
    }

    @SuppressWarnings("unchecked")
	public void delete(String key) {
        redisTemplate.delete(key);
    }

    @SuppressWarnings("unchecked")
	public  void hset(String key,String hashKey, Object value, long expire){
    	hashOperations.put(key, hashKey, toJson(value));
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    public  void hset(String key,String hashKey,Object value){
        hset(key, hashKey,value, DEFAULT_EXPIRE);
    }
    
    @SuppressWarnings("unchecked")
	public  Object hget(String key,String hashKey, long expire) {
        Object value = hashOperations.get(key, hashKey);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    public Object hget(String key,String hashKey) {
        return hget(key,hashKey, NOT_EXPIRE);
    }
    
    
    @SuppressWarnings("unchecked")
	public  void sset(String hashKey, Object value, long expire){
    	setOperations.add(hashKey, value);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(hashKey, expire, TimeUnit.SECONDS);
        }
    }

    public  void sset(String hashKey,Object value){
        sset(hashKey,value, DEFAULT_EXPIRE);
    }
    
    @SuppressWarnings("unchecked")
	public  Set<Object> sget(String hashKey, long expire) {
    	Set<Object> value = setOperations.members(hashKey);
        if(expire != NOT_EXPIRE){
            redisTemplate.expire(hashKey, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    public Set<Object> sget(String hashKey) {
        return sget(hashKey, NOT_EXPIRE);
    }
    
    @SuppressWarnings("unchecked")
	public void expire(String hashKey,long expire){
    	 if(expire >0){
             redisTemplate.expire(hashKey, expire, TimeUnit.SECONDS);
         }
    }
    
    @SuppressWarnings("unchecked")
	public boolean exsit(String hashKey){
   	 
    	return  redisTemplate.hasKey(hashKey);
   }
    
    @SuppressWarnings("unchecked")
	public Set<String> likeKey(String hashKey){
      	 
    	return  redisTemplate.keys(hashKey+"*");
   }
    
    /**
     * Object转成JSON数据
     */
    private  String toJson(Object object){
        if(object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String){
            return String.valueOf(object);
        }
        return gson.toJson(object);
    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(String json, Class<T> clazz){
        return gson.fromJson(json, clazz);
    }
}
