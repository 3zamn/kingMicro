package com.king.listener;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * spring 容器启动后加载@
 * @author king chen
 *
 */
@Component
public  class  ApplicationInitializedListener {
	
	public static HashMap<String, List<HashMap<String,String>>> mapper=null;
	@PostConstruct
	public static void loadEnttyMapper(){
	//	new GenEnttyMapper();
		//spring 启动后加载
	//	HashMap<String, List<HashMap<String,String>>> result = GenEnttyMapper.generateEnttyMapper(); 
	//	mapper =result;
	//	System.out.println("加载实体映射解析完毕"+result);
		
		
	}

}
