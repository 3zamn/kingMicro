package com.king.rest.smp;


import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.king.api.smp.SysRoleService;
import com.king.api.smp.SysUserService;
import com.king.common.annotation.Log;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Page;
import com.king.common.utils.Query;
import com.king.common.utils.validator.Assert;
import com.king.common.utils.validator.ValidatorUtils;
import com.king.common.utils.validator.group.AddGroup;
import com.king.common.utils.validator.group.UpdateGroup;
import com.king.dal.gen.model.smp.SysUser;
import com.king.utils.TokenHolder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 系统用户
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@RestController
@Api(value = "用户管理", description = "用户管理")
@RequestMapping("/sys/user")
public class SysUserController extends AbstractController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysRoleService sysRoleService;

	
	/**
	 * 所有用户列表
	 */
	@ApiOperation(value = "用户列表", notes = "权限编码（sys:user:list）")
	@GetMapping("/list")
	@RequiresPermissions("sys:user:list")
	public JsonResponse list(@RequestParam Map<String, Object> params){
		//查询列表数据
		Query query = new Query(params,SysUser.class.getSimpleName());
		Page page = sysUserService.getPage(query);
		
		return JsonResponse.success(page);
	}
	
	/**
	 * 获取登录的用户信息
	 */
	@ApiOperation(value = "登录信息")
	@GetMapping("/info")
	public JsonResponse info(){
		return JsonResponse.success(getUser());
	}
	
	/**
	 * 修改登录用户密码
	 */
	@Log("修改密码")
	@ApiOperation(value = "修改密码")
	@PostMapping("/password")
	public JsonResponse password(String password, String newPassword){
		Assert.isBlank(newPassword, "新密码不为能空");

		//原密码
		password = new Sha256Hash(password, getUser().getSalt()).toHex();
		//新密码
		newPassword = new Sha256Hash(newPassword, getUser().getSalt()).toHex();
				
		//更新密码
		int count = sysUserService.updatePassword(getUserId(), password, newPassword);
		if(count == 0){
			return JsonResponse.error("原密码不正确");
		}
		
		return JsonResponse.success();
	}
	
	/**
	 * 用户信息
	 */
	@ApiOperation(value = "用户信息", notes = "权限编码（sys:user:info）")
	@GetMapping("/info/{userId}")
	@RequiresPermissions("sys:user:info")
	public JsonResponse info(@PathVariable("userId") Long userId){
		SysUser user = sysUserService.queryObject(userId);
		
		//获取用户所属的角色列表
		List<Long> roleIdList = sysRoleService.queryRoleIdList(userId);
		user.setRoleIdList(roleIdList);
		
		return JsonResponse.success(user);
	}
	
	/**
	 * 保存用户
	 */
	@Log("保存用户")
	@ApiOperation(value = "保存用户", notes = "权限编码（sys:user:save）")
	@PostMapping("/save")
	@RequiresPermissions("sys:user:save")
	public JsonResponse save(@RequestBody SysUser user){
		ValidatorUtils.validateEntity(user, AddGroup.class);
		
		sysUserService.save(user);
		
		return JsonResponse.success();
	}
	
	/**
	 * 修改用户
	 */
	@Log("修改用户")
	@ApiOperation(value = "修改用户", notes = "权限编码（sys:user:update）")
	@PostMapping("/update")
	@RequiresPermissions("sys:user:update")
	public JsonResponse update(@RequestBody SysUser user){
		ValidatorUtils.validateEntity(user, UpdateGroup.class);
		user.setToken(TokenHolder.token.get());
		sysUserService.update(user);
		
		return JsonResponse.success();
	}
	
	/**
	 * 删除用户
	 */
	@Log("删除用户")
	@ApiOperation(value = "删除用户", notes = "权限编码（sys:user:delete）")
	@PostMapping("/delete")
	@RequiresPermissions("sys:user:delete")
	public JsonResponse delete(@RequestBody Long[] userIds){
		if(ArrayUtils.contains(userIds, 1L)){
			return JsonResponse.error("系统管理员不能删除");
		}
		
		if(ArrayUtils.contains(userIds, getUserId())){
			return JsonResponse.error("当前用户不能删除");
		}
		
		sysUserService.deleteBatch(userIds);
		
		return JsonResponse.success();
	}
}
