package com.king.api.smp;

import java.util.List;

/**
 * 角色与部门对应关系
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
public interface SysRoleDeptService {
	
	void saveOrUpdate(Long roleId, List<Long> deptIdList);
	
	/**
	 * 根据角色ID，获取部门ID列表
	 */
	List<Long> queryDeptIdList(Long roleId);
	
}
