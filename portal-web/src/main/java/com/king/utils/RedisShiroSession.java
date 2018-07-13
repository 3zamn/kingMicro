package com.king.utils;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.king.common.utils.constant.Constant;
import com.king.common.utils.exception.ExceptionUtils;
import com.king.common.utils.redis.RedisKeys;
import com.king.common.utils.redis.RedisUtils;

/**
 * shiro session dao
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@Component
public class RedisShiroSession extends EnterpriseCacheSessionDAO {
    @SuppressWarnings("rawtypes")
	@Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisUtils redisUtils;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
    //创建session
    @Override
	protected Serializable doCreate(Session session) {
		Serializable sessionId = null;
		try {
			sessionId = super.doCreate(session);
			final String key = RedisKeys.getShiroSessionKey(sessionId.toString());
			setShiroSession(key, session);
		} catch (Exception e) {
			logger.error(ExceptionUtils.makeStackTrace(e));
		}
		return sessionId;
	}

    //获取session
    @Override
	protected Session doReadSession(Serializable sessionId) {
		Session session = null;
		try {
			session = super.doReadSession(sessionId);
			if (session == null) {
				final String key = RedisKeys.getShiroSessionKey(sessionId.toString());
				session = getShiroSession(key);
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.makeStackTrace(e));
		}
		return session;
	}

    //更新session
    @Override
	protected void doUpdate(Session session) {
		try {
			super.doUpdate(session);
			final String key = RedisKeys.getShiroSessionKey(session.getId().toString());
			setShiroSession(key, session);
		} catch (Exception e) {
			logger.error(ExceptionUtils.makeStackTrace(e));
		}
	}

	// 删除session
	@SuppressWarnings("unchecked")
	@Override
	protected void doDelete(Session session) {
		try {
			super.doDelete(session);
			final String key = RedisKeys.getShiroSessionKey(session.getId().toString());
			redisTemplate.delete(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.makeStackTrace(e));
		}
	}

    private Session getShiroSession(String key) {
    	Session session=null;
    	try {
    		session =(Session)redisTemplate.opsForValue().get(key);
		} catch (Exception e) {
			logger.error(ExceptionUtils.makeStackTrace(e));
		}
        return session;
    }

    @SuppressWarnings("unchecked")
	private void setShiroSession(String key, Session session){
    	try {
    		  redisTemplate.opsForValue().set(key, session);
    	      redisTemplate.expire(key, Constant.SHIRO_SESSION_EXPIRE, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			logger.error(ExceptionUtils.makeStackTrace(e));
		}  
    }

}
