package com.king.services.spi;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.king.api.smp.SysUserRoleService;
import com.king.dao.SysUserRoleDao;

/**
 *  用户与角色对应关系
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@Service("sysUserRoleService")
public class SysUserRoleServiceImpl implements SysUserRoleService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private SysUserRoleDao sysUserRoleDao;

	@Override
	public void saveOrUpdate(Long userId, List<Long> roleIdList) {
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
	public void delete(Long userId) {
		sysUserRoleDao.delete(userId);
	}
}
