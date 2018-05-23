package com.king.listener;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.king.common.utils.entityMapper.GenEntityMapper;

/**
 * spring 容器启动后加载解析表与实体的映射
 * 
 * @author king chen
 * @emai 396885563@qq.com
 * @data2018年1月11日
 */
@Component
public class ApplicationInitializedListener {

	@Autowired
	private GenEntityMapper genEnttyMapper;

	@PostConstruct
	public void loadEnttyMapper() {
		// spring 启动后加载
		genEnttyMapper.generateEnttyMapper();
	}

}
