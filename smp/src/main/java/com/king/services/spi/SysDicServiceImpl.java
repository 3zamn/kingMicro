package com.king.services.spi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.api.smp.SysDicService;
import com.king.dal.gen.model.smp.SysDic;
import com.king.dal.gen.service.BaseServiceImpl;
import com.king.dao.SysDicDao;

@Service("sysDicService")
public class SysDicServiceImpl extends BaseServiceImpl<SysDic> implements SysDicService {
	
	@Autowired
	private SysDicDao sysDicDao;
    
	@Override
	@Transactional(readOnly = true)
	public List<SysDic> queryDicList(String code) {
		// TODO Auto-generated method stub
		return sysDicDao.queryDicList(code);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<SysDic> queryParentList(long parentId) {
		// TODO Auto-generated method stub
		return sysDicDao.queryParentList(parentId);
	}
	
	@Override
	@Transactional
	public void save(SysDic config) {
		sysDicDao.save(config);
		
	}
	
	
}
