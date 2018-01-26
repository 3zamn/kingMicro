package com.king.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 全局范围spring容器工具
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年1月26日
 */
public class SpringCtxUtils implements ApplicationContextAware{
	public static ClassPathXmlApplicationContext ctx;
	private static ApplicationContext webCtx;     //Web环境Spring应用上下文环境
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringCtxUtils.webCtx = applicationContext;
	}
	

	public static void init(String[] springConfigs) {
		ctx = new ClassPathXmlApplicationContext(springConfigs);
	}

	public static void start() {
		ctx.start();
	}

	public static void close() {
		ctx.close();
	}

	public static <T> T getBean(Class<T> requiredType) {
		if(ctx!=null){
			return ctx.getBean(requiredType);
		}else{
			return webCtx.getBean(requiredType);
		}
	}

	public static <T> T getBean(String beanName, Class<T> requiredType) {
		if(ctx!=null){
			return ctx.getBean(beanName, requiredType);
		}else{
			return webCtx.getBean(beanName, requiredType);
		}
	}

	public static Object getBean(String name) throws BeansException {
		if(ctx!=null){
			return ctx.getBean(name);
		}else{
			return webCtx.getBean(name);
		}
	}

}
