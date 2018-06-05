package com.king.aspect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.king.api.smp.SysDeptService;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.entityMapper.EntityMapperResolver;
import com.king.common.utils.exception.RRException;
import com.king.common.utils.spring.SpringContextUtils;
import com.king.dal.gen.model.smp.SysUser;
import com.king.dao.SysRoleDeptDao;
import com.king.dao.SysUserRoleDao;

/**
 * 列表页动态列数据过滤
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年6月5日
 */
@Aspect
@Component
public class DynamicColFilterAspect {
 

    @Pointcut("@annotation(com.king.common.annotation.DynamicCol)")
    public void dynamicColFilterCut() {

    }

    @Before("dynamicColFilterCut()")
    public void getColFilter(JoinPoint point) throws Throwable {
        Object params = point.getArgs()[0];
        if(params != null && params instanceof Map){   
        	SysUser user=null;
        	@SuppressWarnings("rawtypes")
			Object object = ((Map)params).get("user");
        	if(object !=null && object instanceof SysUser){
        		 user = (SysUser)object;
        	}
			@SuppressWarnings("unchecked")
			HashMap<String, Object> map = (HashMap<String, Object>)params;
            map.put("paramsCol", getColFilter(user, point));
            return ;
        }
        throw new RRException("要实现数据权限接口的参数，只能是Map类型，且不能为NULL");
    }

    /**
     * 获取列
     */
    private String getColFilter(SysUser user, JoinPoint point){
        MethodSignature signature = (MethodSignature) point.getSignature();
        com.king.common.annotation.DynamicCol dynamicCol = signature.getMethod().getAnnotation(com.king.common.annotation.DynamicCol.class);
        String entityName=dynamicCol.entity();
        List<String> keyParam = new ArrayList<String>();
        //测试
        keyParam.add("logId");
        keyParam.add("beanName");
        keyParam.add("methodName");
        keyParam.add("params");
        List<String> atts = new ArrayList<String>();
        StringBuilder filterSql = new StringBuilder();
 		for(Object o:keyParam){
 			if((SpringContextUtils.getBean("enttyMapperResolver",EntityMapperResolver.class)).isExistAttribute(entityName, o.toString())){
 				atts.add(o.toString());				
 			}		
 		}
 		for(String att:atts){//实体属性转换表字段
 			JSONObject json = (SpringContextUtils.getBean("enttyMapperResolver",EntityMapperResolver.class)).getColumn(entityName, att);
 			String column=json.getString("column");
 			filterSql.append(column);
 			filterSql.append(" ,");
 		}
 		String cols=" * ";//默认所有列
        if(filterSql.length()>0){
        	cols=filterSql.substring(0, filterSql.length()-1);
        }
      
        return cols;
    }
}
