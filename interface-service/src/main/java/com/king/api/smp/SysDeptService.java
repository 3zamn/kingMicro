package com.king.api.smp;


import java.util.List;
import java.util.Map;

import com.king.dal.gen.model.SysDept;

/**
 * 部门管理
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
public interface SysDeptService {
	
	SysDept queryObject(Long deptId);
	
	List<SysDept> queryList(Map<String, Object> map);

	void save(SysDept sysDept);
	
	void update(SysDept sysDept);
	
	void delete(Long deptId);

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
