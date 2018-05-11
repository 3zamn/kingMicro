package com.king.common.utils.pattern;
import org.apache.commons.lang3.StringUtils;

import com.king.common.utils.exception.RRException;

/**
 * SQL过滤
 * @author King chen
 * @date 2017年12月25日
 */
public class SQLFilter {

    /**
     * SQL注入过滤
     * @param str  待验证的字符串
     */
    public static String sqlInject(String str){
        if(StringUtils.isBlank(str)){
            return null;
        }
        //去掉'|"|;|\字符
        str = StringUtils.replace(str, "'", "");
        str = StringUtils.replace(str, "\"", "");
        str = StringUtils.replace(str, ";", "");
        str = StringUtils.replace(str, "\\", "");

        //转换成小写
        str = str.toLowerCase();
        checkSqlInject(str);
        return str;
    }
    
    /**
     * 过滤特殊字符
     * @param str
     * @return
     */
    public static String filterSqlInject(String str){
        if(StringUtils.isBlank(str)){
            return null;
        }
        //去掉'|"|;|\字符
        str = StringUtils.replace(str, "'", "");
        str = StringUtils.replace(str, "\"", "");
        str = StringUtils.replace(str, ";", "");
        str = StringUtils.replace(str, "\\", "");
        //转换成小写
        str = str.toLowerCase();
        return str;
    }
    
    public  static void checkSqlInject (String str) {
    	 String[] keywords = {"master", "truncate", "insert", "select", "delete", "update", "declare", "alert", "drop", "union","exists"};
         //判断是否包含非法字符
         for(String keyword : keywords){
             if(str.indexOf(keyword) != -1){
             	throw new RRException("包含非法字符");
             }
         }
	}
}
