package com.king.common.utils.redis;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

/**
 * ID生成器
 * RedisAtomicLong并发环境下存在线程安全问题、谨慎用；increment是线程安全的
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年5月15日
 */
@Component
public class IdGenerator {

	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * @Title: set
	 * @Description: set cache.
	 * @param key
	 * @param value
	 * @param expireTime
	 */
	public void set(String key, int value, Date expireTime) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
		counter.set(value);
		counter.expireAt(expireTime);
	}

	/**
	 * @Title: set
	 * @Description: set cache.
	 * @param key
	 * @param value
	 * @param timeout
	 * @param unit
	 */
	public void set(String key, int value, long timeout, TimeUnit unit) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
		counter.set(value);
		counter.expire(timeout, unit);
	}
	
	/**
	 * @Title: generate
	 * @Description: Atomically increments by one the current value.
	 * @param key
	 * @return
	 */
	public long generate(String key) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
		return counter.incrementAndGet();
	}

	/**
	 * @Title: generate
	 * @Description: Atomically increments by one the current value.
	 * @param key
	 * @return
	 */
	public long generate(String key, Date expireTime) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
		counter.expireAt(expireTime);
		return counter.incrementAndGet();
	}

	/**
	 * @Title: generate
	 * @Description: Atomically adds the given value to the current value.
	 * @param key
	 * @param increment
	 * @return
	 */
	public long generate(String key, int increment) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
		return counter.addAndGet(increment);
	}

	/**
	 * @Title: generate
	 * @Description: Atomically adds the given value to the current value.
	 * @param key
	 * @param increment
	 * @param expireTime
	 * @return
	 */
	public long generate(String key, int increment, Date expireTime) {
		RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
		counter.expireAt(expireTime);
		return counter.addAndGet(increment);
	}
	
	/**
	 * 高并发的分布式环境下保证线程安全
	 * @param key
	 * @param hashKey
	 * @param delta
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long incrementHash(String key, String hashKey, Long delta) {
		if (null == delta) {
			delta = 1L;
		}
		return redisTemplate.opsForHash().increment(key, hashKey, delta);
	}
	
}
