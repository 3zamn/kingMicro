package com.king.services.spi;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.king.api.smp.SysMenuService;
import com.king.api.smp.SysUserService;
import com.king.common.utils.constant.Constant;
import com.king.dal.gen.model.smp.SysMenu;
import com.king.dal.gen.model.smp.SysRoleMenu;
import com.king.dal.gen.service.BaseServiceImpl;
import com.king.dao.SysMenuDao;
import com.king.dao.SysRoleMenuDao;


@Transactional
@Service("sysMenuService")
public class SysMenuServiceImpl extends BaseServiceImpl<SysMenu> implements SysMenuService {
	@Autowired
	private SysMenuDao sysMenuDao;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysRoleMenuDao sysRoleMenuDao;
	
	@Transactional(readOnly = true)
	public List<SysMenu> queryListParentId(Object parentId, List<Long> menuIdList) {
		List<SysMenu> menuList = queryListParentId(parentId);
		if(menuIdList == null){
			return menuList;
		}
		
		List<SysMenu> userMenuList = new ArrayList<>();
		for(SysMenu menu : menuList){
			if(menuIdList.contains(menu.getMenuId())){
				userMenuList.add(menu);
			}
		}
		return userMenuList;
	}

	@Transactional(readOnly = true)
	public List<SysMenu> queryListParentId(Object parentId) {
		return sysMenuDao.queryListParentId(parentId);
	}

	@Transactional(readOnly = true)
	public List<SysMenu> queryNotButtonList() {
		return sysMenuDao.queryNotButtonList();
	}

	@Transactional(readOnly = true)
	public List<SysMenu> getUserMenuList(Object userId) {
		//系统管理员，拥有最高权限
		if(userId.equals(Constant.SUPER_ADMIN)){
			return getAllMenuList(null);
		}
		
		//用户菜单列表
		List<Long> menuIdList = sysUserService.queryAllMenuId(userId);
		return getAllMenuList(menuIdList);
	}
	
	@Transactional(readOnly = true)
	public List<SysMenu> queryUserList(Object userId) {
		return sysMenuDao.queryUserList(userId);
	}

	/**
	 * 获取所有菜单列表
	 */
	@Transactional(readOnly = true)
	private List<SysMenu> getAllMenuList(List<Long> menuIdList){
		//查询根菜单列表
		List<SysMenu> menuList = queryListParentId(0L, menuIdList);
		//递归获取子菜单
		getMenuTreeList(menuList, menuIdList);
		
		return menuList;
	}

	/**
	 * 递归
	 */
	@Transactional(readOnly = true)
	private List<SysMenu> getMenuTreeList(List<SysMenu> menuList, List<Long> menuIdList){
		List<SysMenu> subMenuList = new ArrayList<>();
		
		for(SysMenu entity : menuList){
			if(entity.getType() == Constant.MenuType.CATALOG.getValue()){//目录
				entity.setList(getMenuTreeList(queryListParentId(entity.getMenuId(), menuIdList), menuIdList));
			}
			subMenuList.add(entity);
		}
		
		return subMenuList;
	}

	public void saveOrUpdate_R_M(Object roleId, List<Long> menuIdList,String paramExt) {
		//先删除角色与菜单关系
		sysRoleMenuDao.delete(roleId);
		if(menuIdList.isEmpty()){
			return ;
		}

		//保存角色与菜单关系
		Map<String, Object> map = new HashMap<>();
		map.put("roleId", roleId);
		map.put("menuIdList", menuIdList);
		sysRoleMenuDao.save(map);
		if(StringUtils.isNotBlank(paramExt)){
			JSONArray array = JSONArray.parseArray(paramExt);
	    	 for (@SuppressWarnings("rawtypes")
			Iterator iterator = array.iterator(); iterator.hasNext();) { 
	    		 JSONObject j = (JSONObject) iterator.next();             
	    		 Map<String, Object> m = new HashMap<>();
	    		 m.put("roleId", roleId);
	    		 m.put("menuId", j.getLong("menuId"));
	    		 JSONObject params= new JSONObject();
	    		 params.put("pagekey", "setcol");
	    		 JSONArray pagevule=JSONArray.parseArray(j.getString("pagevule"));
	    		 params.put("pagevule", pagevule);
	    		 m.put("params", params.toString());           
	             sysRoleMenuDao.update(m);
	    	 } 
		}
		

	}

	@Transactional(readOnly = true)
	public List<Long> queryMenuIdList(Object roleId) {
		return sysRoleMenuDao.queryMenuIdList(roleId);
	}

	@Transactional(readOnly = true)
	public JSONArray queryParamsList(Object roleId) {	
		JSONArray array = new JSONArray();
		List<SysRoleMenu> list = sysRoleMenuDao.queryParamsList(roleId);
		for(SysRoleMenu R_M :list){
			if(R_M.getParams()!=null){
				JSONObject jsonObject = JSONObject.parseObject(R_M.getParams());
				JSONObject j= new JSONObject();
				j.put("menuId", R_M.getMenuId());
				j.put("pagevule", jsonObject.get("pagevule"));
				array.add(j);
			}
			
		}
		return array;
	}

	@Override
	public JSONObject queryParamsByUserAndPerm(Object userId, String perms) {
		Map<String, Object> map =new HashMap<>();
		map.put("userId", userId);
		map.put("perms", perms);		
		String params = sysMenuDao.queryParamsByUserAndPerm(map);
		return JSONObject.parseObject(params);
	}
	

}
