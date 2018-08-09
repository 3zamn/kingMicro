package com.king.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 系统日志注解
 * @author King chen
 * @date 2017年12月25日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

	String value() default "";
	
	/*更新操作--记录改前后值*/
	boolean update() default false;
	
	/*删除操作--记录删除前记录*/
	boolean delete() default false;
	
	/*所在模块*/
	String module() default "";
	
	/*接口*/
	Class serviceClass() default Log.class;
	
	/*方法*/
	String method() default "queryObject";
}
