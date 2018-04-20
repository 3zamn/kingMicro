package com.king.common.mongodb.log.aspect;

import java.net.InetAddress;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.king.common.mongodb.log.model.ExceptionLogVO;
import com.king.common.mongodb.log.repo.ExceptionLogRepo;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.exception.ExceptionUtils;
import com.king.common.utils.pattern.StringToolkit;
import com.king.common.utils.thread.SerialNoHolder;


/**
 * 异常日志切面
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月19日
 */
public class ExceptionLogAspect {
	
	@Autowired
	private ExceptionLogRepo exceptionLogRepo;

	private static Logger logger = LoggerFactory.getLogger(ExceptionLogAspect.class);
	private static String ipAddress = "127.0.0.1";	

	static {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			ipAddress = inetAddress.getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			return joinPoint.proceed();
		} catch (Exception e) {
			logger.error("", e);
			String errMsg = ExceptionUtils.makeStackTrace(e);
			String logCode = SerialNoHolder.appcode.get() + "-" + DateTimeUtils.currentTimeMillis();
			addExceptionLog(errMsg,joinPoint,logCode);
			
			return JsonResponse.error(String.format("服务调用时发生异常，错误码：【%s】，请联系管理员。", logCode));
		}
	}

	private void addExceptionLog(String errMsg, ProceedingJoinPoint joinPoint, String logCode) {
		try {
			ExceptionLogVO vo = new ExceptionLogVO();
			String apiName = joinPoint.getTarget().getClass().getName() + "#" + joinPoint.getSignature().getName();
			Object[] args = joinPoint.getArgs();
			if (args.length > 2) {
				String oprcode = StringToolkit.getObjectString(args[2]);
				vo.setUserCode(oprcode);
				
				if (args.length > 3) {
					StringBuffer buf = new StringBuffer();
					for (int i = 3; i < args.length; i++){
						buf.append("【").append(StringToolkit.getObjectString(args[i])).append("】");
						vo.setInputData(buf.toString());
					}
				}
				
			}
			vo.setSeriaNo(SerialNoHolder.serialNo.get());
			vo.setApiName(apiName);
			vo.setAppCode(SerialNoHolder.appcode.get());
			vo.setCreateTime(new Date());
			vo.setIp(ipAddress);
			vo.setLogCode(logCode);
			vo.setExceptionMsg(errMsg);
			exceptionLogRepo.insert(vo);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
