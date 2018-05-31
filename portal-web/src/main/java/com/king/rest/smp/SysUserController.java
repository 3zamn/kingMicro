package com.king.rest.smp;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
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
import com.king.common.utils.constant.Constant;
import com.king.common.utils.redis.IdGenerator;
import com.king.common.utils.validator.Assert;
import com.king.common.utils.validator.ValidatorUtils;
import com.king.common.utils.validator.group.AddGroup;
import com.king.common.utils.validator.group.UpdateGroup;
import com.king.dal.gen.controller.AbstractController;
import com.king.dal.gen.model.Response;
import com.king.dal.gen.model.smp.SysUser;
import com.king.utils.TokenHolder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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
	private IdGenerator idGenerator;

	/**
	 * Id生成测试
	 * @return
	 */
	@GetMapping("/test")
	public JsonResponse test(){
		int clientTotal = 100000;
		// 同时并发执行的线程数
		int threadTotal = 600;
		 ExecutorService executorService = Executors.newCachedThreadPool();
		    //信号量，此处用于控制并发的线程数
		    final Semaphore semaphore = new Semaphore(threadTotal);
		    //闭锁，可实现计数器递减
		    final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
		    Long begin = new Date().getTime();  
		    for (int i = 0; i < clientTotal ; i++) {
		      executorService.execute(() -> {
		        try {//执行此方法用于获取执行许可，当总计未释放的许可数不超过60000时，	         	 
		          semaphore.acquire(); //允许通行，否则线程阻塞等待，直到获取到许可。
		          idGenerator.incrementHash("id", "value", null);         
		          semaphore.release(); //释放许可
		        } catch (Exception e) {
		          e.printStackTrace();
		        }       
		        countDownLatch.countDown(); //闭锁减一
		      });
		    }
		    try {
				countDownLatch.await();//线程阻塞，直到闭锁值为0时，阻塞才释放，继续往下执行
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		    executorService.shutdown();
		    Long end = new Date().getTime();  
		    System.out.println("cast : " + (end - begin) / 1000 + " ms");  
			return JsonResponse.success("cast : " + (end - begin) / 1000 + " ms");
		
	}

	/**
	 * 所有用户列表
	 */
	@Log("查看用户列表")
	@ApiOperation(value = "用户列表",response=Response.class, notes = "权限编码（sys:user:list）")
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
	public JsonResponse info(@PathVariable("userId") Object userId){
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
	public JsonResponse save(@RequestBody SysUser user){
		ValidatorUtils.validateEntity(user, AddGroup.class);
		
		sysUserService.save(user);
		
		return JsonResponse.success();
	}
	
	/**
	 * 修改用户
	 */
	@Log("修改用户")
	@ApiOperation(value = "修改用户",response=Response.class, notes = "权限编码（sys:user:update）")
	@PostMapping("/update")
	@RequiresPermissions("sys:user:update")
	public JsonResponse update(@RequestBody SysUser user){
		ValidatorUtils.validateEntity(user, UpdateGroup.class);
		if(user.getUserId() == Constant.SUPER_ADMIN){
			if(user.getStatus()==0){
				return JsonResponse.error("系统管理员不能禁用!");
			}
		}
		user.setToken(TokenHolder.token.get());
		sysUserService.update(user);
		
		return JsonResponse.success();
	}
	
	/**
	 * 删除用户
	 */
	@Log("删除用户")
	@ApiOperation(value = "删除用户",response=Response.class, notes = "权限编码（sys:user:delete）")
	@PostMapping("/delete")
	@RequiresPermissions("sys:user:delete")
	public JsonResponse delete(@RequestBody Object[] userIds){
		if(ArrayUtils.contains(userIds, 1L)){
			return JsonResponse.error("系统管理员不能删除!");
		}
		
		if(ArrayUtils.contains(userIds, getUserId())){
			return JsonResponse.error("当前用户不能删除!");
		}
		
		sysUserService.deleteBatch(userIds);
		
		return JsonResponse.success();
	}
}
