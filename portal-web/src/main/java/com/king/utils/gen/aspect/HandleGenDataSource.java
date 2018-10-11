package com.king.utils.gen.aspect;

/**
 * 数据源线程级别处理
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年4月8日
 */
public class HandleGenDataSource {
	  private static final ThreadLocal<String> local_data = new ThreadLocal<>();

	    public static ThreadLocal<String> getLocal() {
	        return local_data;
	    }
	    public static void setDataSource(String dataSource) {
	    	local_data.set(dataSource);
	    }
	    public static String getDataSource() {
	        return local_data.get();
	    }

		public static void clear() {
			local_data.remove();
		}
}
