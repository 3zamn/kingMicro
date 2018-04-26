package com.king.common.aspect;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
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
import com.king.common.utils.network.HttpContextUtils;
import com.king.common.utils.network.IPUtils;
import com.king.common.utils.pattern.StringToolkit;
import com.king.common.utils.redis.RedisKeys;
import com.king.common.utils.redis.RedisUtils;
import com.king.common.utils.thread.SerialNoHolder;


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
	private ExceptionLogRepo exceptionLogRepo;
    @Autowired
    private RedisUtils redisUtils;
    @Around("execution(* com.king.services.spi.*.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
            try{
            //	Signature signature=point.getSignature();      	
            	String serialNo= SerialNoHolder.serialNo.get();
            	
            	String serialNoKey = RedisKeys.getSerialNoKey(serialNo);
       //     	Object appcode= redisUtils.hget(serialNoKey, "appcode");
                result = point.proceed();
            }catch (Exception e){
            	String serialNo= SerialNoHolder.serialNo.get();           	
            	String serialNoKey = RedisKeys.getSerialNoKey(serialNo);
            	Object appcode= redisUtils.hget(serialNoKey, "appcode");
            	String logCode = appcode + "-" + DateTimeUtils.currentTimeMillis();
            	addExceptionLog(e.getMessage(), point,appcode!=null?appcode.toString():null, logCode);
                logger.error(String.format("错误流水号【%s】", serialNo)+String.format("服务【%s】", appcode)+String.format("方法【%s】异常！", point.getSignature()));
                throw new RRException(String.format("服务调用时【%s】发生未知错误，错误流水号【%s】，请联系管理员", appcode,serialNo),500,e);
            }

        return result;
    }
    
	private void addExceptionLog(String errMsg, ProceedingJoinPoint joinPoint,String appcode, String logCode) {
		try {
			ExceptionLogVO vo = new ExceptionLogVO();
			String apiName = joinPoint.getTarget().getClass().getName() + "#" + joinPoint.getSignature().getName();
			Object[] args = joinPoint.getArgs();
			try {
				String params = new Gson().toJson(args[0]);
				vo.setInputData(params);
			} catch (Exception e) {

			}
			// 获取request
		//	HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
			// 设置IP地址
			vo.setAppCode(appcode);
			vo.setSeriaNo(SerialNoHolder.serialNo.get());
			vo.setApiName(apiName);
			vo.setCreateTime(new Date());
		//	vo.setIp(IPUtils.getIpAddr(request));
			vo.setLogCode(logCode);
			vo.setExceptionMsg(errMsg);
		//	exceptionLogRepo.insert(vo);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
