package com.king.api.smp;


import java.util.List;
import java.util.Map;

import com.king.dal.gen.model.smp.ScheduleJob;
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
	void saveOrUpdate_R_D(Long roleId, List<Long> deptIdList);
	
	/**
	 * 根据角色ID，获取部门ID列表
	 */
	List<Long> queryDeptIdList(Long roleId);

	/**
	 * 查询子部门ID列表
	 * @param parentId  上级部门ID
	 */
	List<Long> queryDetpIdList(Long parentId);

	/**
	 * 获取子部门ID(包含本部门ID)，用于数据过滤
	 */
	String getSubDeptIdList(Long deptId);

}
