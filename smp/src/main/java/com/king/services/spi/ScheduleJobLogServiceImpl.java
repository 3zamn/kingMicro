package com.king.services.spi;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.king.api.smp.ScheduleJobLogService;
import com.king.dal.gen.model.smp.ScheduleJobLog;
import com.king.dao.ScheduleJobLogDao;

@SuppressWarnings("rawtypes")
@Service("scheduleJobLogService")
public class ScheduleJobLogServiceImpl implements ScheduleJobLogService {
	@Autowired
	private ScheduleJobLogDao scheduleJobLogDao;
	
	public ScheduleJobLog queryObject(Long jobId) {
		return scheduleJobLogDao.queryObject(jobId);
	}

	public List<ScheduleJobLog> queryList(Map<String, Object> map) {
		return scheduleJobLogDao.queryList(map);
	}

	public int queryTotal(Map<String, Object> map) {
		return scheduleJobLogDao.queryTotal(map);
	}

	public void save(ScheduleJobLog log) {
		scheduleJobLogDao.save(log);
	}

}
