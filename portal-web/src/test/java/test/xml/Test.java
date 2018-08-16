package test.xml;

import java.util.ArrayList;
import java.util.List;

import com.king.utils.xml.base.XmlBase;
import com.king.utils.xml.util.Xml4bigKit;

import test.xml.vo.Car;

public class Test {

	public static void main(String[] args) {
		String xpath=System.getProperty("user.dir")+"/src/test/java/test/xml/CarDataBig.xml";
		System.out.println(System.getProperty("user.dir"));
		long  startTime = System.currentTimeMillis();	//开始时间
		String dataStartTag =null;
		List<Class<? extends XmlBase>> clslist = new ArrayList<Class<? extends XmlBase>>();
		clslist.add(Car.class);
		Xml4bigKit.parseXml(xpath, dataStartTag,clslist,new XmlHandler1());
		long stopTime = System.currentTimeMillis();		//写文件时间
		System.out.println("write xlsx file time: " + (stopTime - startTime)/1000 + "m");
	}

}
