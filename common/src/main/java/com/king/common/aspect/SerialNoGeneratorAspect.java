package com.king.common.aspect;

import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.king.common.utils.constant.Constant;
import com.king.common.utils.exception.RRException;
import com.king.common.utils.redis.RedisKeys;
import com.king.common.utils.redis.RedisUtils;
import com.king.common.utils.thread.SerialNoHolder;

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
	private static Configuration configs ;
	 private Logger logger = LoggerFactory.getLogger(getClass());
	    @Autowired
	    private RedisUtils redisUtils;
	    
	    @Before("execution(* com.king.dal.gen.service.*.*(..)) || execution(* com.king.services.spi.*.*(..))")
	    public void before(JoinPoint point) throws Throwable {
	    	 logger.info("getSignature方法："+point.getSignature());
	            try{
	            	String serialNo= SerialNoHolder.serialNo.get();
	            	if(serialNo ==null){
	            		serialNo =UUID.randomUUID().toString();
	            		 SerialNoHolder.serialNo.set(serialNo);
	            		 String serialNoKey = RedisKeys.getSerialNoKey(serialNo);
	                 	redisUtils.hset(serialNoKey, "appcode", configs!=null?configs.getString("serverName"):null,Constant.SERIALNO_EXPIRE);
	            	}	         
	            }catch (Exception e){
	                logger.error("服务异常");
	                throw new RRException("服务异常");
	            }
	    }
	    
	    /**
	     * remove掉本地线程变量、防止内存泄漏
	     * @param point
	     * @throws Throwable
	     */
	    @After("execution(* com.king.dal.gen.service.*.*(..)) || execution(* com.king.services.spi.*.*(..))")
	    public void after(JoinPoint point) throws Throwable {

	    	 SerialNoHolder.serialNo.remove();
	    }
	    
	    
	    static{//获取配置
	    	try {
				configs = new PropertiesConfiguration("settings.properties");
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
}
