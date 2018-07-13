package com.king.common.utils.thread;

/**
 * 在线程级别保存应用编码、交易流水号、操作用户编码
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年3月30日
 */
public class SerialNoHolder {
	public  static ThreadLocal<String> serialNo = new ThreadLocal<String>();//交易流程号
	public  static ThreadLocal<String> userCode = new ThreadLocal<String>();//用户编码
	public  static ThreadLocal<String> appCode = new ThreadLocal<String>();//应用编码

}
