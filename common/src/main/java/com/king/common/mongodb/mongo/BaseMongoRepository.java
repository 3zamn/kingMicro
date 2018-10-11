package com.king.common.mongodb.mongo;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;

/**
 *  MongodbDB基础数据操作仓库接口
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年4月19日
 * @param <T>
 * @param <ID>
 */
public interface BaseMongoRepository<T, ID extends Serializable>{
	
    void createCollection(String collectionName);
    
    void dropPersonCollection(String collectionName);
	
	<S extends T> S update(S entity);
	
	<S extends T> void update(Iterable<S> entities);

	T findOne(ID id);

	boolean exists(ID id);

	Iterable<T> findAll();

	List<T> findAll(Query query);

	long count();
	long count( Query query);
	void delete(ID id);
	
	void delete(T entity);
	
	void delete(Iterable<? extends T> entities);
	
	void deleteAll();
		
	Iterable<T> findAll(Sort sort);
	
	Page<T> findAll(Pageable pageable);
	
	Page<T> findAll(Query query,Pageable pageable);

	void insert(T entity);

}
