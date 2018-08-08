package com.king.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("testTask")
public class TestTask {
	private Logger logger = LoggerFactory.getLogger(getClass());
	ExecutorService cachedThreadPool = Executors.newFixedThreadPool(10);//后面可以适当调整大小

	public void test(final String params) {
		for (int i = 0; i < 10; i++) {
			cachedThreadPool.execute(new Runnable() {
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						logger.error(e.getMessage());
						Thread.currentThread().interrupt();
					}
					logger.info("我是带参数的test方法，正在被执行，参数为：" + params);
				}
			});
		}
		cachedThreadPool.shutdown();//释放掉
	}

	ExecutorService fixedThreadPool = Executors.newFixedThreadPool(50);

	public void test2() {
		for (int i = 0; i < 50; i++) {

			fixedThreadPool.execute(new Runnable() {
				public void run() {
					try {
						logger.info("我是不带参数的test2方法，正在被执行");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger.error(e.getMessage());
						Thread.currentThread().interrupt();
					}
				}
			});
		}
		fixedThreadPool.shutdown();//释放掉
	}
}
