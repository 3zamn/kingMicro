package com.king.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.king.api.smp.SysUserService;
import com.king.dal.gen.model.smp.SysUser;


@Component("testTask")
public class TestTask {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private SysUserService sysUserService;
	 ExecutorService cachedThreadPool = Executors.newFixedThreadPool(10);
	public void test(final String params){
			   for(int i=0;i<10;i++){
				   cachedThreadPool.execute(new Runnable() {
					    public void run() {
					    	 try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
					    	logger.info("我是带参数的test方法，正在被执行，参数为：" + params);
					    }
					   });
			   }
		 }	
	
	 ExecutorService fixedThreadPool = Executors.newFixedThreadPool(50);
	public void test2(){
		  for(int i=0;i<50;i++){
			 
			   fixedThreadPool.execute(new Runnable() {
			    public void run() {
			     try {
			    	 logger.info("我是不带参数的test2方法，正在被执行");
			      Thread.sleep(1000);
			     } catch (InterruptedException e) {
			      e.printStackTrace();
			     }
			    }
			   });
		  }
		
	}
}
