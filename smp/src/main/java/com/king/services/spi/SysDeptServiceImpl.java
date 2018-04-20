package com.king.services.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.api.smp.SysDeptService;
import com.king.common.annotation.DataFilter;
import com.king.dal.gen.model.smp.SysDept;
import com.king.dal.gen.service.BaseServiceImpl;
import com.king.dao.SysDeptDao;
import com.king.dao.SysRoleDeptDao;



@Service("sysDeptService")
public class SysDeptServiceImpl extends BaseServiceImpl<SysDept> implements SysDeptService {
	@Autowired
	private SysDeptDao sysDeptDao;
	@Autowired
	private SysRoleDeptDao sysRoleDeptDao;
	
	@Override
	@Transactional(readOnly = true)
	@DataFilter(tableAlias = "d", user = false)
	public List<SysDept> queryList(Map<String, Object> map){
		return sysDeptDao.queryList(map);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Long> queryDetpIdList(Long parentId) {
		return sysDeptDao.queryDetpIdList(parentId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Long> queryDetpIdLists(List<Long> parentIds) {
		return sysRoleDeptDao.queryDetpIdLists(parentIds);
	}

	@Override
	@Transactional(readOnly = true)
	public String getSubDeptIdList(Long deptId){
		//部门及子部门ID列表
		List<Long> deptIdList = new ArrayList<>();

		//获取子部门ID
		List<Long> subIdList = queryDetpIdList(deptId);
		getDeptTreeList(subIdList, deptIdList);

		//添加本部门
		deptIdList.add(deptId);

		String deptFilter = StringUtils.join(deptIdList, ",");
		return deptFilter;
	}

	/**
	 * 递归
	 */
	@Transactional(readOnly = true)
	private void getDeptTreeList(List<Long> subIdList, List<Long> deptIdList){
		for(Long deptId : subIdList){
			List<Long> list = queryDetpIdList(deptId);
			if(list.size() > 0){
				getDeptTreeList(list, deptIdList);
			}

			deptIdList.add(deptId);
		}
	}
	
	@Override
	@Transactional
	public void saveOrUpdate_R_D(Long roleId, List<Long> deptIdList) {
		//先删除角色与菜单关系
		sysRoleDeptDao.delete(roleId);

		if(deptIdList.size() == 0){
			return ;
		}

		//保存角色与菜单关系
		Map<String, Object> map = new HashMap<>();
		map.put("roleId", roleId);
		map.put("deptIdList", deptIdList);
		sysRoleDeptDao.save(map);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> queryDeptIdList(Long roleId) {
		return sysRoleDeptDao.queryDeptIdList(roleId);
	}
}
