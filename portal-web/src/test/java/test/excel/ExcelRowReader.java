package test.excel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.king.common.annotation.PropertyExt;
import com.king.common.utils.spring.SpringContextUtils;

public class ExcelRowReader<T> implements IExcelRowReader {
	

	private Class<T> clazz;
	private Class service;
	private Method method;

   @SuppressWarnings("rawtypes")
public ExcelRowReader(Class<T> clazz, Class service, Method method) {
	   this.clazz=clazz;
	   this.service=service;
	   this.method=method;
		// TODO Auto-generated constructor stub
	}

private	List<T> list = new ArrayList<>();
	private final int max=1000;
	@SuppressWarnings("unchecked")
	@Override
    public void getRows(int sheetIndex, int curRow, List<String> rowlist) {

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
					  field.set(entity, rowlist.get(i));
			            System.out.print(rowlist.get(i)==""?" * ":rowlist.get(i) + " ");  
			            System.out.println();  
			        } 
				  list.add(entity);
				  if(list.size()>max){
					  method.invoke(SpringContextUtils.getBean(service), list);		
				  }
				  
			}        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    }
    
}