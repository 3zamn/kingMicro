package com.king.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.king.dal.gen.dao.BaseDao;
import com.king.dal.gen.model.smp.SysRoleDept;

/**
 * 角色与部门对应关系
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
public interface SysRoleDeptDao extends BaseDao<SysRoleDept> {
	
	/**
	 * 根据角色ID，获取部门ID列表
	 */
	List<Long> queryDeptIdList(Long roleId);
	
	 /**
     * 查询子部门ID列表
     * @param parentIds  上级部门ID
     */
    List<Long> queryDetpIdLists(@Param("parentIds")List<Long> parentIds);
}
