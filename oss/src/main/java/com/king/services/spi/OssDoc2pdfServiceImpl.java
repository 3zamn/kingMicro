package com.king.services.spi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.king.dal.gen.service.BaseServiceImpl;
import com.king.dao.OssWaterSettingDao;
import com.king.dal.gen.model.oss.OssDoc2pdf;
import com.king.dal.gen.model.oss.OssWaterSetting;
import com.king.api.oss.OssDoc2pdfService;

@Service("ossDoc2pdfService")
public class OssDoc2pdfServiceImpl extends BaseServiceImpl<OssDoc2pdf> implements OssDoc2pdfService {
	
	@Autowired
	private OssWaterSettingDao ossWaterSettingDao;
	
	public void saveOrUpdate(OssWaterSetting obj) {
		if(obj.getId()==null){
			ossWaterSettingDao.save(obj);
		}else{
			ossWaterSettingDao.update(obj);
		}
		
	}



	@Override
	public OssWaterSetting queryWaterSetting(Object userId) {
		return ossWaterSettingDao.queryByUser(userId);
	}
}
