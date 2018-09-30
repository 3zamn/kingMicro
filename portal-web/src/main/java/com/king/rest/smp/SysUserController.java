package com.king.rest.smp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.king.common.annotation.DuplicateFilter;
import com.king.common.annotation.Log;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Page;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.pattern.StringToolkit;
import com.king.common.utils.redis.RedisKeys;
import com.king.common.utils.redis.RedisUtils;
import com.king.common.utils.validator.Assert;
import com.king.common.utils.validator.ValidatorUtils;
import com.king.common.utils.validator.group.AddGroup;
import com.king.common.utils.validator.group.UpdateGroup;
import com.king.dal.gen.model.Response;
import com.king.dal.gen.model.smp.SysUser;
import com.king.dal.gen.model.smp.SysUserToken;
import com.king.utils.AbstractController;
import com.king.utils.Query;
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
	@Autowired
	private RedisUtils redisUtils;

	
	/**
	 * 所有用户列表
	 */
	@ApiOperation(value = "用户列表",response=Response.class, notes = "权限编码（sys:user:list）")
	@GetMapping("/list")
	@RequiresPermissions("sys:user:list")
	public JsonResponse list(@RequestParam Map<String, Object> params){
		Query query = new Query(params,SysUser.class);
		Page page = sysUserService.getPage(query);
		return JsonResponse.success(page);
	}
	
	/**
	 * 在线用户列表
	 * 在线用户不多、暂时内存中分页
	 */
	@Log(" 当前在线用户列表")
	@ApiOperation(value = " 当前在线用户列表",response=Response.class, notes = "权限编码（sys:users:online）")
	@GetMapping("/online")
	@RequiresPermissions("sys:users:online")
	public JsonResponse online(@RequestParam Map<String, Object> params){
		//查询在线用户列表数据
		Set<String> token = redisUtils.likeKey("token");
		Iterator<String> its = token.iterator();
		List<SysUserToken> list = new ArrayList<SysUserToken>();
		while (its.hasNext()) {		
			list.add(redisUtils.get(its.next(), SysUserToken.class));
      	}
		int totalCount= list.size();
		int pageSize= Integer.parseInt(StringToolkit.getObjectString(params.get("limit")!=null?params.get("limit"):1));
		int currPage= Integer.parseInt(StringToolkit.getObjectString(params.get("page")!=null?params.get("page"):1));
		Page page = new Page(list, totalCount, pageSize, currPage);
		return JsonResponse.success(page);
	}
	
	/**
	 * 注销在线用户
	 *
	 */
	@Log(" 注销在线用户")
	@ApiOperation(value = " 注销在线用户",response=Response.class, notes = "权限编码（sys:users:offline）")
	@PostMapping("/offline")
	@RequiresPermissions("sys:users:offline")
	public JsonResponse offline(@RequestBody(required = false) String[] tokens){
		String t =getUser().getToken();
		if(ArrayUtils.contains(tokens, t)){
			return JsonResponse.error("当前用户不能注销!");
		}
		if(getUser().getUserId() == Constant.SUPER_ADMIN){
			return JsonResponse.error("系统管理员不能注销!");
		}
		for(String token:tokens){
			redisUtils.delete(RedisKeys.getTokenKey(token));
		}
		return JsonResponse.success();
	}
	
	/**
	 * 获取登录的用户信息
	 */
	@Log("当前登录信息")
	@ApiOperation(value = "登录信息",response=Response.class)
	@GetMapping("/info")
	public JsonResponse info(){
		return JsonResponse.success(getUser());
	}
	
	/**
	 * 修改登录用户密码
	 */
	@Log("修改密码")
	@ApiOperation(value = "修改密码",response=Response.class)
	@RequiresPermissions("sys:user:password")
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
	@Log("用户信息详情")
	@ApiOperation(value = "用户信息",response=Response.class, notes = "权限编码（sys:user:info）")
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
	@ApiOperation(value = "保存用户",response=Response.class, notes = "权限编码（sys:user:save）")
	@PostMapping("/save")
	@RequiresPermissions("sys:user:save")
	@DuplicateFilter(check=true)
	public JsonResponse save(@RequestBody(required = false) SysUser user){
		ValidatorUtils.validateEntity(user, AddGroup.class);	
		sysUserService.save(user);	
		return JsonResponse.success();
	}
	
	/**
	 * 修改用户
	 */
	@Log(value="修改用户",update=true,serviceClass=SysUserService.class)
	@ApiOperation(value = "修改用户",response=Response.class, notes = "权限编码（sys:user:update）")
	@PostMapping("/update")
	@RequiresPermissions("sys:user:update")
	public JsonResponse update(@RequestBody(required = false) SysUser user){
		ValidatorUtils.validateEntity(user, UpdateGroup.class);
		if(user.getUserId() == Constant.SUPER_ADMIN){
			//return JsonResponse.error("演示环境不能修改系统管理员!");
			if(user.getStatus()==false){
				return JsonResponse.error("系统管理员不能禁用!");
			}
		}
		user.setSalt(sysUserService.queryObject(user.getUserId()).getSalt());
		user.setToken(getUser().getToken());
		sysUserService.update(user);	
		return JsonResponse.success();
	}
	
	/**
	 * 删除用户
	 */
	@Log(value="删除用户",delete=true,serviceClass=SysUserService.class)
	@ApiOperation(value = "删除用户",response=Response.class, notes = "权限编码（sys:user:delete）")
	@PostMapping("/delete")
	@RequiresPermissions("sys:user:delete")
	public JsonResponse delete(@RequestBody(required = false) Long[] userIds){
		if(ArrayUtils.contains(userIds, 1L)){
			return JsonResponse.error("系统管理员不能删除!");
		}	
		if(ArrayUtils.contains(userIds, getUserId())){
			return JsonResponse.error("当前用户不能删除!");
		}	
		sysUserService.deleteBatch(userIds);		
		return JsonResponse.success();
	}
	
	/**
	 * 根据roleid查询用户列表
	 */
	@ApiOperation(value = "角色授权用户列表",response=Response.class, notes = "权限编码（sys:users:role）")
	@GetMapping("/list/{roleId}")
	@RequiresPermissions("sys:users:role")
	public JsonResponse usersByRole(@PathVariable("roleId") Object roleId,@RequestParam Map<String, Object> params){
		params.put("roleId", roleId.equals("null")==true?null:roleId);
		Query query = new Query(params,SysUser.class);
		Page page = null;
		page	=sysUserService.getPage(query);
		return JsonResponse.success(page);
	}
	
	
}
