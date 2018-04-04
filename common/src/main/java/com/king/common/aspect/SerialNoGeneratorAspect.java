package com.king.common.aspect;

import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.aspectj.lang.JoinPoint;
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
	    @Before("execution(* com.king.services.spi.*.*(..))")
	    public void before(JoinPoint point) throws Throwable {

	            try{
	            //	Signature signature=point.getSignature();      	
	            	String serialNo= SerialNoHolder.serialNo.get();
	            	if(serialNo ==null){
	            		serialNo =UUID.randomUUID().toString();
	            		 SerialNoHolder.serialNo.set(serialNo);
	            		 String serialNoKey = RedisKeys.getSerialNoKey(serialNo);
	                 	redisUtils.hset(serialNoKey, "appcode", configs!=null?configs.getString("hostname"):null,50);
	            	}	         
	            }catch (Exception e){
	                logger.error("服务异常");
	                throw new RRException("服务异常");
	            }
	    }
	    
	    
	    static{
	    	try {
				configs = new PropertiesConfiguration("settings.properties");
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
}
