package test.xml.annotation;
import java.lang.annotation.ElementType; 
import java.lang.annotation.Retention; 
import java.lang.annotation.RetentionPolicy; 
import java.lang.annotation.Target; 
/**
 * 定义需要解析的实体属性
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年8月16日
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.FIELD) 
public @ interface Column {
		
  /**
 * 字段名
 */
public String name();
  
  /**
 * true：值为空也生成、false相反
 */
public String required() default "1";
}
