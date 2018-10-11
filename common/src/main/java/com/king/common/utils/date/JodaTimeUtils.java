package com.king.common.utils.date;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年4月20日
 */
public class JodaTimeUtils {

	
	/**
	 * 获取一年中所有周六日日期
	 * @param year
	 * @return List 一年所有周六日集合，日期格式：yyyy-MM-dd
	 */
	public static List<String> getWeekendDays(int year) {
		List<String> results = new ArrayList<String>();
		DateTime starTime = new DateTime().withYear(year).withDayOfYear(1);
		DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd");  
		while (!(starTime.getYear()>year)) {
			if (starTime.getDayOfWeek()>5) {
				String date = starTime.toString(format);
				results.add(date);
			}
			starTime = starTime.plusDays(1);
		}
		return results;
	}
	
	
	/**
	 * 获取一年中所有的日期
	 * @param year
	 * @return List 一年中所有的日期集合，日期格式：yyyy-mm-dd
	 */
	public static List<String> getYearDays(int year) {
		List<String> results = new ArrayList<String>();
		DateTime starTime = new DateTime().withYear(year).withDayOfYear(1);
		DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd");  
		while (!(starTime.getYear()>year)) {
			if (starTime.getDayOfWeek()>=1) {
				String date = starTime.toString(format);
				results.add(date);
			}
			starTime = starTime.plusDays(1);
		}
		return results;
	}

	
	/**
	 * 获取当天是星期几，返回中文星期几，如：星期日
	 * @return String 中文星期几
	 */
	public static String getNowDayWeekendName() {
		String weekname = "";
		DateTime dt = new DateTime();  
		//星期  
		switch(dt.getDayOfWeek()) {  
		case DateTimeConstants.SUNDAY:  
			weekname ="星期日";  
		    break;  
		case DateTimeConstants.MONDAY:
			weekname ="星期一";   
		    break;  
		case DateTimeConstants.TUESDAY:  
			weekname ="星期二";    
		    break;  
		case DateTimeConstants.WEDNESDAY:  
			weekname ="星期三";
		    break;  
		case DateTimeConstants.THURSDAY:
			weekname ="星期四";
		    break;  
		case DateTimeConstants.FRIDAY: 
			weekname ="星期五";
		    break;  
		case DateTimeConstants.SATURDAY:  
			weekname ="星期六"; 
		    break;  
		} 
		return weekname;
	}
	
	
	/**
	 * 获取指定日期是星期几，返回中文星期几，如：星期日
	 * @param day 指定日期，格式：yyyy-mm-dd
	 * @return String 中文星期几
	 */
	public static String getDesignatedDayWeekName(String day) {
		String weekname = "";
		DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd");
		DateTime dt = format.parseDateTime(day); 
		//星期  
		switch(dt.getDayOfWeek()) {  
		case DateTimeConstants.SUNDAY:  
			weekname ="星期日";  
		    break;  
		case DateTimeConstants.MONDAY:
			weekname ="星期一";   
		    break;  
		case DateTimeConstants.TUESDAY:  
			weekname ="星期二";    
		    break;  
		case DateTimeConstants.WEDNESDAY:  
			weekname ="星期三";
		    break;  
		case DateTimeConstants.THURSDAY:
			weekname ="星期四";
		    break;  
		case DateTimeConstants.FRIDAY: 
			weekname ="星期五";
		    break;  
		case DateTimeConstants.SATURDAY:  
			weekname ="星期六"; 
		    break;  
		} 
		return weekname;
	}
	
	
	
	/**
	 * 获取指定日期是星期几，返回英文星期几（字母小写），如：SUNDAY
	 * @param day 指定日期，格式：yyyy-mm-dd
	 * @return String 英文星期几
	 */
	public static String getDesignatedDayWeekCode(String day) {
		String weekname = "";
		DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd");
		DateTime dt = format.parseDateTime(day); 
		//星期  
		switch(dt.getDayOfWeek()) {  
		case DateTimeConstants.SUNDAY:  
			weekname ="sunday";  
		    break;  
		case DateTimeConstants.MONDAY:
			weekname ="monday";   
		    break;  
		case DateTimeConstants.TUESDAY:  
			weekname ="tuesday";    
		    break;  
		case DateTimeConstants.WEDNESDAY:  
			weekname ="wednesday";
		    break;  
		case DateTimeConstants.THURSDAY:
			weekname ="thursday";
		    break;  
		case DateTimeConstants.FRIDAY: 
			weekname ="friday";
		    break;  
		case DateTimeConstants.SATURDAY:  
			weekname ="saturday"; 
		    break;  
		} 
		return weekname;
	}
	
	public static void main(String[] args){
		List<String> name = JodaTimeUtils.getYearDays(2015);
		for (String str : name) {
			System.out.println(str);
		}
		
	}
	
}
