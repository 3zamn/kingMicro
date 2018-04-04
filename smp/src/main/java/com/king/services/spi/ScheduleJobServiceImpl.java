package com.king.services.spi;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.api.smp.ScheduleJobService;
import com.king.common.utils.Constant;
import com.king.common.utils.Page;
import com.king.dal.gen.model.smp.ScheduleJob;
import com.king.dal.gen.model.smp.ScheduleJobLog;
import com.king.dal.gen.service.BaseServiceImpl;
import com.king.dao.ScheduleJobDao;
import com.king.dao.ScheduleJobLogDao;
import com.king.utils.ScheduleUtils;

@Service("scheduleJobService")
public class ScheduleJobServiceImpl extends BaseServiceImpl<ScheduleJob>implements ScheduleJobService {
	@Autowired
    private Scheduler scheduler;
	@Autowired
	private ScheduleJobDao schedulerJobDao;
	@Autowired
	private ScheduleJobLogDao scheduleJobLogDao;
	/**
	 * 项目启动时，初始化定时器
	 */
	@PostConstruct
	public void init(){
		List<ScheduleJob> scheduleJobList = schedulerJobDao.queryList(new HashMap<String, Object>());
		for(ScheduleJob scheduleJob : scheduleJobList){
			CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getJobId());
            //如果不存在，则创建
            if(cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            }else {
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
		}
	}
	
	@Override
	@Transactional
	public void save(ScheduleJob scheduleJob) {
		scheduleJob.setCreateTime(new Date());
		scheduleJob.setStatus(Constant.ScheduleStatus.NORMAL.getValue());
        schedulerJobDao.save(scheduleJob);
        
        ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
    }
	
	@Override
	@Transactional
	public void update(ScheduleJob scheduleJob) {
        ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
                
        schedulerJobDao.update(scheduleJob);
    }

	@Override
	@Transactional
    public void deleteBatch(Long[] jobIds) {
    	for(Long jobId : jobIds){
    		ScheduleUtils.deleteScheduleJob(scheduler, jobId);
    	}
    	
    	//删除数据
    	schedulerJobDao.deleteBatch(jobIds);
	}

	@Override
    public int updateBatch(Long[] jobIds, int status){
    	Map<String, Object> map = new HashMap<>();
    	map.put("list", jobIds);
    	map.put("status", status);
    	return schedulerJobDao.updateBatch(map);
    }
    
	@Override
	@Transactional
    public void run(Long[] jobIds) {
    	for(Long jobId : jobIds){
    		ScheduleUtils.run(scheduler, queryObject(jobId));
    	}
    }

	@Override
	@Transactional
    public void pause(Long[] jobIds) {
        for(Long jobId : jobIds){
    		ScheduleUtils.pauseJob(scheduler, jobId);
    	}
        
    	updateBatch(jobIds, Constant.ScheduleStatus.PAUSE.getValue());
    }

	@Override
	@Transactional
    public void resume(Long[] jobIds) {
    	for(Long jobId : jobIds){
    		ScheduleUtils.resumeJob(scheduler, jobId);
    	}

    	updateBatch(jobIds, Constant.ScheduleStatus.NORMAL.getValue());
    }

	@Override
	public ScheduleJobLog queryScheduleJobLog(Long jobId) {
		// TODO Auto-generated method stub
		return scheduleJobLogDao.queryObject(jobId);
	}

	@Override
	public List<ScheduleJobLog> queryScheduleJobLogList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return scheduleJobLogDao.queryList(map);
	}

	@Override
	public int queryScheduleJobLogTotal(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return scheduleJobLogDao.queryTotal();
	}

	@Override
	public void save(ScheduleJobLog log) {
		// TODO Auto-generated method stub
		scheduleJobLogDao.save(log);
	}
	
	@Transactional(readOnly = true)
	public Page getPageScheduleJobLog(Map<String, Object> map) {
		List<ScheduleJobLog> list =scheduleJobLogDao.queryList(map);
		int totalCount =scheduleJobLogDao.queryTotal(map);
		Page page = new Page(list, totalCount, (int)map.get("limit"), (int)map.get("page"));	
		return page;
	}
    
}
