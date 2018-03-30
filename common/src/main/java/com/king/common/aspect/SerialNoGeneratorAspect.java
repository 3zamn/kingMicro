package com.king.common.aspect;

import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.king.common.exception.RRException;
import com.king.common.utils.RedisKeys;
import com.king.common.utils.RedisUtils;
import com.king.common.utils.SerialNoHolder;
import com.king.common.utils.ShiroUtils;

/**
 * 服务类参数切面
 * 在线程级别保存应用编码、交易流水号、操作用户编码
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年3月30日
 */
@Aspect
@Order(2)
@Component
public class SerialNoGeneratorAspect {
	 private Logger logger = LoggerFactory.getLogger(getClass());
	    @Autowired
	    private RedisUtils redisUtils;
	    @Before("execution(* com.king.services.spi.*.*(..))")
	    public void before(JoinPoint point) throws Throwable {

	            try{
	            //	Signature signature=point.getSignature();      	
	            	String serialNo= SerialNoHolder.serialNo.get();
	            	if(serialNo ==null){
	            		serialNo =UUID.randomUUID().toString();
	            		 SerialNoHolder.serialNo.set(serialNo);
	            		 String serialNoKey = RedisKeys.getSerialNoKey(serialNo);
	                 	redisUtils.hset(serialNoKey, "appcode", "smp",50);
	              //   	redisUtils.hset(serialNoKey, "usercode", ShiroUtils.getUserId(), 5000);
	             //    	System.out.println(";serialNo:"+serialNoKey+"method:"+point.getSignature());
	            	}
	            	

	            }catch (Exception e){
	                logger.error("服务异常");
	                throw new RRException("服务异常");
	            }

	    }
}
