package com.king.utils;

import com.caucho.hessian.io.JavaUnsharedSerializer;

/**
 * Hessian反序列化找不到此类会警告
 * 空类
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年6月4日
 */
public class Query extends JavaUnsharedSerializer{

	public Query(Class<?> cl) {
		super(cl);
	}

}