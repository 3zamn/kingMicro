package com.king.common.utils;

import java.util.HashMap;
import java.util.List;


/**
 * 实体解析
 * @author king chen
 *
 */
public  class EnttyMapperResolver {
	
	
	/**
	 * 根据实体、熟悉获取字段
	 * @param enttyName
	 * @param attribute
	 * @return
	 */
	public static String getColumn(String enttyName,String attribute){
		String column =null;
//		HashMap<String, List<HashMap<String,String>>> mapper=ApplicationInitializedListener.mapper;
		HashMap<String, List<HashMap<String,String>>> mapper=null;
		if(mapper!=null){
			 List<HashMap<String, String>> enttys=mapper.get(enttyName);
		        for(HashMap<String, String> entty:enttys){
		        	if(entty.get("property").equals(attribute)){
		        		column=entty.get("column");
		        	}
		        	
		        }
		}
		return column;
	}
	
	/**
	 * 实体是否存在该属性
	 * @param enttyName
	 * @param attribute
	 * @return
	 */
	public static Boolean isExistAttribute(String enttyName,String attribute){
		Boolean isExist =false;
//		HashMap<String, List<HashMap<String,String>>> mapper=ApplicationInitializedListener.mapper;
		HashMap<String, List<HashMap<String,String>>> mapper=null;
		if(mapper!=null){
			 List<HashMap<String, String>> enttys=mapper.get(enttyName);
			 if(enttys!=null){
				 for(HashMap<String, String> entty:enttys){
			        	if(entty.get("property").equals(attribute)){
			        		isExist =true;
			        	}        	
			        } 
			 }
		        
		}
		return isExist;
	}

}
