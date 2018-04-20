package com.king.common.mongodb.log.aspect;

import java.net.InetAddress;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.king.common.mongodb.log.model.CommonLogVO;
import com.king.common.mongodb.log.repo.CommonLogRepo;
import com.king.common.utils.SerialNoHolder;
import com.king.common.utils.StringToolkit;


/**
 * 通用日志切面
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月19日
 */
public class CommonLogAspect {

	private static Logger logger = LoggerFactory.getLogger(CommonLogAspect.class);
	private static String ipAddress = "127.0.0.1";
	
	@Autowired
	private CommonLogRepo commonLogRepo;
	
	static {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			ipAddress = inetAddress.getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

		Object result = "";
		try {
			result = joinPoint.proceed();
		} finally {
			try {
				addLogRecode(joinPoint, result);
			} catch (Exception e) {
				e.printStackTrace();
				logger.warn(joinPoint.getTarget().getClass() + ":登记业务日志时出错，【" + e.getMessage() + "】");
			}
		}
		return result;
	}
	
	private void addLogRecode(ProceedingJoinPoint joinPoint,Object result) {
		Object[] args = joinPoint.getArgs();
		String oprcode = StringToolkit.getObjectString(args[2]);
		StringBuffer buf = new StringBuffer();
		for (int i = 3; i < args.length; i++){
			buf.append("【").append(StringToolkit.getObjectString(args[i])).append("】");
		}

		String apiName = joinPoint.getTarget().getClass().getName() + "#" + joinPoint.getSignature().getName();
		String appCode = SerialNoHolder.appcode.get();
		String logCode = appCode + "-" + DateTimeUtils.currentTimeMillis();
		
		CommonLogVO logVO = new CommonLogVO();
		logVO.setUserCode(oprcode);
		logVO.setSeriaNo(SerialNoHolder.serialNo.get());
		logVO.setApiName(apiName);
		logVO.setAppCode(SerialNoHolder.appcode.get());
		logVO.setCreateTime(new Date());
		logVO.setInputData(buf.toString());
		logVO.setOutputData(StringToolkit.getObjectString(result));
		logVO.setIp(ipAddress);
		logVO.setLogCode(logCode);
		
		commonLogRepo.insert(logVO);
	}
}
