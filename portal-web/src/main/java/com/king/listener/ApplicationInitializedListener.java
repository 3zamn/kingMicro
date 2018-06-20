package com.king.listener;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * spring 容器启动后加载@
 * @author king chen
 *
 */
@Component
public  class  ApplicationInitializedListener {
	 Logger logger = LoggerFactory.getLogger(getClass());
	@PostConstruct
	public  void loadEnttyMapper(){
		logger.info("spring容器启动完毕");
	}

}
