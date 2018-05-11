package com.king.common.aspect;

import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.king.common.mongodb.log.model.ExceptionLogVO;
import com.king.common.mongodb.log.repo.ExceptionLogRepo;
import com.king.common.utils.exception.RRException;
import com.king.common.utils.pattern.StringToolkit;
import com.king.common.utils.redis.RedisKeys;
import com.king.common.utils.redis.RedisUtils;
import com.king.common.utils.thread.SerialNoHolder;


/**
 * 异常日志处理切面
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
	private ExceptionLogRepo exceptionLogRepo;
    @Autowired
    private RedisUtils redisUtils;
	private static String ipAddress = "127.0.0.1";	
	private static Configuration configs ;
	
	@AfterThrowing(pointcut="execution(* com.king.dal.gen.service.*.*(..)) || execution(* com.king.services.spi.*.*(..))", throwing = "e")
	public void afterThrowing(JoinPoint  point,Exception e) throws Throwable  {
        String serialNo= SerialNoHolder.serialNo.get();           	
    	String serialNoKey = RedisKeys.getSerialNoKey(serialNo);
    	Object appcode= redisUtils.hget(serialNoKey, "appcode");
    	if(appcode==null) {
    		appcode=configs.getString("serverName");
    	}
    
    	addExceptionLog(e.getMessage(), point,appcode!=null?appcode.toString():null, serialNo);
        logger.error(String.format("错误流水号【%s】", serialNo==null?serialNo =UUID.randomUUID().toString():serialNo)+String.format("服务【%s】", appcode)+String.format("方法【%s】异常！", point.getSignature()));
        throw new RRException(String.format("服务调用时【%s】发生未知错误，错误流水号【%s】，请联系管理员", appcode,serialNo),500,e);

    }
    
	private void addExceptionLog(String errMsg, JoinPoint joinPoint,String appcode, String serialNo) throws Throwable {
		try {
			ExceptionLogVO vo = new ExceptionLogVO();
			
			String apiName = joinPoint.getTarget().getClass().getName() + "#" + joinPoint.getSignature().getName();
			Object[] args = joinPoint.getArgs();
			try {
				String params = new Gson().toJson(args[0]);
				vo.setOutputData(StringToolkit.getObjectString(joinPoint.getTarget()));
				vo.setInputData(params);
			} catch (Exception e) {

			}
			String logCode = appcode + "-" + DateTimeUtils.currentTimeMillis();
			vo.setAppCode(appcode);
			vo.setSeriaNo(serialNo);
			vo.setApiName(apiName);
			vo.setCreateTime(new Date());
			vo.setIp(ipAddress);
			vo.setLogCode(logCode);
			vo.setExceptionMsg(errMsg);
			exceptionLogRepo.insert(vo);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//本机IP
	static {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			ipAddress = inetAddress.getHostAddress();
			configs = new PropertiesConfiguration("settings.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
