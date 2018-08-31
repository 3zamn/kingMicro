package com.king.aspect;
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

import com.king.api.smp.SysDeptService;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.exception.RRException;
import com.king.dal.gen.model.smp.SysUser;
import com.king.dao.SysRoleDeptDao;
import com.king.dao.SysUserRoleDao;


/**
 * 数据权限过滤，切面处理类
 * @author King chen
 * @date 2017年12月25日
 */
@Aspect
@Component
public class DataFilterAspect {
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysUserRoleDao sysUserRoleDao;
    @Autowired
    private SysRoleDeptDao sysRoleDeptDao;

    @Pointcut("@annotation(com.king.common.annotation.DataFilter)")
    public void dataFilterCut() {
    	//数据权限切面
    }

    @Before("dataFilterCut()")
    public void dataFilter(JoinPoint point) throws Throwable {
        Object params = point.getArgs()[0];
        if(params != null && params instanceof Map){
        	@SuppressWarnings("rawtypes")
			Object object = ((Map)params).get("user");
        	if(object !=null && object instanceof SysUser){
        		  //如果不是超级管理员，则只能查询本部门及子部门数据
        		SysUser user = (SysUser)object;
                if(user.getUserId() != Constant.SUPER_ADMIN){
                    @SuppressWarnings("unchecked")
					HashMap<String, Object> map = (HashMap<String, Object>)params;
                    map.put("filterSql", getFilterSQL(user, point));
                }
        	}      
            return ;
        }
        throw new RRException("要实现数据权限接口的参数，只能是Map类型，且不能为NULL");
    }

    /**
     * 数据权限过滤的SQL
     */
    private String getFilterSQL(SysUser user, JoinPoint point){
        MethodSignature signature = (MethodSignature) point.getSignature();
        com.king.common.annotation.DataFilter dataFilter = signature.getMethod().getAnnotation(com.king.common.annotation.DataFilter.class);
        //获取表的别名
        StringBuilder filterSql = new StringBuilder();
        String tableAlias = dataFilter.tableAlias();
        if(StringUtils.isNotBlank(tableAlias)){
            tableAlias +=  ".";
        }
       if(dataFilter.top()){//获取父节点是0L的根节点、自顶向下整颗树
    	   Long deptId= user.getDeptId();
    	   String deptIds=sysDeptService.getTopDeptIdList(deptId);
    	   if(deptIds!=null){
    		   filterSql.append("and (");
    		   filterSql.append(tableAlias).append("dept_id in(").append(deptIds).append("))");
    	   }
    	       	   
       }else{//勾选数据权限
    	   Set<Long> deptIds = new HashSet<>();
           StringBuilder listSubDeptId = new StringBuilder();
           List<Long> list= sysUserRoleDao.queryRoleIdList(user.getUserId());
           for(Long roleId:list){
           	List<Long> deptId=sysRoleDeptDao.queryDeptIdList(roleId);
           	deptIds.addAll(deptId);
           }
           for(Long deptId:deptIds){
           	 String subDeptIds = sysDeptService.getDownDeptIdList(deptId,true);
           	 listSubDeptId.append(","+subDeptIds);
           }
           //获取子部门ID

           if(listSubDeptId.length()>0){
           	  filterSql.append("and (");
                 filterSql.append(tableAlias).append("dept_id in(").append(listSubDeptId.toString().replaceFirst(",", "")).append(")");
                 filterSql.append(")");
           }else{
           	//没有数据权限或没勾选数据权限
           	 filterSql.append("and 1=0");
           	//没有本部门数据权限，也能查询本人数据
               if(dataFilter.user()){
                   filterSql.append(" or ").append(tableAlias).append("user_id=").append(user.getUserId());
               }
           }
       }
      
        return filterSql.toString();
    }
}
