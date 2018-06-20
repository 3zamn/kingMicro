package com.king.gen.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.king.common.utils.pattern.StringToolkit;

/**
 * 切换数据源
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月8日
 */
@Aspect
@Component
public class DataSourcesAspect {
	 private Logger logger = LoggerFactory.getLogger(getClass());

	@Pointcut("execution(* com.king.gen.service.impl.*.*(..))")
	public void aspect() {
		//数据源切面日志
	}

	/**
	 * 配置前置通知,使用在方法aspect()上注册的切入点
	 */
	@Before("aspect()")
	public void before(JoinPoint point) {
        Object[] args = point.getArgs();
		try {
			//根据第一个参数获取数据源
			String dataSource=StringToolkit.getObjectString(args[0]);
			logger.debug("当前数据源:"+dataSource);
			HandleGenDataSource.setDataSource(dataSource);
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
		HandleGenDataSource.clear();
	}
}
