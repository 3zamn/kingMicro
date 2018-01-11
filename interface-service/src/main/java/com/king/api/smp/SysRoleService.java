package com.king.api.smp;

import java.util.List;
import java.util.Map;

import com.king.dal.gen.model.smp.SysRole;

/**
 * 角色
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
public interface SysRoleService {
	
	SysRole queryObject(Long roleId);
	
	List<SysRole> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(SysRole role);
	
	void update(SysRole role);
	
	void deleteBatch(Long[] roleIds);

}
