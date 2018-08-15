package com.king.common.annotation;

import java.lang.annotation.*;

/**
 *   数据权限过滤注解
 * @author King chen
 * @date 2017年12月25日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataFilter {
    /**  表的别名 */
    String tableAlias() default  "";

    /**  true：没有本部门数据权限，也能查询本人数据 */
    boolean user() default true;
}
