package com.king.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 测试
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年8月15日
 */
@Component("testTask")
public class TestTask {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public void test(final String params) {
		logger.info("我是带参数的test方法，正在被执行，参数为：" + params);
	}

	ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);

	public void test2() {
		logger.info("我是不带参数的test2方法，正在被执行");
	}
}
