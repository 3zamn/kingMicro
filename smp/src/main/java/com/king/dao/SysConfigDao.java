package com.king.dao;


import org.apache.ibatis.annotations.Param;

import com.king.dal.gen.dao.BaseDao;
import com.king.dal.gen.model.smp.SysConfig;

/**
 * 系统配置信息
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
public interface SysConfigDao extends BaseDao<SysConfig> {

	/**
	 * 根据key，查询value
	 */
	SysConfig queryByKey(String paramKey);

	/**
	 * 根据key，更新value
	 */
	int updateValueByKey(@Param("key") String key, @Param("value") String value);
	
}
