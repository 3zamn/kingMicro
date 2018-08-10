package com.king.rest.smp;


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

import com.king.api.smp.SysConfigService;
import com.king.api.smp.SysUserService;
import com.king.common.annotation.Log;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Page;
import com.king.common.utils.validator.ValidatorUtils;
import com.king.dal.gen.model.Response;
import com.king.dal.gen.model.smp.SysConfig;
import com.king.utils.AbstractController;
import com.king.utils.Query;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * 系统配置信息
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@RestController
@Api(value = "系统配置", description = "系统配置")
@RequestMapping("/sys/config")
public class SysConfigController extends AbstractController {
	@Autowired
	private SysConfigService sysConfigService;
	
	/**
	 * 所有配置列表
	 */
	@Log("配置列表")
	@ApiOperation(value = "配置列表",response=Response.class, notes = "权限编码（sys:config:list）")
	@GetMapping("/list")
	@RequiresPermissions("sys:config:list")
	public JsonResponse list(@RequestParam Map<String, Object> params){
		//查询列表数据
		Query query = new Query(params,SysConfig.class.getSimpleName());
		Page page = sysConfigService.getPage(query);	
		return JsonResponse.success(page);
	}
	
	
	/**
	 * 配置信息
	 */
	@Log("配置信息")
	@ApiOperation(value = "配置信息",response=Response.class, notes = "权限编码（sys:config:info）")
	@GetMapping("/info/{id}")
	@RequiresPermissions("sys:config:info")
	public JsonResponse info(@PathVariable("id") Object id){
		SysConfig config = sysConfigService.queryObject(id);
		
		return JsonResponse.success(config);
	}
	
	/**
	 * 保存配置
	 */
	@Log("保存配置")
	@ApiOperation(value = "保存配置",response=Response.class, notes = "权限编码（sys:config:save）")
	@PostMapping("/save")
	@RequiresPermissions("sys:config:save")
	public JsonResponse save(@RequestBody SysConfig config){
		ValidatorUtils.validateEntity(config);

		sysConfigService.save(config);
		
		return JsonResponse.success();
	}
	
	/**
	 * 修改配置
	 */
	@Log(value ="修改配置",update=true,serviceClass=SysConfigService.class)
	@ApiOperation(value = "修改配置",response=Response.class, notes = "权限编码（sys:config:update）")
	@PostMapping("/update")
	@RequiresPermissions("sys:config:update")
	public JsonResponse update(@RequestBody SysConfig config){
		ValidatorUtils.validateEntity(config);
		
		sysConfigService.update(config);
		
		return JsonResponse.success();
	}
	
	/**
	 * 删除配置
	 */
	@Log(value ="删除配置",delete=true,serviceClass=SysConfigService.class)
	@ApiOperation(value = "删除配置",response=Response.class, notes = "权限编码（sys:config:delete）")
	@PostMapping("/delete")
	@RequiresPermissions("sys:config:delete")
	public JsonResponse delete(@RequestBody Long[] ids){
		sysConfigService.deleteBatch(ids);
		
		return JsonResponse.success();
	}

}
