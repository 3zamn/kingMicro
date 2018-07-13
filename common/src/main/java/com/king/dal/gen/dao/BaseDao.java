package com.king.dal.gen.dao;

import java.util.List;
import java.util.Map;

/**
 * 基础Dao(还需在XML文件里，有对应的SQL语句)
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 * @param <T>
 */
public interface BaseDao<T> {
	
	int save(T t);
	
	int save(Map<String, Object> map);
	
	int saveBatch(List<T> list);
	
	int update(T t);
	
	int update(Map<String, Object> map);
	
	int delete(Object id);
	
	int delete(Map<String, Object> map);
	
	int deleteBatch(Object[] id);

	T queryObject(Object id);
	
	List<T> queryList(Map<String, Object> map);
	
	List<T> queryBatch(Object[] id);
	
	int queryTotal(Map<String, Object> map);

	int queryTotal();
}
