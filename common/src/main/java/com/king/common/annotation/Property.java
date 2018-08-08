package com.king.common.annotation;

import java.lang.annotation.*;

/**
 *  动态列展示、导出Excel
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年6月5日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Property {
    /** 实体名称*/
    String entity() default  "";
	/**  列名*/
	String value() default "";
	/**  列表中是否展示此列*/
	boolean isShow() default true;
	/**  列表中展示的排序*/
	int showOrder() ;
	/**  是否导出此列*/
	boolean isExport() default true;
	/**  导出此列的排序*/
	int exportOrder();
    /** Excel单元格提示信息*/
    public abstract String tips() default "";
    /** 设置Excel单元格下拉数据*/
    public abstract String[] combox() default {};
}
