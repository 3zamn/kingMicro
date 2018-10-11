package com.king.common.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.king.common.dataSource.HandleDataSource;

/**
 * 切换数据源--读写分离
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年4月8日
 */
@Aspect
@Order(1)
@Component
public class DataSourceAspect {
	 private Logger logger = LoggerFactory.getLogger(getClass());

	@Pointcut("execution(* com.king.dal.gen.service.*.*(..)) || execution(* com.king.services.spi.*.*(..))")
	public void aspect() {
	}

	/**
	 * 配置前置通知,使用在方法aspect()上注册的切入点
	 */
	@Before("aspect()")
	public void before(JoinPoint point) {
		Object object = point.getTarget();
	//	 logger.info(point.getSignature().toString());
		String methodName = point.getSignature().getName();
        Class<?>[] parameterTypes = ((MethodSignature)point.getSignature()).getMethod().getParameterTypes();
        Method method;
		try {
			method = object.getClass().getMethod(methodName, parameterTypes);
			 Transactional transactional = method.getAnnotation(Transactional.class);
			 if(transactional!=null){
				 boolean readOnly = transactional.readOnly();
				 if(readOnly){
					 HandleDataSource.read();
			//		 logger.info("切换--读库");
				 }
			 }else{
				 HandleDataSource.write();
			//	 logger.info("切换--写库");
			 }			
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage());
		} catch (SecurityException e) {
			logger.error(e.getMessage());
		}
       
	}

	/**
	 * remove掉本地线程变量、防止内存泄漏
	 * @param point
	 */
	@After("aspect()")
	public void after(JoinPoint point) {
		HandleDataSource.clear();
	}
}
