package com.king.common.annotation;

import java.lang.annotation.*;


/**
 * 动态列
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年8月27日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicColFilter {

	 boolean status() default true;
}
