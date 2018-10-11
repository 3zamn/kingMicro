package com.king.common.utils.entityMapper;  
  
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;  
  
/**
 * 表与实体映射解析--存本地map
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年1月11日
 */
public class GenEntityMapperMap implements EntityResolver{  
	 @Override
	 public InputSource resolveEntity(String publicId, String systemId)
	   throws SAXException, IOException {
	        return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
	 }
    /** 
     * 根据类的属性名找表的列名（取一个的时候可以使用此方法） 
     * @param fileName 类对应的Mapper xml文件 
     * @param id  唯一的id 
     * <p> 
     * 如：resultMap id="BaseResultMap" type="com.king.dal.gen.model.smp.SysUser" 中的id 
     * </p> 
     * @param property 属性名（对应的Java对象属性名） 
     * @return 
     */  
    public static String getMapperColumnByProperty(String fileName, String id, String property){  
        try {  
            SAXReader saxReader = new SAXReader();    
            saxReader.setEntityResolver(new GenEntityMapper());
            Document document = saxReader.read(GenEntityMapper.class.getClassLoader().getResourceAsStream(fileName));    
            if(document != null){  
                Element root = document.getRootElement();  
                if(root != null){  
                    @SuppressWarnings("unchecked")  
                    List<Element> resultMaps = root.elements("resultMap");  
                    for (Element resultMap : resultMaps) {  
                        if(resultMap != null && resultMap.attributeValue("id").equals(id)){  
                            @SuppressWarnings("unchecked")  
                            List<Element> properties = resultMap.elements();  
                            for (Element prop : properties) {  
                                if(prop != null && prop.attributeValue("property").equals(property)){  
                                    return prop.attributeValue("column");  
                                }  
                            }  
                        }  
                    }  
                }    
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
      
    /** 
     * 返回ResultMap对应Element对象（取2次以上的时候，建议先把Element对象找到，再根据此Element对象再去找column，效率高很多） 
     * @param fileName 类对应的Mapper xml文件 
     * @param id  唯一的id 
     * <p> 
     * 如：resultMap id="BaseResultMap" type="com.king.dal.gen.model.smp.SysUser" 中的id 
     * </p> 
     * @return 
     */  
    public   Element getResultMapElement(String fileName, String id){  
        try {  
            SAXReader saxReader = new SAXReader();    
            saxReader.setEntityResolver(new GenEntityMapper());//去掉dtd检验,要不然卡爆了、还可能网络连接超时。因为联网下载关联的dtd
            Document document = saxReader.read(GenEntityMapperMap.this.getClass().getResourceAsStream(fileName));    
            if(document != null){  
                Element root = document.getRootElement();  
                if(root != null){  
                    @SuppressWarnings("unchecked")  
                    List<Element> resultMaps = root.elements("resultMap");  
                    for (Element resultMap : resultMaps) {  
                        if(resultMap != null && resultMap.attributeValue("id").equals(id)){  
                            return resultMap;  
                        }  
                    }  
                }    
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
      
    /** 
     * 在Element根据property找表的列名（和方法getResultMapElement()结合使用，多次取Column时效率高出很多倍） 
     * @param resultMapElement Mapper xml文件解析后得到的Element对象（方法：getResultMapElement()） 
     * @param property 属性名（对应的Java对象属性名） 
     * @return 
     */  
    public static String getMapperColumnByElement(Element resultMapElement, String property){  
        try {  
            if(resultMapElement != null){  
                @SuppressWarnings("unchecked")  
                List<Element> properties = resultMapElement.elements();  
                for (Element prop : properties) {  
                    if(prop != null && prop.attributeValue("property").equals(property)){  
                        return prop.attributeValue("column");  
                    }  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
    
    public static List<HashMap<String, String>> getMapperResultByElement(Element resultMapElement ,String entity){  
    	List<HashMap<String, String>> entityMap = new ArrayList<HashMap<String, String>>();
        try {  	   
            if(resultMapElement != null){  
                @SuppressWarnings("unchecked")  
                List<Element> properties = resultMapElement.elements();       
                for (Element prop : properties) {  
                	if(prop !=null){
                		HashMap<String, String> result = new HashMap<String, String>();
                    	result.put("column", prop.attributeValue("column"));
                    	result.put("property", prop.attributeValue("property"));
                    	result.put("jdbcType", prop.attributeValue("jdbcType"));
                    	entityMap.add(result);
                	}
                } 
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		return entityMap;  
    }  
    static final List<String> files = new ArrayList<String>();
	public static List<String>  find(String path, String reg) {
		Pattern pat = Pattern.compile(reg);
		File file = new File(path);
		File[] arr = file.listFiles();
		for (int i = 0; i < arr.length; i++) {
			// 判断是否是文件夹，如果是的话，再调用一下find方法
			if (arr[i].isDirectory()) {
				find(arr[i].getAbsolutePath(), reg);
			}
			Matcher mat = pat.matcher(arr[i].getAbsolutePath());
			// 根据正则表达式，寻找匹配的文件
			if (mat.matches()) {
				// 这个getAbsolutePath()方法返回一个String的文件绝对路径
				String filename= arr[i].getName();
			//	System.out.println(filename);
				files.add(arr[i].getAbsolutePath());
		//		System.out.println(arr[i].getAbsolutePath());
			}
		}
		return files;
	}
      
	/**
	 * 生成实体、表字段的映射map
	 * @return
	 */
	/*public static HashMap<String, List<HashMap<String,String>>> generateEnttyMapper(){
		 GenEntityMapper aa = new GenEntityMapper();           
	        List<String> filenames=find(GenEntityMapper.class.getClassLoader().getResource("mapper").getPath(), "\\S+\\.xml");
	        //放在redis中会好效率一些，太多层了。
	        HashMap<String, List<HashMap<String,String>>> map = new HashMap<String, List<HashMap<String,String>>>();
	        for(String filename:filenames){
	        	if (filename!=null && filename!="") {
	        		String entity =filename.substring(filename.lastIndexOf("\\")+1, filename.length()).replace("Dao.xml", "");
	        		filename=filename.replace("\\","/");
	        		String str ="/mapper";
	        		String relePath = filename.substring(filename.indexOf(str), filename.length());
	        		Element e = aa.getResultMapElement(relePath,"BaseResultMap");     		
	        		map.put(entity, getMapperResultByElement(e, entity));
				}	
	        }
	        return map;
	}*/
	
    public static void main(String[] args) {  
    	  long startTime = new Date().getTime();  
    	  
    	  GenEntityMapper aa = new GenEntityMapper();           
        List<String> filenames=find("src/main/resources/mapper", "\\S+\\.xml");
        //放在redis中会好效率一些，太多层了。
        HashMap<String, List<HashMap<String,String>>> map = new HashMap<String, List<HashMap<String,String>>>();
        /*for(String filename:filenames){
        	if (filename!=null && filename!="") {
        		String entity =filename.substring(filename.lastIndexOf("\\")+1, filename.length()).replace("Dao.xml", "");
        		filename=filename.replace("\\","/");
        		String str ="/mapper";
        		String relePath = filename.substring(filename.indexOf(str), filename.length());
        		Element e = aa.getResultMapElement(relePath,"BaseResultMap");     		
        		map.put(entity, getMapperResultByElement(e, entity));
			}
   	
        }*/
        System.out.println(map);
        List<HashMap<String, String>> ss=map.get("Dept");
        if(ss!=null){
        	for(HashMap<String, String> a:ss){
            	if(a.get("property").equals("orderNum")){
            		System.out.println(a.get("column"));
            	}
            	
            }
        }

        long endTime = new Date().getTime();  
        System.out.println("所用的时间间隔是："+ (endTime-startTime));  
          
    }  
      
      
}  