package com.king.common.annotation;

import java.lang.annotation.*;

/**
 *  动态列数据处理
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年6月5日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicCol {
    /**  列参数默认 * 返回所有列 */
    String paramsCol() default  " * ";
    /** 实体名称*/
    String entity() default  "";
}
