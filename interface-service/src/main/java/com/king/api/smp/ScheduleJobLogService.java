package com.king.api.smp;


import java.util.List;
import java.util.Map;

import com.king.dal.gen.model.ScheduleJobLog;

/**
 * 定时任务日志
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
public interface ScheduleJobLogService {

	/**
	 * 根据ID，查询定时任务日志
	 */
	ScheduleJobLog queryObject(Long jobId);
	
	/**
	 * 查询定时任务日志列表
	 */
	List<ScheduleJobLog> queryList(Map<String, Object> map);
	
	/**
	 * 查询总数
	 */
	int queryTotal(Map<String, Object> map);
	
	/**
	 * 保存定时任务日志
	 */
	void save(ScheduleJobLog log);
	
}
