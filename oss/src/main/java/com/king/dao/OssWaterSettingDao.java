package com.king.dao;

import com.king.dal.gen.model.oss.OssWaterSetting;
import com.king.dal.gen.dao.BaseDao;


/**
 * 
 * 
 * @author king chen
 * @email 396885563@qq.com
 * @date 2018-07-30 10:51:12
 */
public interface OssWaterSettingDao extends BaseDao<OssWaterSetting> {
	
	/**
	 * 根据用户Id查询水印
	 * @param userId
	 * @return
	 */
	OssWaterSetting queryByUser(Object userId);
}
