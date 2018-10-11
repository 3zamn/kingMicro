package com.king.aspect;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.king.common.utils.entityMapper.EntityMapperResolver;
import com.king.common.utils.exception.RRException;
import com.king.common.utils.spring.SpringContextUtils;
import com.king.dal.gen.model.smp.SysUser;

/**
 * 列表页动态列数据过滤
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年6月5日
 */
@Aspect
@Component
public class DynamicColFilterAspect {
 

    @Pointcut("@annotation(com.king.common.annotation.DynamicColFilter)")
    public void dynamicColFilterCut() {
    	// 列表页动态列数据切面
    }

    @SuppressWarnings("rawtypes")
	@Before("dynamicColFilterCut()")
    public void getColFilter(JoinPoint point) throws Throwable {
        Object params = point.getArgs()[0];
        if(params != null && params instanceof Map){   
        	SysUser user=null;
			Object object = ((Map)params).get("user");
        	Class entity =(Class)((Map)params).get("entity");
        	if(object !=null && object instanceof SysUser){
        		 user = (SysUser)object;
        	}
			@SuppressWarnings("unchecked")
			HashMap<String, Object> map = (HashMap<String, Object>)params;
            map.put("paramsCol", getColFilter(entity,user));
            return ;
        }
        throw new RRException("要实现数据权限接口的参数，只能是Map类型，且不能为NULL");
    }

    /**
     * 获取列
     */
    @SuppressWarnings("rawtypes")
	private String getColFilter(Class clazz, SysUser user) {
		String cols = "";
		try {
			List<String> keyParam = new ArrayList<>();
			String entityName = clazz.getSimpleName();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				com.king.common.annotation.PropertyExt dynamicCol = field.getAnnotation(com.king.common.annotation.PropertyExt.class);
				if (dynamicCol != null && dynamicCol.isShow()) {
					keyParam.add(field.getName());
				}
			}

			List<String> atts = new ArrayList<>();
			StringBuilder filterSql = new StringBuilder();
			for (Object o : keyParam) {// 校验是否存在
				if ((SpringContextUtils.getBean("entityMapperResolver", EntityMapperResolver.class)).isExistAttribute(entityName, o.toString())) {
					atts.add(o.toString());
				}
			}
			for (String att : atts) {// 实体属性转换表字段
				JSONObject json = (SpringContextUtils.getBean("entityMapperResolver", EntityMapperResolver.class)).getColumn(entityName, att);
				String column = json.getString("column");
				filterSql.append(column);
				filterSql.append(" ,");
			}

			if (filterSql.length() > 0) {
				cols = filterSql.substring(0, filterSql.length() - 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

		return cols;
	}
}
