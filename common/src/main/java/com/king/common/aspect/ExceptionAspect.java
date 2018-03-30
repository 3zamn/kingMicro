package com.king.common.aspect;

import java.util.UUID;

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
 * 异常处理切面
 * 在线程级别保存应用编码、交易流水号、操作用户编码
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年3月30日
 */
@Aspect
@Order(5)
@Component
public class ExceptionAspect {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RedisUtils redisUtils;
    @Around("execution(* com.king.services.spi.*.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
            try{
            //	Signature signature=point.getSignature();      	
            	String serialNo= SerialNoHolder.serialNo.get();
            	
            	String serialNoKey = RedisKeys.getSerialNoKey(serialNo);
            	Object appcode= redisUtils.hget(serialNoKey, "appcode");

          //  	System.out.println("appcode:"+appcode+";serialNo:"+serialNoKey+"method:"+point.getSignature());
                result = point.proceed();
            }catch (Exception e){
            	String serialNo= SerialNoHolder.serialNo.get();           	
            	String serialNoKey = RedisKeys.getSerialNoKey(serialNo);
            	Object appcode= redisUtils.hget(serialNoKey, "appcode");
                logger.error("服务异常:"+appcode+"方法："+"method:"+point.getSignature());
          //      e.printStackTrace();
                throw new RRException("服务异常"+appcode+"ff"+point.getSignature());
            }

        return result;
    }
}
