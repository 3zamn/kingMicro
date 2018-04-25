package com.king.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.king.common.utils.exception.RRException;


/**
 * Redis启用切面处理类
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年2月26日
 */
@Aspect
@Component
public class RedisEnableAspect {
    private Logger logger = LoggerFactory.getLogger(getClass());
    //是否开启redis缓存  true开启   false关闭
    @Value("#{new Boolean('${king.redis.open}')}")
    private Boolean open;

	@Pointcut("@annotation(com.king.common.annotation.Redis)")
	public void redisPointCut() { 
		
	}

	@Around("redisPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
		Object result= (Boolean)  false;
		try {
			 if(open instanceof Boolean &&open !=null){
				 result=open;
		        }
		} catch (Exception e){
            logger.error("redis error", e);
            throw new RRException("Redis服务异常");
        }
       
        return result;
    }
}
