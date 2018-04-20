package com.king.common.utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.king.common.utils.entityMapper.EntityMapperResolver;
import com.king.common.utils.pattern.SQLFilter;
import com.king.common.utils.security.ShiroUtils;
import com.king.common.utils.spring.SpringContextUtils;

/**
 *  查询参数
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年2月27日
 */

public class Query extends LinkedHashMap<String, Object> {
/*	@Autowired
	private EnttyMapperResolver enttyMapperResolver;*/
	private static final long serialVersionUID = 1L;
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
        	 this.page = Integer.parseInt(params.get("page").toString());
             this.limit = Integer.parseInt(params.get("limit").toString());
             this.put("offset", (page - 1) * limit);
             this.put("page", page);
             this.put("limit", limit);
             //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
             String sidx = params.get("sidx").toString();
             String order = params.get("order").toString();
             this.put("sidx", SQLFilter.sqlInject(sidx));
             this.put("order", SQLFilter.sqlInject(order));
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
                 				likeSql +=column +" like"+" '%"+params.get("searchKey")+"%'" +" or ";
                 			}else {
                 				likeSql +=column +" like"+" '%"+params.get("searchKey")+"%'";
         					}			
         				}
             		}
             	}
             }
        }
        //多列查询
        List<String> attributes = new ArrayList<String>();
		Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			if((SpringContextUtils.getBean("enttyMapperResolver",EntityMapperResolver.class)).isExistAttribute(enttyName, entry.getKey()))
				if(entry.getValue()!=null && !entry.getValue().toString().trim().equals(""))
					attributes.add(entry.getKey());
		}
		int j=0;
		for(String attribute:attributes){
				j=j+1;
				JSONObject json = (SpringContextUtils.getBean("enttyMapperResolver",EntityMapperResolver.class)).getColumn(enttyName, attribute);
				String column = json.getString("column");
				if(column!=null && column!=""){   	
     			if(j<attributes.size()){
     				mutlSql +=column +" = "+"'"+params.get(attribute)+"'" +" and ";
     			}else {
     				mutlSql +=column +" = "+"'"+params.get(attribute)+"'";
					}			
				}
 		}
		if(!likeSql.toString().trim().equals("") && !mutlSql.toString().trim().equals("")){
			searchSql =likeSql+" and "+ "("+mutlSql+")";
		}else{
			searchSql =likeSql.toString() + mutlSql.toString();
		}
        this.put("searchSql", searchSql);
        //分页参数
        this.page = Integer.parseInt(params.get("page").toString());
        this.limit = Integer.parseInt(params.get("limit").toString());
        this.put("offset", (page - 1) * limit);
        this.put("page", page);
        this.put("limit", limit);
        if(SpringContextUtils.getBean("shiroFilter")!=null){
            this.put("user", ShiroUtils.getUserEntity());//用户
        }
        //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String sidx = params.get("sidx").toString();
        String order = params.get("order").toString();
         sidx = (SpringContextUtils.getBean("enttyMapperResolver",EntityMapperResolver.class)).getColumn(enttyName, sidx).getString("column");
        this.put("sidx", SQLFilter.sqlInject(sidx));
        this.put("order", SQLFilter.sqlInject(order));
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
