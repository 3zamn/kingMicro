package com.king.common.utils.entityMapper;  
  
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;  
  
/**
 * 表与实体映射解析
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年1月11日
 */
@Component
public class GenEntityMapper implements EntityResolver{  

	@Autowired
	private EntityMapperRedis enttyMapperRedis;
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
  /*  public static String getMapperColumnByProperty(String fileName, String id, String property){  
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
    }  */
      
    /** 
     * 返回ResultMap对应Element对象（取2次以上的时候，建议先把Element对象找到，再根据此Element对象再去找column，效率高很多） 
     * @param fileName 类对应的Mapper xml文件 
     * @param id  唯一的id 
     * <p> 
     * 如：resultMap id="BaseResultMap" type="com.king.dal.gen.model.smp.SysUser" 中的id 
     * </p> 
     * @return 
     */  
    public   Element getResultMapElement(String fileName, String id,Boolean jar){  
        try {  
            SAXReader saxReader = new SAXReader();    
            saxReader.setEntityResolver(new GenEntityMapper());//去掉dtd检验,要不然卡爆了、还可能网络连接超时。因为联网下载关联的dtd
            Document document = null;
            System.out.println(fileName);
            if(jar){
            	 document =saxReader.read(GenEntityMapper.class.getClassLoader().getResourceAsStream(fileName));    
            }else{
            	 document =saxReader.read(GenEntityMapper.this.getClass().getResourceAsStream(fileName));    
            }    
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
    
    public  void getMapperResultByElement(Element resultMapElement ,String entity){  
        try {  	   
            if(resultMapElement != null){  
                @SuppressWarnings("unchecked")  
                List<Element> properties = resultMapElement.elements();       
                for (Element prop : properties) {  
                	if(prop !=null){        		     		
                		JSONObject jsonObject = new JSONObject();
                		jsonObject.put("column", prop.attributeValue("column"));
                		jsonObject.put("jdbcType", prop.attributeValue("jdbcType"));
                		enttyMapperRedis.set(entity, prop.attributeValue("property"), jsonObject);    
                	}
                } 
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    static final List<String> files = new ArrayList<String>();
	public static List<String>  find(String path, String reg) {
		Pattern pat = Pattern.compile(reg);
		File file = new File(path);
	//	System.out.println(file);
	//	System.out.println(file.getPath());
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
	@SuppressWarnings("null")
	public  void  generateEnttyMapper(){
		    URL url=Thread.currentThread().getContextClassLoader().getResource("mapper");
		    String protocol =url.getProtocol();
		    if("jar".equals(protocol)){
		    	try {
					JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
					JarFile jarFile = jarURLConnection.getJarFile();  
			        System.out.println("jarFile:" + jarFile.getName()); 		        
			        Enumeration<JarEntry> jarEntries = jarFile.entries(); 
			    //   List<String> mapperfiles=null;
			        while(jarEntries.hasMoreElements()){
			            JarEntry entry = jarEntries.nextElement();               
			            if (entry.getName().trim().startsWith("mapper")&& !entry.isDirectory()) {
			            	System.out.println("mapper："+entry.getName());     
			            	String name=entry.getName();
			            	String entity =name.substring(name.lastIndexOf("/")+1, name.length()).replace("Dao.xml", "");
			            	System.out.println(entity);
			        		String filename=entry.getName().substring(entry.getName().lastIndexOf("/")+1, entry.getName().length());
			        		System.out.println(filename);
			        		Element e = getResultMapElement(entry.getName(),"BaseResultMap",true);     	
			        		getMapperResultByElement(e, entity);
			            }
			        }
			       
			        
			        jarFile.close();	       
			       	       
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		    }else if("file".equals(protocol)){
		    	 List<String> filenames=find(GenEntityMapper.class.getClassLoader().getResource("mapper").getPath(), "\\S+\\.xml");
			        //放在redis中会好效率一些，太多层了。
			        for(String filename:filenames){
			        	if (filename!=null && filename!="") {
			        		String entity =filename.substring(filename.lastIndexOf("\\")+1, filename.length()).replace("Dao.xml", "");
			        		filename=filename.replace("\\","/");
			        		String str ="/mapper";
			        		String relePath = filename.substring(filename.indexOf(str), filename.length());
			        	//	System.out.println("entity:"+entity);
			        	//	System.out.println("relePath:"+relePath);
			        		Element e = getResultMapElement(relePath,"BaseResultMap",false);     	
			        		getMapperResultByElement(e, entity);
						}	
			        }
		    }     
	}
 
}  