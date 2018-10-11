package com.king.common.utils.date;

/**
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年4月20日
 */
public enum DatePattern {
	_YYYYMMDD ("yyyy-MM-dd"),
	_YYYYMMDDHHMISS("yyyy-MM-dd HH:mm:ss"),
	YYYYMMDD ("yyyyMMdd"),
	YYYYMMDDHHMISS("yyyyMMddHHmmss");
	
	private final String value;
	private DatePattern(String value) {
		this.value = value;
	}
	
	public String toString(){
		return value;
	}
	
}
