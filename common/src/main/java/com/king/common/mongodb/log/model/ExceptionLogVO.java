package com.king.common.mongodb.log.model;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 异常日志信息
 * 对应MongoDB集合名称，与类名一致
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月19日
 */
@Document(collection = "ExceptionLogVO")
public class ExceptionLogVO extends CommonLogVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4254135989361051946L;
	private String exceptionMsg;
	
	public String getExceptionMsg() {
		return exceptionMsg;
	}
	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}
	
}
