package test.xml;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.king.common.utils.file.FileToolkit;

import test.xml.base.XmlBase;
import test.xml.util.Xml4bigKit;
import test.xml.vo.Car;
import test.xml.vo.Row;

public class Test {

	public static void main(String[] args) {
	/*	long  startTime = System.currentTimeMillis();	//开始时间
		String dir="F:\\test";
		String[] files=FileToolkit.listFilebySuffix(dir, "xml");
		for(String file:files){
			String filePath=dir+File.separator+file;
			
			String dataStartTag =null;
			List<Class<? extends XmlBase>> clslist = new ArrayList<Class<? extends XmlBase>>();
			clslist.add(Row.class);
			Xml4bigKit.parseXml(filePath, dataStartTag,clslist,new XmlHandler1());
		
			
		}
		long stopTime = System.currentTimeMillis();		//写文件时间
		System.out.println("write xlsx file time: " + (stopTime - startTime)/1000 + "m");*/
		String xpath=System.getProperty("user.dir")+"/src/test/java/test/xml/CarDataBig.xml";
		System.out.println(System.getProperty("user.dir"));
		long  startTime = System.currentTimeMillis();	//开始时间
		String dataStartTag =null;
		List<Class<? extends XmlBase>> clslist = new ArrayList<Class<? extends XmlBase>>();
		clslist.add(Car.class);
		Xml4bigKit.parseXml(xpath, dataStartTag,clslist,new XmlHandler1());
		long stopTime = System.currentTimeMillis();		//写文件时间
		System.out.println("write xlsx file time: " + (stopTime - startTime)/1000 + "m");
		/*try {
			LinkedHashMap<Field, Object> map = new LinkedHashMap<>();
			map.put(Car.class.getField("type"), new Date());
			map.put(Car.class.getField("type"), null);
		} catch (Exception e) {
			// TODO: handle exception
		}*/
	}

}
