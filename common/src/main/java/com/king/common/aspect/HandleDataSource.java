package com.king.common.aspect;

import com.king.common.enumeration.DataSourceType;

/**
 * 数据源线程级别处理
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月8日
 */
public class HandleDataSource {
	  private static final ThreadLocal<String> local = new ThreadLocal<>();

	    public static ThreadLocal<String> getLocal() {
	        return local;
	    }

	    /**
	     * 读可能是多个库
	     */
	    public static void read() {
	        local.set(DataSourceType.read.getType());
	    }

	    /**
	     * 写只有一个库
	     */
	    public static void write() {
	        local.set(DataSourceType.write.getType());
	    }

	    public static String getDataSource() {
	        return local.get();
	    }

		public static void clear() {
			local.remove();
		}
}
