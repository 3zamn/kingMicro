package com.king.services.spi;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.king.api.smp.SysLogService;
import com.king.dal.gen.model.SysLog;
import com.king.dao.SysLogDao;



@Service("sysLogService")
public class SysLogServiceImpl implements SysLogService {
	@Autowired
	private SysLogDao sysLogDao;
	
	@Override
	public SysLog queryObject(Long id){
		return sysLogDao.queryObject(id);
	}
	
	@Override
	public List<SysLog> queryList(Map<String, Object> map){
		return sysLogDao.queryList(map);
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return sysLogDao.queryTotal(map);
	}
	
	@Override
	public void save(SysLog sysLog){
		sysLogDao.save(sysLog);
	}
	
	@Override
	public void update(SysLog sysLog){
		sysLogDao.update(sysLog);
	}
	
	@Override
	public void delete(Long id){
		sysLogDao.delete(id);
	}
	
	@Override
	public void deleteBatch(Long[] ids){
		sysLogDao.deleteBatch(ids);
	}
	
}
