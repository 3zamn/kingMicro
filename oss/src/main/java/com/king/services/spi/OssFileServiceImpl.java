package com.king.services.spi;

import org.springframework.stereotype.Service;
import com.king.dal.gen.service.BaseServiceImpl;
import com.king.dal.gen.model.oss.OssFile;
import com.king.api.oss.OssFileService;



@Service("ossFileService")
public class OssFileServiceImpl extends BaseServiceImpl<OssFile> implements OssFileService {
	
	
}
