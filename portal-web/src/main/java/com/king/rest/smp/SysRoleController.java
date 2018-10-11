package com.king.rest.smp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.king.api.smp.SysDeptService;
import com.king.api.smp.SysMenuService;
import com.king.api.smp.SysRoleService;
import com.king.api.smp.SysUserService;
import com.king.common.annotation.DuplicateFilter;
import com.king.common.annotation.Log;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Page;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.validator.ValidatorUtils;
import com.king.dal.gen.model.Response;
import com.king.dal.gen.model.smp.SysRole;
import com.king.dal.gen.model.smp.SysUser;
import com.king.utils.AbstractController;
import com.king.utils.Query;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@RestController
@Api(value = "角色管理", description = "角色管理")
@RequestMapping("/sys/role")
public class SysRoleController extends AbstractController {
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private SysDeptService sysDeptService;
	@Autowired
	private SysUserService sysUserService;
	
	/**
	 * 角色列表
	 */
	@ApiOperation(value = "角色列表",response=Response.class, notes = "权限编码（sys:role:list）")
	@GetMapping("/list")
	@RequiresPermissions("sys:role:list")
	public JsonResponse list(@RequestParam Map<String, Object> params){
		//如果不是超级管理员，则只查询自己创建的角色列表
		if(getUserId() != Constant.SUPER_ADMIN){
			params.put("createUserId", getUserId());
		}	
		Query query = new Query(params,SysRole.class);
		Page page = sysRoleService.getPage(query);
		return JsonResponse.success(page);
	}
	
	/**
	 * 角色选择
	 */
	@ApiOperation(value = "角色选择",response=Response.class, notes = "权限编码（sys:role:select）")
	@GetMapping("/select")
	@RequiresPermissions("sys:role:select")
	public JsonResponse select(){
		Query query = new Query(new HashMap<String, Object>());
		List<SysRole> list = sysRoleService.queryList(query);	
		return JsonResponse.success(list);
	}
	
	/**
	 * 角色信息
	 */
	@ApiOperation(value = "角色信息",response=Response.class, notes = "权限编码（sys:role:info）")
	@GetMapping("/info/{roleId}")
	@RequiresPermissions("sys:role:info")
	public JsonResponse info(@PathVariable("roleId") Object roleId){
		SysRole role = sysRoleService.queryObject(roleId);	
		//查询角色对应的菜单
		List<Long> menuIdList = sysMenuService.queryMenuIdList(roleId);
		role.setMenuIdList(menuIdList);
		//查询角色对应的部门
		List<Long> deptIdList = sysDeptService.queryDeptIdListByRoleId(roleId);
		role.setDeptIdList(deptIdList);	
		JSONArray params=sysMenuService.queryParamsList(roleId);
		List<Long> userIdList = sysRoleService.queryUserIdList(roleId);
		role.setParamExt(params);
		role.setUserIdList(userIdList);
		return JsonResponse.success(role);
	}
	
	/**
	 * 保存角色
	 */
	@Log("保存角色")
	@ApiOperation(value = "保存角色",response=Response.class, notes = "权限编码（sys:role:save）")
	@PostMapping("/save")
	@RequiresPermissions("sys:role:save")
	@DuplicateFilter(check=true)
	public JsonResponse save(@RequestBody(required = false) SysRole role){
		ValidatorUtils.validateEntity(role);	
		sysRoleService.save(role);	
		return JsonResponse.success();
	}
	
	/**
	 * 修改角色
	 */
	@Log(value="修改角色",update=true,serviceClass=SysRoleService.class)
	@ApiOperation(value = "保存角色",response=Response.class, notes = "权限编码（sys:role:update）")
	@PostMapping("/update")
	@RequiresPermissions("sys:role:update")
	public JsonResponse update(@RequestBody(required = false) SysRole role){
		ValidatorUtils.validateEntity(role);	
		sysRoleService.update(role,getUser().getToken());	
		return JsonResponse.success();
	}
	
	/**
	 * 授权的用户
	 */
	@ApiOperation(value = "查询授权的用户",response=Response.class, notes = "权限编码（sys:role:grantUsers）")
	@PostMapping("/grantUsers")
	@RequiresPermissions("sys:role:grantUsers")
	public JsonResponse grantUsers(@RequestBody Long roleId){
		List<SysUser> sysUsers = sysUserService.queryByRoleId(roleId);
		return JsonResponse.success(sysUsers);
	}
	
	/**
	 * 删除角色
	 */
	@Log(value="删除角色",delete=true,serviceClass=SysRoleService.class)
	@ApiOperation(value = "删除角色",response=Response.class, notes = "权限编码（sys:role:delete）")
	@PostMapping("/delete")
	@RequiresPermissions("sys:role:delete")
	public JsonResponse delete(@RequestBody(required = false) Long[] roleIds){
		for(Object roleId:roleIds){
			List<Long> list=sysRoleService.queryUserIdList(roleId);
			if(list!=null && !list.isEmpty()){
				return JsonResponse.error("角色Id为:"+roleId+",已存在用户。请先删除用户!");
			}	
		}
		sysRoleService.deleteBatch(roleIds);	
		return JsonResponse.success();
	}
}
