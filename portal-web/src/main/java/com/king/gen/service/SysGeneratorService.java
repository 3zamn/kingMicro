package com.king.gen.service;

import java.util.List;
import java.util.Map;

import com.king.common.utils.Page;


/**
 * 代码生成器
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年3月12日
 */
public interface SysGeneratorService {
	
	List<Map<String, Object>> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	Map<String, String> queryTable(String tableName);
	
	List<Map<String, String>> queryColumns(String tableName);
	
	/**
	 * 生成代码
	 */
	byte[] generatorCode(String[] tableNames);
	
	/**
	 * 分页查询列表
	 */
	public Page getPage(Map<String, Object> map); 
}
