package com.king.utils;

/**
 * 在线程级别获取token
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年3月30日
 */
public class TokenHolder {
	public  static ThreadLocal<String> token = new ThreadLocal<String>();

}
