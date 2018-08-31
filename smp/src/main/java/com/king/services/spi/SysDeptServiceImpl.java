package com.king.services.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
	public List<Long> queryDeptIdList(Object parentId) {
		return sysDeptDao.queryDeptIdList(parentId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Long> queryDeptIdLists(List<Long> parentIds) {
		return sysRoleDeptDao.queryDeptIdLists(parentIds);
	}

	/**  
	 * 向下递归查询子节点及是否包括本节点
	 */
	@Override
	@Transactional(readOnly = true)
	public String getDownDeptIdList(Object deptId,Boolean include){
		//部门及子部门ID列表
		List<Long> deptIdList = new ArrayList<>();
		//获取子部门ID
		List<Long> subIdList = queryDeptIdList(deptId);
		getDeptTreeList(subIdList, deptIdList);
		//添加本部门
		if(include){
			deptIdList.add((Long)deptId);
		}
		
		return StringUtils.join(deptIdList, ",");
	}

	/**
	 * 递归
	 */
	@Transactional(readOnly = true)
	private void getDeptTreeList(List<Long> subIdList, List<Long> deptIdList){
		for(Long deptId : subIdList){
			List<Long> list = queryDeptIdList(deptId);
			if(!list.isEmpty()){
				getDeptTreeList(list, deptIdList);
			}
			deptIdList.add(deptId);
		}
	}
	
	@Override
	@Transactional
	public void saveOrUpdate_R_D(Object roleId, List<Long> deptIdList) {
		//先删除角色与菜单关系
		sysRoleDeptDao.delete(roleId);
		if(deptIdList.isEmpty()){
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
	public List<Long> queryDeptIdListByRoleId(Object roleId) {
		return sysRoleDeptDao.queryDeptIdList(roleId);
	}

	/**  
	 * 获取顶级节点下所有部门ID
	 */
	@Transactional(readOnly = true)
	public String getTopDeptIdList(Object deptId) {
		Long topId=getDeptTreeList(deptId, 0L);
		
		return getDownDeptIdList(topId, true);
	}
	
	/**
	 * 向上递归获取顶节点
	 */
	@Transactional(readOnly = true)
	public Long getDeptTreeList(Object deptId,Long topId){
		Long parentId =queryParentDeptId(deptId);
		if(parentId!=null){		
			if(parentId!=0L){
				topId=parentId;
				getDeptTreeList(parentId, topId);
			}	
		}
		return topId==0L?(Long)deptId:topId;
	}

	@Transactional(readOnly = true)
	public Long queryParentDeptId(Object deptId) {

		return sysDeptDao.queryParentDeptId(deptId);
	}
}
