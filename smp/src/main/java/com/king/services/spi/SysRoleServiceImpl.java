package com.king.services.spi;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.api.smp.SysDeptService;
import com.king.api.smp.SysMenuService;
import com.king.api.smp.SysRoleService;
import com.king.common.annotation.DataFilter;
import com.king.dal.gen.model.smp.SysRole;
import com.king.dal.gen.service.BaseServiceImpl;
import com.king.dao.SysRoleDao;
import com.king.dao.SysUserRoleDao;


/**
 * 角色
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@Transactional
@Service("sysRoleService")
public class SysRoleServiceImpl extends BaseServiceImpl<SysRole> implements SysRoleService {
	@Autowired
	private SysRoleDao sysRoleDao;
	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private SysDeptService sysDeptService;
	@Autowired
	private SysUserRoleDao sysUserRoleDao;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	@DataFilter(tableAlias = "r", user = false)
	public List<SysRole> queryList(Map<String, Object> map) {
		return sysRoleDao.queryList(map);
	}

	@Override
	@DataFilter(tableAlias = "r", user = false)
	public int queryTotal(Map<String, Object> map) {
		return sysRoleDao.queryTotal(map);
	}

	@Override
	
	public void save(SysRole role) {
		role.setCreateTime(new Date());
		sysRoleDao.save(role);
		
		//保存角色与菜单关系
		sysMenuService.saveOrUpdate_R_M(role.getRoleId(), role.getMenuIdList());

		//保存角色与部门关系
		sysDeptService.saveOrUpdate_R_D(role.getRoleId(), role.getDeptIdList());
	}

	@Override
	public void update(SysRole role) {
		sysRoleDao.update(role);
		
		//更新角色与菜单关系
		sysMenuService.saveOrUpdate_R_M(role.getRoleId(), role.getMenuIdList());

		//保存角色与部门关系
		sysDeptService.saveOrUpdate_R_D(role.getRoleId(), role.getDeptIdList());
	}

	@Override
	public void saveOrUpdate_R_U(Long userId, List<Long> roleIdList) {
		try {
			if(roleIdList.size() == 0){
				return ;
			}
			
			//先删除用户与角色关系
			sysUserRoleDao.delete(userId);
			
			//保存用户与角色关系
			Map<String, Object> map = new HashMap<>();
			map.put("userId", userId);
			map.put("roleIdList", roleIdList);
			sysUserRoleDao.save(map);
			logger.info("角色修改成功");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}

	@Override
	public List<Long> queryRoleIdList(Long userId) {
		return sysUserRoleDao.queryRoleIdList(userId);
	}

	@Override
	public void delete_R_U(Long userId) {
		sysUserRoleDao.delete(userId);
	}

}
