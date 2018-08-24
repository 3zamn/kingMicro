package com.king.utils.excel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.king.common.annotation.PropertyExt;
import com.king.common.utils.spring.SpringContextUtils;

/**
 *  Excel读取业务处理
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年8月23日
 * @param <T>
 */
public class ExcelRowReader<T> implements IExcelRowReader {
	

	private Class<T> clazz;
	private Class service;
	private Method method;
	private	List<T> list = new ArrayList<>();
	private final int max=1000;
	int count =0;
	
   @SuppressWarnings("rawtypes")
public ExcelRowReader(Class<T> clazz, Class service, Method method) {
	   this.clazz=clazz;
	   this.service=service;
	   this.method=method;
		// TODO Auto-generated constructor stub
	}


	@SuppressWarnings("unchecked")
	@Override
    public void getRows(int sheetIndex, int curRow,Boolean end, List<String> rowlist) {

		T entity = null;
		try {
			if(clazz!=null){
				entity = (entity == null ? clazz.newInstance() : entity);
				Map<Integer, Field> fieldsMap = new HashMap<Integer, Field>(); 
				Field[] allFields = clazz.getDeclaredFields(); 				
				int col=0;
				for (Field field:allFields) {			
					PropertyExt ext=field.getAnnotation(PropertyExt.class);
					 if (ext!=null && ext.isExport()) {
					 field.setAccessible(true);
					 fieldsMap.put(col, field); 
					 col=col+1;
					 }				 
				}				
				  for (int i = 0; i < rowlist.size(); i++) {  
					  Field field=fieldsMap.get(i);
					  if(field==null){
							continue;
						}
						Class<?> fieldType = field.getType();
						if (String.class == fieldType) {
							field.set(entity, String.valueOf(rowlist.get(i)));
						} else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
							field.set(entity, Integer.parseInt(rowlist.get(i)));
						} else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
							field.set(entity, Long.valueOf(rowlist.get(i)));
						} else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
							field.set(entity, Float.valueOf(rowlist.get(i)));
						} else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
							field.set(entity, Short.valueOf(rowlist.get(i)));
						} else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
							field.set(entity, Double.valueOf(rowlist.get(i)));
						} else if ((Boolean.TYPE == fieldType) || (Boolean.class == fieldType)) {
							if(rowlist.get(i)=="true" || rowlist.get(i)=="false"){
								field.set(entity, Boolean.valueOf(rowlist.get(i)));
							}else{
								if(Integer.parseInt(rowlist.get(i))==1){
									field.set(entity, true);
								}
								if(Integer.parseInt(rowlist.get(i))==0){
									field.set(entity, false);
								}
							}
						} else if (Character.TYPE == fieldType) {
							if ((rowlist.get(i) != null) && (rowlist.get(i).length() > 0)) {
								field.set(entity, Character.valueOf(rowlist.get(i).charAt(0)));
							}
						} else if (java.util.Date.class == fieldType) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							field.set(entity, sdf.parse(rowlist.get(i)));
						} else if (java.math.BigDecimal.class == fieldType) {
							field.set(entity, new BigDecimal(rowlist.get(i)));
						}						
			        } 
				  if(!rowlist.isEmpty()){
					  list.add(entity);
				  }				 
				  if(list.size()==max){
					  method.invoke(SpringContextUtils.getBean(service), list);
					  list.clear();
					//  System.out.println(count=count+1);
				  }
				  if(end && !list.isEmpty()){
					  method.invoke(SpringContextUtils.getBean(service), list);
					  list.clear();
					  System.out.println("第"+sheetIndex+"worksheet"+"结束");
				  }
				  
			}        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    }
    
}