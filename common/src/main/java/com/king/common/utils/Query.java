package com.king.common.utils;
import com.king.common.utils.SQLFilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询参数
 * @author king chen
 *
 */
public class Query extends LinkedHashMap<String, Object> {
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
             			if(EnttyMapperResolver.isExistAttribute(enttyName, o.toString())){
             				atts.add(o.toString());				
             			}		
             		}
             		for(String attr:atts){
     					i=i+1;
             			String column = EnttyMapperResolver.getColumn(enttyName, attr);
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
			if(EnttyMapperResolver.isExistAttribute(enttyName, entry.getKey()))
				if(entry.getValue()!=null && !entry.getValue().toString().trim().equals(""))
					attributes.add(entry.getKey());
		}
		int j=0;
		for(String attribute:attributes){
				j=j+1;
 			String column = EnttyMapperResolver.getColumn(enttyName, attribute);
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

        //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String sidx = params.get("sidx").toString();
        String order = params.get("order").toString();
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
