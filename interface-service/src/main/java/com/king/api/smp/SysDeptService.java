package com.king.api.smp;


import java.util.List;

import com.king.dal.gen.model.smp.SysDept;
import com.king.dal.gen.service.BaseService;

/**
 * 部门管理
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
public interface SysDeptService extends BaseService<SysDept>{
	
	/**
	 * 保存或更新角色、部门相关联
	 */
	void saveOrUpdate_R_D(Object roleId, List<Long> deptIdList);
	
	/**
	 * 根据角色ID，获取部门ID列表
	 */
	List<Long> queryDeptIdListByRoleId(Object roleId);

	/**
	 * 查询子部门ID列表
	 * @param parentId  上级部门ID
	 */
	List<Long> queryDeptIdList(Object parentId);

	/**
	 * 向下获取子部门ID(是否包含本部门ID)，用于数据过滤
	 */
	String getDownDeptIdList(Object deptId,Boolean include);
	
	/**
	 * 获取顶级节点下所有部门ID，用于数据过滤
	 */
	String getTopDeptIdList(Object deptId);
	
	/**
	 * 查询子部门ID列表
	 * @param parentIds  上级部门ID
	 */
	List<Long> queryDeptIdLists(List<Long> parentId);
	
	Long queryParentDeptId(Object deptId);

}
