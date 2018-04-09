package com.king.services.spi;


import org.springframework.stereotype.Service;

import com.king.api.smp.SysLogService;
import com.king.dal.gen.model.smp.SysLog;
import com.king.dal.gen.service.BaseServiceImpl;



@Service("sysLogService")
public class SysLogServiceImpl extends BaseServiceImpl<SysLog> implements SysLogService {
	

}
