package com.king.dao;

import java.util.List;

import com.king.dal.gen.dao.BaseDao;
import com.king.dal.gen.model.smp.SysUserRole;

/**
 * 用户与角色对应关系
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
public interface SysUserRoleDao extends BaseDao<SysUserRole> {
	
	/**
	 * 根据用户ID，获取角色ID列表
	 */
	List<Long> queryRoleIdList(Long userId);
	
	/**
	 * 根据角色ID，获取授权用户ID列表
	 */
	List<Long> queryUserIdList(Long roleId);
}
