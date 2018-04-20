package com.king.common.mongodb.mongo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.king.common.utils.ReflectUtil;

/**
 * Mongodb 基础数据操作仓库实现类
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月19日
 * @param <T>
 * @param <ID>
 */
@Repository
public class BaseMongoRepositoryImpl<T, ID extends Serializable> implements BaseMongoRepository<T, ID>{
	
	@Autowired
	private MongoTemplate  mongoTemplate;
	
	private Class<?> clz;
	
	public Class<?> getClazz() {
		clz = ReflectUtil.findParameterizedType(getClass(), 0);
		return clz;
	}
	
	public BaseMongoRepositoryImpl() {
		clz = this.getClazz();
	}
	
	@Override
	public void createCollection(String collectionName) {
		if (StringUtils.isEmpty(collectionName)) {
			collectionName = clz.getSimpleName();
		}
		
		if (!mongoTemplate.collectionExists(collectionName)) {
			mongoTemplate.createCollection(collectionName);
		}
	};
	
	@Override
	public void dropPersonCollection(String collectionName) {
		if (StringUtils.isEmpty(collectionName)) {
			collectionName = clz.getSimpleName();
		}
		
		if (mongoTemplate.collectionExists(collectionName)) {
			mongoTemplate.dropCollection(collectionName);
		}
	};

	
	private static <T> List<T> convertIterableToList(Iterable<T> entities) {

		if (entities instanceof List) {
			return (List<T>) entities;
		}

		int capacity = tryDetermineRealSizeOrReturn(entities, 10);

		if (capacity == 0 || entities == null) {
			return Collections.<T> emptyList();
		}

		List<T> list = new ArrayList<T>(capacity);
		for (T entity : entities) {
			list.add(entity);
		}

		return list;
	}
	
	private static int tryDetermineRealSizeOrReturn(Iterable<?> iterable, int defaultSize) {
		return iterable == null ? 0 : (iterable instanceof Collection) ? ((Collection<?>) iterable).size() : defaultSize;
	}

	@SuppressWarnings("deprecation")
	@Override
	public <S extends T> S update(S entity) {
		Assert.notNull(entity);
		mongoTemplate.save(entity);
		return entity;
	}

	@SuppressWarnings("deprecation")
	@Override
	public <S extends T> void update(Iterable<S> entities) {
		Assert.notNull(entities);
		List<S> result = convertIterableToList(entities);
		
		for (S entity : result) {
			update(entity);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findOne(ID id) {
		return  (T) mongoTemplate.findById(id, clz);
	}

	@Override
	public boolean exists(ID id) {
		return findOne(id) != null;
	}

	@Override
	public Iterable<T> findAll() {
		return findAll(new Query());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAll(Query query) {
		if (query == null) {
			return Collections.emptyList();
		}

		return (List<T>) mongoTemplate.find(query, clz, clz.getSimpleName());
	}

	@Override
	public long count() {
		String collectionName = clz.getSimpleName();
		return mongoTemplate.getCollection(collectionName).count();
	}

	@Override
	public void delete(ID id) {
		T entity = findOne(id);
		delete(entity);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void delete(T entity) {
		Assert.notNull(entity);
		mongoTemplate.remove(entity);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void delete(Iterable<? extends T> entities) {
		Assert.notNull(entities);
		for (T entity : entities) {
			delete(entity);
		}
 	}

	@Override
	public void deleteAll() {
		String collectionName = clz.getSimpleName();
		mongoTemplate.remove(new Query(), collectionName);
	}

	@Override
	public Iterable<T> findAll(Sort sort) {
		return findAll(new Query().with(sort));
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		Long count = count();
		List<T> list = findAll(new Query().with(pageable));

		return new PageImpl<T>(list, pageable, count);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void insert(T entity) {
		Assert.notNull(entity);
		mongoTemplate.insert(entity);
	}

}
