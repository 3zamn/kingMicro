package com.king.api.oss;

import com.king.dal.gen.model.oss.OssDoc2pdf;
import com.king.dal.gen.model.oss.OssWaterSetting;
import com.king.dal.gen.service.BaseService;


/**
 * 文档转pdf、生成图片
 * 
 * @author king chen
 * @email 396885563@qq.com
 * @date 2018-07-25 10:14:40
 */
public interface OssDoc2pdfService extends BaseService<OssDoc2pdf>{
	/**
	 * 根据ID，查询
	 */
	 OssWaterSetting queryWaterSetting(Object userId);
	/**
	 * 更新
	 */
	void  saveOrUpdate(OssWaterSetting object);

}
