package com.king.common.dataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 获取数据源
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年4月8日
 */
public class ChooseDataSource extends AbstractRoutingDataSource {
	 private Logger logger = LoggerFactory.getLogger(getClass());
	// 获取数据源名称
	protected Object determineCurrentLookupKey() {

	//	logger.info(HandleDataSource.getDataSource()==null?DataSourceType.write.getType():HandleDataSource.getDataSource());
		return HandleDataSource.getDataSource()==null?DataSourceType.write.getType():HandleDataSource.getDataSource();
	}

	
}
