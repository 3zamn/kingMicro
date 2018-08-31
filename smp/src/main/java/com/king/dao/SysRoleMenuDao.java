package com.king.dao;

import java.util.List;

import com.king.dal.gen.dao.BaseDao;
import com.king.dal.gen.model.smp.SysRoleMenu;

/**
 * 角色与菜单对应关系
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
public interface SysRoleMenuDao extends BaseDao<SysRoleMenu> {
	
	/**
	 * 根据角色ID，获取菜单ID列表
	 */
	List<Long> queryMenuIdList(Object roleId);
	
	/**
	 * 根据角色ID，获取扩展参数列表
	 */
	List<SysRoleMenu> queryParamsList(Object roleId);
}
