package com.king.common.utils.pattern;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 反射工具类
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月19日
 */
public class ReflectUtil {
	@SuppressWarnings("unchecked")
	public static <T> Class<T> findParameterizedType(Class<?> clazz, int index) {
		Type parameterizedType = clazz.getGenericSuperclass();
		if (!(parameterizedType instanceof ParameterizedType)) {
			parameterizedType = clazz.getSuperclass().getGenericSuperclass();
		}
		if (!(parameterizedType instanceof ParameterizedType)) {
			return null;
		}
		Type[] actualTypeArguments = ((ParameterizedType) parameterizedType).getActualTypeArguments();
		if (actualTypeArguments == null || actualTypeArguments.length == 0) {
			return null;
		}
		return (Class<T>) actualTypeArguments[index];
	}
}
