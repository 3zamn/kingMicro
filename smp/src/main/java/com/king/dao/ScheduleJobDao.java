package com.king.dao;


import java.util.Map;

import com.king.dal.gen.dao.BaseDao;
import com.king.dal.gen.model.smp.ScheduleJob;

/**
 * 定时任务
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
public interface ScheduleJobDao extends BaseDao<ScheduleJob> {
	
	/**
	 * 批量更新状态
	 */
	int updateBatch(Map<String, Object> map);
}
