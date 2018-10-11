package com.king.common.utils.file.xml.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target; 
/**
 * 指定根节点
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年8月16日
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE) 
public @ interface Root {
  /**
 * 指定根节点名称
 */
public String name();
}
