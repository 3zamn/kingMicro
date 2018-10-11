package com.king.utils;

import com.alibaba.com.caucho.hessian.io.JavaSerializer;

/**
 * Hessian反序列化找不到此类会警告
 * 空类
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年6月4日
 */
public class Query extends JavaSerializer{

	public Query(Class<?> cl) {
		super(cl, null);
	}

}
