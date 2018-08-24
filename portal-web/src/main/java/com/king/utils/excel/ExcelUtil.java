package com.king.utils.excel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.king.common.annotation.PropertyExt;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.date.DateToolkit;
import com.king.common.utils.pattern.StringToolkit;
import com.king.common.utils.spring.SpringContextUtils;
import com.king.utils.Query;

import io.swagger.annotations.ApiModelProperty;



/**
 * 高性能Excel导入导出--支持数百万级别大数据
 * 导入采用xml方式分段逐步解析避免大数据导致内存溢出
 * 导出同样采用分段分页list中开辟有限存储空间,用完了清空
 * 
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年8月3日
 * @param <T>
 */
public class ExcelUtil<T> {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public Class<T> clazz;

	public ExcelUtil(Class<T> clazz) {
		this.clazz = clazz;
	}

	/**
	 * 超大Excel数据导入--支持2007版及以上
	 * 采用xml方式解析Excel表数据
	 * @param input Excel文件流
	 * @param offset 从第几行开始/默认0
	 * @param map Excel列按顺序对应的Map<实体属性、默认值>
	 * @param method 数据保存业务方法
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void importExcel2007( int offset, MultipartFile file, LinkedHashMap<Field, Object> map,Class service ,Method method) {
		
		try {
			long startTime = System.currentTimeMillis();
			IExcelRowReader rowReader = new ExcelRowReader(clazz,service,method);			
			CommonsMultipartFile cf = (CommonsMultipartFile) file;
			DiskFileItem fi = (DiskFileItem) cf.getFileItem();
			File myfile = fi.getStoreLocation();
			ExcelReaderUtil.read2007Excel(offset,rowReader, myfile);
			long stopTime = System.currentTimeMillis();
			System.out.println("write xlsx file time: " + (stopTime - startTime) / 1000 + "m");
		} catch (Exception e) {
			logger.error("导入失败！"+e.getMessage());
		}
	
	}
	

	/**
	 * 导入Excel文件
	 * @param offset 从第几行开始导入
	 * @param file Excel文件
	 * @param map Excel列按顺序对应的Map<实体属性、默认值>
	 * @param service 业务接口
	 * @param method 数据保存业务方法
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JsonResponse importExcel(int offset, MultipartFile file, LinkedHashMap<Field, Object> map,Class service ,Method method) {
			
		try {
			if(!file.isEmpty()){
				if (file.getOriginalFilename().endsWith(".xlsx")) {
					importExcel2007(offset, file, map, service, method);

				}else if(file.getOriginalFilename().endsWith("xls")){
					 List<List<T>>  lists =importExcel2003(offset, file.getInputStream(),map);
					 for(List<T> list:lists){
						 method.invoke(SpringContextUtils.getBean(service), list);		
					 }
							
				}else{
					logger.error("请导入Excel文件！");
					return	JsonResponse.error("请导入Excel文件！");
					
				}
			}	
		} catch (Exception e) {
			logger.error("导入失败！"+e.getMessage());
			return	JsonResponse.error("导入失败！");			
		}
		return JsonResponse.success();
	
	}
	
	/**
	 * 导入Excel2003或小量数据
	 * @param offset //从第几行开始导入--默认0
	 * @param input
	 * @param map Excel列按顺序对应的Map<实体属性、默认值校验>
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public List<List<T>> importExcel2003(int offset, InputStream input, LinkedHashMap<Field, Object> map) throws Exception {
		
		int max=1000;//批量处理
		List<List<T>> lists= new ArrayList<List<T>>();
		List<T> list = new ArrayList<T>();
		try {
			Workbook workbook = WorkbookFactory.create(input);
			Sheet sheet = workbook.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();
			if (rows > 0) {
				Map<Integer, Field> fieldsMap = new HashMap<Integer, Field>(); 
				if(map.isEmpty()){//使用注解方式-属性顺序按实体默认
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
				}else{//自定义方式
					Iterator iter = map.entrySet().iterator(); 
					while (iter.hasNext()) { 
					Map.Entry entry = (Map.Entry) iter.next(); 
					Object key = entry.getKey(); 
					Object val = entry.getValue(); 
					} 
				}
				for (int i = offset; i < rows; i++) {
					Row row = sheet.getRow(i);
					int cellNum = sheet.getRow(0).getPhysicalNumberOfCells();
					T entity = null;
					for (int j = 0; j <= cellNum; j++) {
						Cell cell = row.getCell(j);
						if (cell == null) {
							continue;
						}
						String c = cell.getStringCellValue();
						if (c.equals("")) {
							continue;
						}
						entity = (entity == null ? clazz.newInstance() : entity);
						Field field = fieldsMap.get(j);
						if(field==null){
							continue;
						}
						Class<?> fieldType = field.getType();
						if (String.class == fieldType) {
							field.set(entity, String.valueOf(c));
						} else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
							field.set(entity, Integer.parseInt(c));
						} else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
							field.set(entity, Long.valueOf(c));
						} else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
							field.set(entity, Float.valueOf(c));
						} else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
							field.set(entity, Short.valueOf(c));
						} else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
							field.set(entity, Double.valueOf(c));
						} else if ((Boolean.TYPE == fieldType) || (Boolean.class == fieldType)) {
							if(c=="true" || c=="false"){
								field.set(entity, Boolean.valueOf(c));
							}else{
								if(Integer.parseInt(c)==1){
									field.set(entity, true);
								}
								if(Integer.parseInt(c)==0){
									field.set(entity, false);
								}
							}
						} else if (Character.TYPE == fieldType) {
							if ((c != null) && (c.length() > 0)) {
								field.set(entity, Character.valueOf(c.charAt(0)));
							}
						} else if (java.util.Date.class == fieldType) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							field.set(entity, sdf.parse(c));
						} else if (java.math.BigDecimal.class == fieldType) {
							c = cell.getStringCellValue();
							field.set(entity, new BigDecimal(c));
						}
					}
					if (entity != null) {
						list.add(entity);
					}
					
					if(list.size()>0 && list.size()%max==0){
						lists.add(list);
						list.clear();
					}
				}
				if(list.size()<max){
					lists.add(list);
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return lists;
	}


	/**
	 * 数百万级数据导出Excel
	 * @param title Excel标题
	 * @param fileName 文件名称
	 * @param service 业务接口
	 * @param method 查询方法
	 * @param query 查询方法参数
	 */
	@SuppressWarnings("rawtypes")
	public void exportExcel(String title, String fileName,Class service ,Method method,Query query,HttpServletResponse response) {
		OutputStream os = null;
		int rowNo = 0;//总行号
		int pageRowNo=0;//当前sheet行号
		int maxRowSheet=1000000;//每个sheet最大行
		long startTime = System.currentTimeMillis();
		try {
			response.setContentType("application/force-download");
			String file_name = new String((fileName+".xlsx").getBytes(), "ISO-8859-1");
			response.setHeader("Content-Disposition", "attachment;fileName="+"\""+file_name+"\"");
			os = response.getOutputStream();
			os = response.getOutputStream();
			SXSSFWorkbook wb = new SXSSFWorkbook(1000);// 内存中保留 1000 条数据，以免内存溢出，其余写入 硬盘
			Sheet sheet1 = wb.createSheet("sheet1");
			Row titleRow = (Row) sheet1.createRow(pageRowNo++);
			rowNo++;
			Field[] fields = clazz.getDeclaredFields();
			LinkedHashMap<Field, String> columnList = new LinkedHashMap<>();
			for (Field field : fields) {
				field.setAccessible(true);
				PropertyExt ext = field.getAnnotation(PropertyExt.class);
				if (ext!=null &&ext.isExport()) {
					String fieldName = field.getAnnotation(ApiModelProperty.class).value();
					columnList.put(field, fieldName);
				}
			}
			
			int i=0;
			Iterator iter = columnList.entrySet().iterator(); 
			while (iter.hasNext()) { 
			Map.Entry entry = (Map.Entry) iter.next(); 
			Cell cell = titleRow.createCell(i++);		
			cell.setCellValue(StringToolkit.getObjectString(entry.getValue()));
			} 
			Sheet sheet = null;	
			List<T> lists= hande(query, service, method);
			for(T t : lists){
				if(rowNo%maxRowSheet==0){
					sheet = wb.createSheet("sheet"+(rowNo/maxRowSheet)+1);//建立新的sheet对象
					sheet = wb.getSheetAt(rowNo/maxRowSheet);		//动态指定当前的工作表
					pageRowNo = 0;//每当新建了工作表就将当前工作表的行号重置为0
				}else if(rowNo<maxRowSheet){
					sheet=sheet1;
				}
				rowNo++;
				Row contentRow = (Row) sheet.createRow(pageRowNo++);  
				int j=0;
				Iterator iterator = columnList.entrySet().iterator(); 
				while (iterator.hasNext()) { 
				Map.Entry entry = (Map.Entry) iterator.next(); 
				Cell cell = contentRow.createCell(j++);
				Field f = (Field)entry.getKey();
				Class<?> fieldType = f.getType();
				Object obj=f.get(t);
				if (String.class == fieldType) {
					cell.setCellValue(String.valueOf(f.get(t)));					
				} else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
					cell.setCellValue(String.valueOf(f.get(t)));
				} else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
					cell.setCellValue(String.valueOf(obj));
				} else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
					cell.setCellValue(Float.valueOf(String.valueOf(obj)));
				} else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
					cell.setCellValue(Short.valueOf(String.valueOf(obj)));
				} else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
					cell.setCellValue(Double.valueOf(String.valueOf(obj)));
				} else if ((Boolean.TYPE == fieldType) || (Boolean.class == fieldType)) {
					String c=String.valueOf(obj);
					if(c=="true" || c=="false"){
						cell.setCellValue(Boolean.valueOf(String.valueOf(obj)));
					}else{
						if(Integer.parseInt(c)==1){
							cell.setCellValue(true);
						}
						if(Integer.parseInt(c)==0){
							cell.setCellValue(false);
						}
					}
				} else if (Character.TYPE == fieldType) {
					String c=String.valueOf(obj);
					if ((c != null) && (c.length() > 0)) {
						cell.setCellValue(Character.valueOf(c.charAt(0)));
					}
				} else if (java.util.Date.class == fieldType) {
					String c=String.valueOf(f.get(t));			
					cell.setCellValue(DateToolkit.simpleDateFormat(c));
				} else if (java.math.BigDecimal.class == fieldType) {
					cell.setCellValue(new BigDecimal(String.valueOf(obj)).toString());
				}
				} 
			}			
			wb.write(os);
			wb.close();
			lists.clear();
			long finishedTime = System.currentTimeMillis();
			logger.info("finished execute  time: " + (finishedTime - startTime)/1000 + "m"+" ;row:"+rowNo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} // 关闭输
		}
	}
	
	
	/**
	 * 查询要导出的数据
	 * @param queryMap 查询条件
	 * @param service 业务接口
	 * @param method 查询方法
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> hande(Query query,Class service ,Method method){
		
		List<T> list=null;
		 try {
			list = (List<T>)method.invoke(SpringContextUtils.getBean(service), query);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return list;
	}
}