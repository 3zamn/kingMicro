package test;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import test.redis.IdGenerator;

/**
 * 
 * 模拟高并发线程安全问题
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年5月11日
 */
@RunWith(SpringJUnit4ClassRunner.class)  
@WebAppConfiguration
@ContextConfiguration({"classpath*:applicationContext.xml","classpath*:redis.xml"}) 
public class IdGen {
	@Autowired
	private IdGenerator idGenerator;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Test 
	public void  test(){
		int clientTotal = 1000000;
		// 同时并发执行的线程数
		int threadTotal = 100;
		 ExecutorService executorService = Executors.newCachedThreadPool();
		    //信号量，此处用于控制并发的线程数
		    final Semaphore semaphore = new Semaphore(threadTotal);
		    //闭锁，可实现计数器递减
		    final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
		    Long begin = new Date().getTime();  
		    for (int i = 0; i < clientTotal ; i++) {
		      executorService.execute(() -> {
		        try {//执行此方法用于获取执行许可，当总计未释放的许可数不超过60000时，	         	 
		          semaphore.acquire(); //允许通行，否则线程阻塞等待，直到获取到许可。
		   //       idGenerator.set("test:"+UUID.randomUUID().toString(), UUID.randomUUID().toString(), 300);
		          idGenerator.incrementHash("id", "value", null);         
		          semaphore.release(); //释放许可
		        } catch (Exception e) {
		        	logger.error(e.getMessage());
		        }       
		        countDownLatch.countDown(); //闭锁减一
		      });
		    }
		    try {
				countDownLatch.await();//线程阻塞，直到闭锁值为0时，阻塞才释放，继续往下执行
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				Thread.currentThread().interrupt();
			}
		    executorService.shutdown();
		    Long end = new Date().getTime();  
		    System.out.println("cast : " + (end - begin) / 1000 + "s");  		
	}
}
