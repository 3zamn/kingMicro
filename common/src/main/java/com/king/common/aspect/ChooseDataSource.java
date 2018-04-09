package com.king.common.aspect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.king.common.enumeration.DataSourceType;

/**
 * 获取数据源
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月8日
 */
public class ChooseDataSource extends AbstractRoutingDataSource {
	public static Map<String, List<String>> METHODTYPE = new HashMap<String, List<String>>();
	 private Logger logger = LoggerFactory.getLogger(getClass());
	// 获取数据源名称
	protected Object determineCurrentLookupKey() {

	//	logger.info(HandleDataSource.getDataSource()==null?DataSourceType.write.getType():HandleDataSource.getDataSource());
		return HandleDataSource.getDataSource()==null?DataSourceType.write.getType():HandleDataSource.getDataSource();
	}

	
}
