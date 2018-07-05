package com.king.common.utils.spring;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.king.common.utils.pattern.ParserUtil;
import com.king.common.utils.pattern.StringToolkit;


/**
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年6月27日
 */
public class BeanUtils {

	private static Logger log = LoggerFactory.getLogger(BeanUtils.class);

	/**
	 * 
	 * @param dest
	 * @param orig
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void copyProperties(Object dest, Object orig)
			throws IllegalAccessException, InvocationTargetException {
		copyProperties(dest, orig, false);
	}
	
	public static void copyPropertiesInclude(Object dest, Object orig,
			boolean copyNullValue, String include)
			throws IllegalAccessException, InvocationTargetException {
		copyProperties(dest, orig, copyNullValue, include, null);
	}
	
	/**
	 * 
	 * @param dest
	 * @param orig
	 * @param copyNullValue
	 * @param exclude
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void copyPropertiesExclude(Object dest, Object orig,
			boolean copyNullValue, String exclude)
			throws IllegalAccessException, InvocationTargetException {
		copyProperties(dest, orig, copyNullValue, null, exclude);
	}

	public static void copyProperties(Object dest, Object orig,
			boolean copyNullValue, String include, String exclude)
			throws IllegalAccessException, InvocationTargetException {
		// Validate existence of the specified beans
		if (dest == null) {
			throw new IllegalArgumentException("No destination bean specified");
		}
		if (orig == null) {
			throw new IllegalArgumentException("No origin bean specified");
		}
		if (log.isDebugEnabled()) {
			log.debug("BeanUtils.copyProperties(" + dest + ", " + orig + ")");
		}

		String[] includes = new String[] {};
		String[] excludes = new String[] {};
		if (StringToolkit.isNotEmpty(include)) {
			includes = include.split(",");
		}
		if (StringToolkit.isNotEmpty(exclude)) {
			excludes = exclude.split(",");
		}

		// Copy the properties, converting as necessary
		if (orig instanceof DynaBean) {
			DynaProperty origDescriptors[] = ((DynaBean) orig).getDynaClass()
					.getDynaProperties();
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				if (PropertyUtils.isWriteable(dest, name)) {
					Object value = ((DynaBean) orig).get(name);

					if (copyNullValue == false) {
						if (value == null
								|| value.toString().trim().length() == 0) {
							continue;
						}
					}

					if (ArrayUtils.contains(excludes, name)) {
						continue;
					}

					if (includes.length > 0
							&& !ArrayUtils.contains(includes, name)) {
						continue;
					}

					org.apache.commons.beanutils.BeanUtils.copyProperty(dest,
							name, value);
				}
			}
		} else if (orig instanceof Map) {
			Iterator names = ((Map) orig).keySet().iterator();
			while (names.hasNext()) {
				String name = (String) names.next();
				if (PropertyUtils.isWriteable(dest, name)) {
					Object value = ((Map) orig).get(name);

					if (copyNullValue == false) {
						if (value == null
								|| value.toString().trim().length() == 0) {
							continue;
						}
					}

					if (ArrayUtils.contains(excludes, name)) {
						continue;
					}

					if (includes.length > 0
							&& !ArrayUtils.contains(includes, name)) {
						continue;
					}

					org.apache.commons.beanutils.BeanUtils.copyProperty(dest,
							name, value);
				}
			}
		} else /* if (orig is a standard JavaBean) */{
			PropertyDescriptor origDescriptors[] = PropertyUtils
					.getPropertyDescriptors(orig);
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				if ("class".equals(name)) {
					continue; // No point in trying to set an object's class
				}
				if (PropertyUtils.isReadable(orig, name)
						&& PropertyUtils.isWriteable(dest, name)) {
					try {
						Object value = PropertyUtils.getSimpleProperty(orig,
								name);
						if (log.isDebugEnabled())
							log.debug("orig: property name:" + name + " value:"
									+ value);
						if (copyNullValue == false) {
							if (value == null
									|| value.toString().trim().length() == 0) {
								continue;
							}
						}

						if (ArrayUtils.contains(excludes, name)) {
							continue;
						}

						if (includes.length > 0
								&& !ArrayUtils.contains(includes, name)) {
							continue;
						}

						org.apache.commons.beanutils.BeanUtils.copyProperty(
								dest, name, value);
					} catch (NoSuchMethodException e) {
						; // Should not happen
					}
				}
			}
		}
	}

	public static void copyProperties(Object dest, Object orig,
			boolean copyNullValue) throws IllegalAccessException,
			InvocationTargetException {
		// Validate existence of the specified beans
		if (dest == null) {
			throw new IllegalArgumentException("No destination bean specified");
		}
		if (orig == null) {
			throw new IllegalArgumentException("No origin bean specified");
		}
		if (log.isDebugEnabled()) {
			log.debug("BeanUtils.copyProperties(" + dest + ", " + orig + ")");
		}

		// Copy the properties, converting as necessary
		if (orig instanceof DynaBean) {
			DynaProperty origDescriptors[] = ((DynaBean) orig).getDynaClass()
					.getDynaProperties();
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				if (PropertyUtils.isWriteable(dest, name)) {
					Object value = ((DynaBean) orig).get(name);

					/* add by hekun & hbm, don't copy null value */
					if (copyNullValue == false) {
						if (value == null
								|| value.toString().trim().length() == 0) {
							continue;
						}
					}
					/* add by hekun */
					org.apache.commons.beanutils.BeanUtils.copyProperty(dest,
							name, value);
				}
			}
		} else if (orig instanceof Map) {
			Iterator names = ((Map) orig).keySet().iterator();
			while (names.hasNext()) {
				String name = (String) names.next();
				if (PropertyUtils.isWriteable(dest, name)) {
					Object value = ((Map) orig).get(name);

					/* add by hekun & hbm, don't copy null value */
					if (copyNullValue == false) {
						if (value == null
								|| value.toString().trim().length() == 0) {
							continue;
						}
					}
					/* add by hekun */

					org.apache.commons.beanutils.BeanUtils.copyProperty(dest,
							name, value);
				}
			}
		} else /* if (orig is a standard JavaBean) */{
			PropertyDescriptor origDescriptors[] = PropertyUtils
					.getPropertyDescriptors(orig);
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				if ("class".equals(name)) {
					continue; // No point in trying to set an object's class
				}
				if (PropertyUtils.isReadable(orig, name)
						&& PropertyUtils.isWriteable(dest, name)) {
					try {
						Object value = PropertyUtils.getSimpleProperty(orig,
								name);
						if (log.isDebugEnabled())
							log.debug("orig: property name:" + name + " value:"
									+ value);
						/* add by hekun & hbm, don't copy null value */
						if (copyNullValue == false) {
							if (value == null
									|| value.toString().trim().length() == 0) {
								continue;
							}
						}
						/* add by hekun */
						org.apache.commons.beanutils.BeanUtils.copyProperty(
								dest, name, value);
					} catch (NoSuchMethodException e) {
						; // Should not happen
					}
				}
			}
		}
	}

	public static void copyvaluewithNULL(Object ob, String property,
			Object value) {
		Method[] method = ob.getClass().getMethods();
		try {
			String getmethodname = "set" + StringToolkit.capitalize(property);
			Object values[] = new Object[1];
			if (value == null || "".equals(value))
				value = null;
			values[0] = value;
			for (int i = 0; i < method.length; i++) {
				if (getmethodname.equals(method[i].getName())) {
					method[i].invoke(ob, values);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void setProperty(Serializable bean, String name, Object value)
			throws IllegalAccessException, InvocationTargetException {
		org.apache.commons.beanutils.BeanUtils.setProperty(bean, name, value);
	}

	/**
	 * Return the entire set of properties for which the specified bean provides
	 * a read method. This map can be fed back to a call to
	 * <code>BeanUtils.populate()</code> to reconsitute the same set of
	 * properties, modulo differences for read-only and write-only properties,
	 * but only if there are no indexed properties.
	 *
	 * @param bean
	 *            Bean whose properties are to be extracted
	 *
	 * @exception IllegalAccessException
	 *                if the caller does not have access to the property
	 *                accessor method
	 * @exception InvocationTargetException
	 *                if the property accessor method throws an exception
	 * @exception NoSuchMethodException
	 *                if an accessor method for this property cannot be found
	 */
	public static Map describe(Object bean) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		if (bean == null) {
			// return (Collections.EMPTY_MAP);
			return (new java.util.HashMap());
		}

		if (log.isDebugEnabled()) {
			log.debug("Describing bean: " + bean.getClass().getName());
		}

		Map description = new HashMap();
		if (bean instanceof DynaBean) {
			DynaProperty descriptors[] = ((DynaBean) bean).getDynaClass()
					.getDynaProperties();
			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();
				description.put(name, getProperty(bean, name));
			}
		} else {
			PropertyDescriptor descriptors[] = PropertyUtils
					.getPropertyDescriptors(bean);
			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();
				if (descriptors[i].getReadMethod() != null)
					description.put(name, getProperty(bean, name));
			}
		}
		return (description);

	}

	/**
	 * companyName ת��Ϊ company_name,
	 * 
	 * @param javaPropertyName
	 * @return
	 */
	private String toDBStyleName(String javaPropertyName) {
		if (javaPropertyName == null || javaPropertyName.length() < 2)
			return javaPropertyName;

		StringBuffer buffer = new StringBuffer(javaPropertyName.length());

		buffer.append(javaPropertyName.charAt(0));
		for (int i = 1; i < javaPropertyName.length(); i++) {
			char ch = javaPropertyName.charAt(i);
			if (ch >= 'A' && ch <= 'Z') {
				buffer.append("_").append(Character.toLowerCase(ch));
			} else
				buffer.append(ch);
		}
		return buffer.toString();
	}

	public static boolean containsProperty(Object obj, String key) {
		try {
			if (null == obj){
				return false;
			}
			
			if (obj instanceof JSONObject){
				return ((JSONObject)obj).containsKey(key);
			}
			
			if (obj instanceof JSONArray){
				return false;
			}
			
			return PropertyUtils.describe(obj).containsKey(key);
		} catch (Exception e) {
			return false;
		}
	}

	public static <T> T deepClone(T src) throws IOException,
			OptionalDataException, ClassNotFoundException {
		if (!(src instanceof Serializable)) {
			return null;
		}
		// 将对象写到流里
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		oo.writeObject(src);
		// 从流里读出来
		ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
		ObjectInputStream oi = new ObjectInputStream(bi);
		return (T) (oi.readObject());
	}

	public static Object getProperty(Object bean, String name) {
		if (null == bean || StringToolkit.isEmpty(name)){
			return null;
		}
		
		List nameList = new ArrayList();
		List idxList = new ArrayList();
		List keyList = new ArrayList();
		try {
			ParserUtil.analyzeName(name, nameList, idxList, keyList);
			String tName = "";
			while (nameList.size() > 0) {
				tName = (String) nameList.get(0);
				nameList.remove(0);
				if (ParserUtil.IDX_MODEL.equals(tName)) {
					int index = ((Integer) idxList.get(0)).intValue();
					idxList.remove(0);
					bean = getIdxProperty(bean, index);
				} else if (ParserUtil.MAP_MODEL.equals(tName)) {
					String key = (String) keyList.get(0);
					keyList.remove(0);
					bean = getMapProperty(bean, key);
				} else if (ParserUtil.NESTED_MODEL.equals(tName)) {
					continue;
				} else {
					bean = PropertyUtils.getProperty(bean, tName);
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return bean;
	}

	public static Object getIdxProperty(Object bean, int index) {
		if (!bean.getClass().isArray()) {
			if (!(bean instanceof java.util.List)) {
				if (log.isInfoEnabled()) {
					log.info("Object '" + bean.getClass().getName()
							+ "' is not indexed");
				}
				return null;
			} else {
				// get the List's value
				return ((java.util.List) bean).get(index);
			}
		} else {
			// get the array's value
			return (Array.get(bean, index));
		}
	}

	public static Object getMapProperty(Object bean, String key) {
		if (bean instanceof java.util.Map) {
			return ((Map) bean).get(key);
		} else {
			if (log.isInfoEnabled()) {
				log.info("Object '" + bean.getClass().getName()
						+ "' is not Map type");
			}
			return null;
		}
	}
}
