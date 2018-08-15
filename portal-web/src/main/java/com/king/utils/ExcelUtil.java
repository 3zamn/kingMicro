package com.king.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 高性能Excel导入导出--支持数百万级别大数据
 * 导入采用xml方式分段逐步解析避免大数据导致内存溢出
 * 导出同样采用分段分页list中开辟有限存储空间,用完了清空
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年8月3日
 * @param <T>
 */
public class ExcelUtil<T> {
	private Logger logger = LoggerFactory.getLogger(getClass());

	

}