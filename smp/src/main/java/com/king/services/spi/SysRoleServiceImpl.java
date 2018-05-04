package com.king.services.spi;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.api.smp.ShiroService;
import com.king.api.smp.SysDeptService;
import com.king.api.smp.SysMenuService;
import com.king.api.smp.SysRoleService;
import com.king.api.smp.SysUserService;
import com.king.common.annotation.DataFilter;
import com.king.common.utils.Page;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.redis.RedisKeys;
import com.king.common.utils.redis.RedisUtils;
import com.king.common.utils.spring.SpringContextUtils;
import com.king.dal.gen.model.smp.SysRole;
import com.king.dal.gen.model.smp.SysUser;
import com.king.dal.gen.model.smp.SysUserToken;
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
	@Autowired
	private ShiroService shiroService;
	@Autowired
	private SysUserService sysUserService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	
	@Transactional(readOnly = true)
	@DataFilter(tableAlias = "r", user = false)
	public List<SysRole> queryList(Map<String, Object> map) {
		return sysRoleDao.queryList(map);
	}

	@Transactional(readOnly = true)
	@DataFilter(tableAlias = "r", user = false)
	public Page getPage(Map<String, Object> map) {
		List<SysRole> list =sysRoleDao.queryList(map);
		int totalCount =sysRoleDao.queryTotal(map);
		Page page = new Page(list, totalCount, (int)map.get("limit"), (int)map.get("page"));	
		return page;
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
	public void update(SysRole role,String token) {
		sysRoleDao.update(role);
		
		//更新角色与菜单关系
		sysMenuService.saveOrUpdate_R_M(role.getRoleId(), role.getMenuIdList());

		//保存角色与部门关系
		sysDeptService.saveOrUpdate_R_D(role.getRoleId(), role.getDeptIdList());
		// 刷新权限缓存
		List<Long> userList = queryUserIdList(role.getRoleId());
		for (Long userId : userList) {
			String permKey = RedisKeys.getPermsKey(userId, token);
			RedisUtils redisUtils = SpringContextUtils.getBean(RedisUtils.class);
			String pattern = RedisKeys.getPermsKey(userId, "");
			Set<String> permKeys = redisUtils.likeKey(pattern);
			Iterator<String> its = permKeys.iterator();
			while (its.hasNext()) {
	    		permKey=its.next();
	    		redisUtils.delete(permKey);    	
	      	}
	    	Iterator<String> is = permKeys.iterator();  
	    	while (is.hasNext()) {
	    		permKey=is.next();
	    		Set<String> perms=shiroService.getUserPermissions(userId, false,token);
	        	Iterator<String> it = perms.iterator();  
	        	while (it.hasNext()) {  
	        	  redisUtils.sset(permKey, it.next(),Constant.TOKEN_EXPIRE/1000);
	        	} 
	      	}	
		}	
	}

	@Override
	public void saveOrUpdate_R_U(Long userId, List<Long> roleIdList) {
		try {
			/*if(roleIdList.size() == 0){
				return ;
			}*/
			
			//先删除用户与角色关系
			sysUserRoleDao.delete(userId);
			
			//保存用户与角色关系
			Map<String, Object> map = new HashMap<>();
			map.put("userId", userId);
			map.put("roleIdList", roleIdList);
			if(roleIdList.size()>0){
				sysUserRoleDao.save(map);
				logger.info("角色修改成功");
			}		
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}

	@Transactional(readOnly = true)
	public List<Long> queryRoleIdList(Long userId) {
		return sysUserRoleDao.queryRoleIdList(userId);
	}
	
	@Transactional(readOnly = true)
	public List<Long> queryUserIdList(Long roleId) {
		return sysUserRoleDao.queryUserIdList(roleId);
	}

	@Override
	public void delete_R_U(Long userId) {
		sysUserRoleDao.delete(userId);
	}

}
