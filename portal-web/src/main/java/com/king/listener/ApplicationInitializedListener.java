package com.king.listener;

import org.springframework.stereotype.Component;

/**
 * spring 容器启动后加载@
 * @author king chen
 *
 */
@Component
public  class  ApplicationInitializedListener {
//	@PostConstruct
	public  void loadEnttyMapper(){
	//	  System.out.println(ShiroUtils.getUserEntity());      

		System.out.println("spring容器启动完毕");
		
		
	}

}
