package com.king.dal.gen.service;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.king.dal.gen.dao.BaseDao;

/**
 * service基础实现类,
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年3月5日
 * @param <T>
 */
@Transactional
public abstract class BaseServiceImpl<T> implements BaseService<T> {

	@Autowired 
	protected BaseDao<T> baseDao;
	
	/**
	 * getBaseDao()的默认实现,子类可覆盖此方法
	 * @return
	 */
	protected BaseDao<T> getBaseDao() {
		return baseDao;
	}
	

	public void delete(Object id) {
		getBaseDao().delete(id);
	}
	
	public void update(T obj) {
		getBaseDao().update(obj);
	}


	public void deleteBatch(Object[] ids) {
		getBaseDao().deleteBatch(ids);	
	}


	public void save(T obj) {
		getBaseDao().save(obj);
		
	}


	public int queryTotal(Map<String, Object> map) {	
		return getBaseDao().queryTotal(map);
	}
	
	public int queryTotal() {	
		return getBaseDao().queryTotal();
	}


	public T queryObject(Long id) {
		T obj=getBaseDao().queryObject(id);
		return obj;
	}


	public List<T> queryList(Map<String, Object> map) {
		List<T> list =getBaseDao().queryList(map);
		return list;
	}
	
}
