package com.king.common.utils.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月20日
 */
public class Message {
	
	private static Logger log = LoggerFactory.getLogger(BeanUtils.class);

	public static final String PUBLIC_PROPERTIES_RESOURCE = "";

	public static final String MESSAGE_COMPART_SYMBOL = ";";

	/**
	 * 取指定文件的持久的属性集
	 * 
	 * @param bundleName
	 *            资源文件全称
	 * @return 持久的属性集
	 */
	public static Properties getProperties(String bundleName) {

		InputStream in = Message.class.getResourceAsStream(bundleName);
		Properties properties = new Properties();
		if (null != in) {
			try {
				properties.load(in);
				in.close();
			} catch (IOException ex) {
				log.error("error : can't load Properties!");
			} catch (Exception ex) {
				log.error("error : can't load Properties!");
				if (null != ex.getMessage()) {
					log.error("error:" + ex.getMessage(), ex);

				} else {
					log.error("error", ex);
				}
			}
		} else {
			log.info("info : can't InputStream bundleName:" + bundleName);
		}
		return properties;
	}
	
	public static Properties getProperties(File file){
		
		Properties properties = new Properties();
		try {
			InputStream in = new FileInputStream(file);
			try {
				properties.load(in);
				in.close();
			} catch (IOException ex) {
				log.error("error : can't load Properties!");
			} catch (Exception ex) {
				log.error("error : can't load Properties!");
				if (null != ex.getMessage()) {
					log.error("error:" + ex.getMessage(), ex);

				} else {
					log.error("error", ex);
				}
			}
		} catch (FileNotFoundException e) {
			log.info("info : can't InputStream bundleName");
			e.printStackTrace();
		}
		return properties;
	}
	
	public static String getString(File file,String key,String defaultString){
		return getString(getProperties(file), key, defaultString);
	}

	/**
	 * 取指定资源文件里的指定信息
	 * 
	 * @param properties
	 *            持久的属性集
	 * @param key
	 *            关键字
	 * @return 语言信息
	 */
	private static String getString(Properties properties, String key) {
		String result = key;
		if (properties == null)
			return key;
		if ((key != null) && (key.length() != 0)) {
			try {
				result = properties.getProperty(key);
			} catch (MissingResourceException e) {
				result = key;
			}
		}
		return result;
	}

	/**
	 * 取指定资源文件里的指定信息
	 * 
	 * @param properties
	 *            持久的属性集
	 * @param key
	 *            关键字
	 * @param args
	 *            参数值
	 * @return 语言信息
	 */
	private static String getString(Properties properties, String key,
			String[] args) {
		String result = key;
		if (properties == null)
			return key;
		if ((key != null) && (key.length() != 0)) {
			try {
				result = properties.getProperty(key);

				if (args != null) {
					for (int i = 0; i < args.length; i++) {
						result = result
								.replaceFirst("\\{" + i + "\\}", args[i]);
					}
				}
			} catch (MissingResourceException e) {
				result = key;
			}
		}
		return result;
	}

	/**
	 * 取指定资源文件里的指定信息
	 * 
	 * @param properties
	 *            持久的属性集
	 * @param key
	 *            关键字
	 * @param defaultValue
	 *            默认值 如果找不到key对应的值，则返回默认值
	 * @return 语言信息
	 */
	private static String getString(Properties properties, String key,
			String defaultValue) {
		String result = key;
		if (properties == null)
			return key;
		if ((key != null) && (key.length() != 0)) {
			try {
				result = properties.getProperty(key, defaultValue);
			} catch (MissingResourceException e) {
				result = key;
			}
		}
		return result;
	}

	/**
	 * 从public资源中取指定的信息
	 * 
	 * @param key
	 *            关键字
	 * @return 语言信息
	 */
	public static String getPublicString(String key) {
		Properties properties = getProperties(PUBLIC_PROPERTIES_RESOURCE);
		return getString(properties, key);
	}

	/**
	 * 从public资源中取指定的信息
	 * 
	 * @param key
	 *            关键字
	 * @param defaultValue
	 *            默认值 如果找不到key对应的值，则返回默认值
	 * @return 语言信息
	 */
	public static String getPublicString(String key, String defaultValue) {
		Properties properties = getProperties(PUBLIC_PROPERTIES_RESOURCE);
		return getString(properties, key, defaultValue);
	}

	/**
	 * 从public资源中取指定的信息，并用替换参数
	 * 
	 * @param key
	 *            关键字
	 * @param args
	 *            参数值
	 * @return 语言信息
	 */
	public static String getPublicString(String key, String[] args) {
		Properties properties = getProperties(PUBLIC_PROPERTIES_RESOURCE);
		return getString(properties, key, args);
	}

	/**
	 * 取指定资源文件里的指定信息
	 * 
	 * @param bundleName
	 *            资源文件全称
	 * @param key
	 *            关键字
	 * @param args
	 *            参数值
	 * @return 语言信息
	 */
	public static String getString(String bundleName, String key, String[] args) {
		Properties properties = getProperties(bundleName);
		return getString(properties, key, args);
	}

	/**
	 * 取指定资源文件里的指定信息
	 * 
	 * @param bundleName
	 *            资源文件全称
	 * @param key
	 *            关键字
	 * @return 语言信息
	 */
	public static String getString(String bundleName, String key,
			String defaultValue) {
		Properties properties = getProperties(bundleName);
		return getString(properties, key, defaultValue);
	}
	
	/**
	 * 取指定资源文件里的指定信息
	 * 
	 * @param bundleName
	 *            资源文件全称
	 * @param key
	 *            关键字
	 * @param defaultValue
	 *            默认值 如果找不到key对应的值，则返回默认值
	 * @return 语言信息
	 */
	public static String getString(String bundleName, String key) {
		Properties properties = getProperties(bundleName);
		return getString(properties, key);
	}

	/**
	 * 取指定资源文件里的指定信息
	 * 
	 * @param bundleName
	 *            资源文件全称
	 * @param key
	 *            多个关键字
	 * @return 语言信息
	 */
	public static String toString(String bundleName, String[] key) {
		return toString(bundleName, key, MESSAGE_COMPART_SYMBOL);
	}

	/**
	 * 取指定资源文件里的指定信息
	 * 
	 * @param bundleName
	 *            资源文件全称
	 * @param key
	 *            多个关键字
	 * @param symbol
	 *            分隔符号
	 * @return 语言信息
	 */
	public static String toString(String bundleName, String[] key, String symbol) {
		Properties properties = getProperties(bundleName);
		StringBuffer message = new StringBuffer("");
		if (key != null) {
			for (int i = 0; i < key.length; i++) {
				message.append(getString(properties, key[i]));
				message.append(symbol);
			}
		}
		return message.toString();
	}
}
