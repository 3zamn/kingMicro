package com.king.dao;

import java.util.List;

import com.king.dal.gen.dao.BaseDao;
import com.king.dal.gen.model.smp.SysMenu;

/**
 * 菜单管理
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
public interface SysMenuDao extends BaseDao<SysMenu> {
	
	/**
	 * 根据父菜单，查询子菜单
	 * @param parentId 父菜单ID
	 */
	List<SysMenu> queryListParentId(Long parentId);
	
	/**
	 * 获取不包含按钮的菜单列表
	 */
	List<SysMenu> queryNotButtonList();
	
	/**
	 * 查询用户的权限列表
	 */
	List<SysMenu> queryUserList(Long userId);
}
