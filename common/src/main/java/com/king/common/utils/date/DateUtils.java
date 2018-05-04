package com.king.common.utils.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 日期处理
 * @author King chen
 * @date 2017年12月25日
 */
public class DateUtils {
	/** 时间格式(yyyy-MM-dd) */
	public final static String DATE_PATTERN = "yyyy-MM-dd";
	/** 时间格式(yyyy-MM-dd HH:mm:ss) */
	public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    public static String format(Date date, String pattern) {
        if(date != null){
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }
    
    
    /**
	 * 获取当前时间的字符形式
	 * @param format
	 * @return
	 */
	public static String getCurrentDateStr(String format) {
		SimpleDateFormat dateFormat=new SimpleDateFormat(format);
		return dateFormat.format(new Date());
	}
	
	/**
	 * 获取默认的当前日期字符串
	 * @return
	 */
	public static String getDefaultDateStr() {
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(new Date());
	}
	
	/**
	 * 获取默认的当前日期与时间字符串
	 * @return
	 */
	public static String getDefaultDateTimeStr() {
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return dateFormat.format(new Date());
	}
	
	
	/**
	 * 解析日期
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static Date parse(String dateStr,String pattern) {
		SimpleDateFormat dateFormat=new SimpleDateFormat(pattern);
		try {
			return dateFormat.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException("解析日期出错! dateStr="+dateStr,e);
		}
	}
	
	/**
	 * 获取指定月份的最后一天
	 * @param year		年份
	 * @param month		月份,从1开始
	 * @return
	 */
	public static int getLastDayInMonth(int year,int month) {
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.YEAR, year);
		return cal.getActualMaximum(Calendar.DATE);
	}
	
	/**
	 * 两个时间相差多少天、小时、分钟
	 * @param formdate
	 * @param todate
	 * @return Map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map daysBetween(Date formdate,Date todate) {    
        Map map = new HashMap();
        long time1 = formdate.getTime();
        long time2 = todate.getTime();
        long diff = time2-time1;//这样得到的差值是微秒级别  
        long days=diff/(1000*3600*24);
        long hours=(diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);  
        long minutes=(diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);  
        map.put("days", days);
        map.put("hours", hours);
        map.put("minutes", minutes);
       return map;
    }
	
	 /**
	  * 几天后是日期
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date getDateAfter(Date d,int day){  
		   Calendar now =Calendar.getInstance();  
		   now.setTime(d);  
		   now.set(Calendar.DATE,now.get(Calendar.DATE)+day);  
		   return now.getTime();  
	}  
}
