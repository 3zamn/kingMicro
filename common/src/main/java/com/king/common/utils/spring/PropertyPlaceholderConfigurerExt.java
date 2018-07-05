package com.king.common.utils.spring;

import java.util.Enumeration;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;

import com.king.common.utils.security.SecurityUtil;
import com.king.common.utils.security.crypto.Sha256Hash;


/**
 * 配置文件密码加密处理
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年6月27日
 */
public class PropertyPlaceholderConfigurerExt extends PropertyPlaceholderConfigurer {
	private static final String key="YzcmCZNvbXocrsz9dm8e";
	
	protected void convertProperties(Properties props) {
		@SuppressWarnings("rawtypes")
		Enumeration propertyNames = props.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String propertyName = (String) propertyNames.nextElement();
			String propertyValue = props.getProperty(propertyName);
			String convertedValue = convertPropertyValue(propertyName,propertyValue);
			if (!ObjectUtils.nullSafeEquals(propertyValue, convertedValue)) {
				props.setProperty(propertyName, convertedValue);
			}
		}
	}
	
	protected String convertPropertyValue(String propertyName,String originalValue) {
		String value = originalValue;
		if (propertyName.endsWith(".password")){
			try {
				value =SecurityUtil.decryptDes(originalValue, key.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			value = super.convertPropertyValue(originalValue);
		}
		return value;
	}
	
	public void setLocation(Resource location) {
		super.setLocation(location);
	}
}
