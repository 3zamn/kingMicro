package com.king.listener;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.king.common.utils.ShiroUtils;

/**
 * spring 容器启动后加载@
 * @author king chen
 *
 */
@Component
public  class  ApplicationInitializedListener {
	@Value("${king.swagger.status}")
	private String redisOpen;
//	public static HashMap<String, List<HashMap<String,String>>> mapper=null;
//	@PostConstruct
	public  void loadEnttyMapper(){
	//	  System.out.println(ShiroUtils.getUserEntity());      

		System.out.println("spring容器启动完毕");
		
		
	}

}
