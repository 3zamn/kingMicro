package com.king.common.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.*;

/**
 *  动态列展示、导出Excel
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年6月5日
 */
@Target(value = { FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PropertyExt {

	/**  列表中是否展示此列*/
	boolean isShow() default true;
	/**  列表中展示的排序*/
//	int showOrder() default 0;
	/**  是否导出此列*/
	boolean isExport() default true;
	/**  导出此列的排序*/
//	int exportOrder()  default 0;
    /** Excel单元格提示信息*/
    public abstract String tips() default "";
    /** 设置Excel单元格下拉数据*/
    public abstract String[] combox() default {};
}
