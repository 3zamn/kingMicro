package com.king.common.utils.validator;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 限制map大小、防止传参数过大。
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年5月21日
 */
public abstract class LimitMap implements java.util.Map<String, Object> {
		LinkedHashMap<String, Object> Map= new LinkedHashMap<String, Object>() {
			int maximumSize = 200;
				private static final long serialVersionUID = 1L;
				protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
		            return size() > maximumSize;
		        }
		    };
}

