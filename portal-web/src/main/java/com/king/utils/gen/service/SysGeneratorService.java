package com.king.utils.gen.service;

import java.util.List;
import java.util.Map;

import com.king.common.utils.Page;


/**
 * 代码生成器
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年3月12日
 */
public interface SysGeneratorService {
	
	Map<String, String> queryTable(String dataSource,String tableName);
	
	List<Map<String, String>> queryColumns(String dataSource,String tableName);
	
	/**
	 * 生成代码
	 */
	byte[] generatorCode(String dataSource,String[] tableNames);
	
	/**
	 * 分页查询列表
	 */
	public Page getPage(String dataSource,Map<String, Object> map); 
}
