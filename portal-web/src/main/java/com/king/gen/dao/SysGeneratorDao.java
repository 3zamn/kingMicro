package com.king.gen.dao;

import java.util.List;
import java.util.Map;


/**
 * 代码生成器
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年3月12日
 */
public interface SysGeneratorDao {
	
	List<Map<String, Object>> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	Map<String, String> queryTable(String tableName);
	
	List<Map<String, String>> queryColumns(String tableName);
}
