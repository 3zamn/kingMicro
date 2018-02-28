package com.king.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;


/**
 * 实体解析
 * @author king chen
 * @emai 396885563@qq.com
 * @data2018年1月11日
 */
@Component("enttyMapperResolver")
public  class EnttyMapperResolver {
	 @Autowired
	    private  RedisUtils redisUtils;
	/**
	 * 根据实体、熟悉获取字段
	 * @param enttyName
	 * @param attribute
	 * @return
	 */
	public  JSONObject getColumn(String enttyName,String attribute){
		Object column = redisUtils.hget(RedisKeys.getEnttyKey(enttyName), attribute);
		JSONObject column_json = new JSONObject();
		if(column !=null){
			column_json =JSONObject.parseObject(column.toString());
		}
		return column_json;
	}
	
	/**
	 * 实体是否存在该属性
	 * @param enttyName
	 * @param attribute
	 * @return
	 */
	public Boolean isExistAttribute(String enttyName,String attribute){
		Boolean isExist =false;
		String key =RedisKeys.getEnttyKey(enttyName);
		Object column = redisUtils.hget(key.trim(),attribute.trim());
		if(column !=null){
			isExist =true;  
		}
		return isExist;
	}

}
