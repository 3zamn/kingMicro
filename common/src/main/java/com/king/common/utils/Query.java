package com.king.common.utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.king.common.utils.entityMapper.EntityMapperResolver;
import com.king.common.utils.pattern.SQLFilter;
import com.king.common.utils.pattern.StringToolkit;
import com.king.common.utils.security.ShiroUtils;
import com.king.common.utils.spring.SpringContextUtils;

/**
 *  查询参数
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年2月27日
 */

public class Query extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = 6414037103985495948L;
	
	//当前页码
    private int page;
    //每页条数
    private int limit;

    /**
     * 分页列表
     * @param params
     */
    public Query(Map<String, Object> params){
        this.putAll(params);     
        //分页参数
        if(!params.isEmpty()){
        	try {
        		 this.page = Integer.parseInt(StringToolkit.getObjectString(params.get("page")));
                 this.limit = Integer.parseInt(StringToolkit.getObjectString(params.get("limit")));
                 this.put("offset", (page - 1) * limit);
                 this.put("page", page);
                 this.put("limit", limit>200?200:limit);//分页过载保护、最大每页200
               //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）          
                 String order = StringToolkit.getObjectString(params.get("order"));
                 if(!order.trim().equalsIgnoreCase("desc") && !order.trim().equalsIgnoreCase("asc")){
                   	 order="";
                    }
                 this.put("order", order);
                 String sidx = StringToolkit.getObjectString(params.get("sidx"));
                 this.put("sidx", SQLFilter.sqlInject(sidx));         
			} catch (Exception e) {
				 this.put("offset", null);
				 this.put("page", null);
		         this.put("limit", null);
				// TODO: handle exception
			}     
        } 
        if(SpringContextUtils.getBean("shiroFilter")!=null){
            this.put("user", ShiroUtils.getUserEntity());//用户
        }
    }
    
    /**
     * 分页列表模糊、多列查询
     * @param params
     * @param enttyName
     */
    public Query(Map<String, Object> params,String enttyName){
        this.putAll(params);
        Object mutlSql ="";//多列查询
        Object likeSql ="";//模糊查询
        Object betweenSql ="";//范围查询
        Object searchSql ="";
        //多字段模糊查询
        if(params.get("keyParam")!=null && params.get("searchKey")!=null){
        	 String[] keyParam =params.get("keyParam").toString().replace("[","").replace("]", "").replace("\"", "").split(",");
             if(keyParam!=null && !params.get("searchKey").toString().trim().isEmpty()){
             	if(keyParam.length>0){
             		int i=0;
             		List<String> atts = new ArrayList<String>();
             		for(Object o:keyParam){
             			if((SpringContextUtils.getBean("enttyMapperResolver",EntityMapperResolver.class)).isExistAttribute(enttyName, o.toString())){
             				atts.add(o.toString());				
             			}		
             		}
             		for(String attr:atts){
     					i=i+1;
             			JSONObject json = (SpringContextUtils.getBean("enttyMapperResolver",EntityMapperResolver.class)).getColumn(enttyName, attr);
             			String column = json.getString("column"); 
         				if(column!=null && column!=""){   	
                 			if(i<atts.size()){
                 				likeSql +=column +" like"+" '%"+SQLFilter.filterSqlInject(params.get("searchKey").toString().trim())+"%'" +" or ";
                 			}else {
                 				likeSql +=column +" like"+" '%"+SQLFilter.filterSqlInject(params.get("searchKey").toString().trim())+"%'";
         					}			
         				}
             		}
             	}
             }
        }
        //多列查询
        List<String> attributes = new ArrayList<String>();
        List<String> between_ttr = new ArrayList<String>();//范围查询
        List<String> equal_ttr = new ArrayList<String>();//精确查询
		Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			if((SpringContextUtils.getBean("enttyMapperResolver",EntityMapperResolver.class)).isExistAttribute(enttyName, entry.getKey()))
				if(entry.getValue()!=null && !entry.getValue().toString().trim().equals(""))
					attributes.add(entry.getKey());
		}	
		for(String attribute:attributes){
			JSONObject json = (SpringContextUtils.getBean("enttyMapperResolver",EntityMapperResolver.class)).getColumn(enttyName, attribute);
			String column = json.getString("column");
			if(column!=null && column!=""){   	
				Object strobj = params.get(attribute);
				Boolean isJson =false;
				try {
					JSONObject.parseObject(strobj.toString());
					isJson=true;
				} catch (Exception e) {
					// TODO: handle exception
					isJson=false;
				}
				if(isJson){
					between_ttr.add(attribute);
				}else{
					equal_ttr.add(attribute);
				}
			}
					
 		}
		int j=0;
		for(String attribute:between_ttr){//范围查询
			j=j+1;		
			JSONObject json = (SpringContextUtils.getBean("enttyMapperResolver",EntityMapperResolver.class)).getColumn(enttyName, attribute);
			String column = json.getString("column");
			Object strobj = params.get(attribute);
			JSONObject jsonObject = JSONObject.parseObject(strobj.toString());
			String begin =jsonObject.getString("begin");
			String end =jsonObject.getString("end");
			if(StringUtils.isNotBlank(begin) && StringUtils.isNotBlank(end)){
				if(j<between_ttr.size()){
					betweenSql += column + " between  " +"'"+SQLFilter.filterSqlInject(begin)+"'" +"  and  " +"'"+SQLFilter.filterSqlInject(end)+"'" +" and ";
	 			}else {
	 				betweenSql += column + " between  " +"'"+SQLFilter.filterSqlInject(begin)+"'" +"  and  " +"'"+ SQLFilter.filterSqlInject(end)+"'" ;
				}
			}		
		}
		int i=0;
		for(String attribute:equal_ttr){//精确查询
			i=i+1;		
			JSONObject json = (SpringContextUtils.getBean("enttyMapperResolver",EntityMapperResolver.class)).getColumn(enttyName, attribute);
			String column = json.getString("column");
			String value =params.get(attribute).toString().trim();
			if(i<equal_ttr.size()){
 				mutlSql +=column +" = "+"'"+SQLFilter.filterSqlInject(value)+"'" +" and ";
 			}else {
 				mutlSql +=column +" = "+"'"+SQLFilter.filterSqlInject(value)+"'";
			}
		}
		StringBuffer like = new StringBuffer();
		StringBuffer equal = new StringBuffer();
		StringBuffer mutl = new StringBuffer();
		if(StringUtils.isNotBlank(likeSql.toString())){
			like.append("(").append(likeSql).append(")");
		}
		if(StringUtils.isNotBlank(mutlSql.toString())){
			equal.append("(").append(mutlSql).append(")");
		}
		if(StringUtils.isNotBlank(betweenSql.toString())){
			mutl.append("(").append(betweenSql).append(")");
		}
		//组合查询语句
		if(StringUtils.isBlank(like.toString())){
			if(StringUtils.isNotBlank(equal.toString()) && StringUtils.isNotBlank(mutl.toString())){
				searchSql =  equal +" and "+ mutl;
			}else if(StringUtils.isBlank(equal.toString()) && StringUtils.isNotBlank(mutl.toString())){
				searchSql =  mutl;
			}else if(StringUtils.isNotBlank(equal.toString()) && StringUtils.isBlank(mutl.toString())){
				searchSql =  equal;
			}
		}else{
			if(StringUtils.isNotBlank(equal.toString()) && StringUtils.isNotBlank(mutl.toString())){
				searchSql = like +" and "+ equal +" and "+ mutl;
			}else if(StringUtils.isBlank(equal.toString()) && StringUtils.isNotBlank(mutl.toString())){
				searchSql = like +" and "+  mutl;
			}else if(StringUtils.isNotBlank(equal.toString()) && StringUtils.isBlank(mutl.toString())){
				searchSql = like +" and "+  equal;
			}else{
				searchSql = like;
			}
		}
        this.put("searchSql", searchSql.toString());
       
        //分页参数
        try {
        	this.page = Integer.parseInt(StringToolkit.getObjectString(params.get("page")));
            this.limit = Integer.parseInt(StringToolkit.getObjectString(params.get("limit")));
            this.put("offset", (page - 1) * limit);
            this.put("page", page);
            this.put("limit", limit>200?200:limit);//分页过载保护、最大每页200
          //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
            String sidx = StringToolkit.getObjectString(params.get("sidx"));
            String order = StringToolkit.getObjectString(params.get("order"));
             sidx = (SpringContextUtils.getBean("enttyMapperResolver",EntityMapperResolver.class)).getColumn(enttyName, sidx).getString("column");
            this.put("sidx", SQLFilter.sqlInject(sidx));
            if(!order.trim().equalsIgnoreCase("desc") && !order.trim().equalsIgnoreCase("asc")){
           	 order="";
            }
            this.put("order", order);
		} catch (Exception e) {
			 this.put("offset", null);
			 this.put("page", null);
	         this.put("limit", null);
			// TODO: handle exception
		}      
        if(SpringContextUtils.getBean("shiroFilter")!=null){
            this.put("user", ShiroUtils.getUserEntity());//用户
        }
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    
}
