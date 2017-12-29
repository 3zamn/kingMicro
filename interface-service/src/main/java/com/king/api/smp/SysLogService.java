package com.king.api.smp;


import java.util.List;
import java.util.Map;

import com.king.dal.gen.model.SysLog;


/**
 * 系统日志
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
public interface SysLogService {
	
	SysLog queryObject(Long id);
	
	List<SysLog> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(SysLog sysLog);
	
	void update(SysLog sysLog);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);
}
