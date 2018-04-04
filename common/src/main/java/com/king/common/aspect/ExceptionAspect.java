package com.king.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.king.common.exception.RRException;
import com.king.common.utils.RedisKeys;
import com.king.common.utils.RedisUtils;
import com.king.common.utils.SerialNoHolder;

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
                result = point.proceed();
            }catch (Exception e){
            	String serialNo= SerialNoHolder.serialNo.get();           	
            	String serialNoKey = RedisKeys.getSerialNoKey(serialNo);
            	Object appcode= redisUtils.hget(serialNoKey, "appcode");
                logger.error(String.format("错误流水号【%s】", serialNo)+String.format("服务【%s】", appcode)+String.format("方法【%s】异常！", point.getSignature()));
                throw new RRException(String.format("服务调用时【%s】发生未知错误，错误流水号【%s】，请联系管理员", appcode,serialNo),500,e);
            }

        return result;
    }
}
