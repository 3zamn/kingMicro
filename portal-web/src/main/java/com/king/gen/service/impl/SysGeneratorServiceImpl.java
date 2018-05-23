package com.king.gen.service.impl;


import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.common.utils.Page;
import com.king.gen.dao.SysGeneratorDao;
import com.king.gen.service.SysGeneratorService;
import com.king.gen.utils.GenUtils;

@Service("sysGeneratorService")
public class SysGeneratorServiceImpl implements SysGeneratorService {
	@Autowired
	private SysGeneratorDao sysGeneratorDao;

/*	@Override
	public List<Map<String, Object>> queryList(Map<String, Object> map) {
		return sysGeneratorDao.queryList(map);
	}

	@Override
	public int queryTotal(Map<String, Object> map) {
		return sysGeneratorDao.queryTotal(map);
	}*/

	@Override
	public Map<String, String> queryTable(String dataSource,String tableName) {
		return sysGeneratorDao.queryTable(tableName);
	}

	@Override
	public List<Map<String, String>> queryColumns(String dataSource,String tableName) {
		return sysGeneratorDao.queryColumns(tableName);
	}

	@Override
	public byte[] generatorCode(String dataSource,String[] tableNames) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(outputStream);
		
		for(String tableName : tableNames){
			//查询表信息
			Map<String, String> table = queryTable(dataSource,tableName);
			//查询列信息
			List<Map<String, String>> columns = queryColumns(dataSource,tableName);
			//生成代码
			GenUtils.generatorCode(table, columns, zip);
		}
		IOUtils.closeQuietly(zip);
		return outputStream.toByteArray();
	}

	public Page getPage(String dataSource,Map<String, Object> map) {
		List<Map<String, Object>> list =sysGeneratorDao.queryList(map);
		int totalCount =sysGeneratorDao.queryTotal(map);
		Page page = new Page(list, totalCount, (int)map.get("limit"), (int)map.get("page"));	
		return page;
	}

}
