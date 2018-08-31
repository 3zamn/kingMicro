package com.king.dao;

import java.util.List;
import java.util.Map;

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
	List<Long> queryRoleIdList(Object userId);
	
	/**
	 * 根据角色ID，获取授权用户ID列表
	 */
	List<Long> queryUserIdList(Object roleId);
	
	/**
	 * 保存角色的授权用户userIdList
	 * @param map 
	 * @return
	 */
	int saveUserList(Map<String, Object> map);
	
	/**
	 * 根据角色删除记录
	 * @param roleId 
	 * @return
	 */
	int deleteByRoleId(Object roleId);
}
