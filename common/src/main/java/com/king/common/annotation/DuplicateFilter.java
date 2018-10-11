package com.king.common.annotation;

import java.lang.annotation.*;

/**
 *  防止表单重复提交过滤
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年9月30日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DuplicateFilter {

	 boolean check() default true;
}
