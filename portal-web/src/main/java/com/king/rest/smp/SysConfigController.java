package com.king.rest.smp;


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

import com.king.api.smp.SysConfigService;
import com.king.common.annotation.Log;
import com.king.common.utils.PageUtils;
import com.king.common.utils.Query;
import com.king.common.utils.JsonResponse;
import com.king.common.validator.ValidatorUtils;
import com.king.dal.gen.model.smp.SysConfig;

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
	@ApiOperation(value = "配置列表")
	@GetMapping("/list")
	@RequiresPermissions("sys:config:list")
	public JsonResponse list(@RequestParam Map<String, Object> params){
		//查询列表数据
		Query query = new Query(params);
		List<SysConfig> configList = sysConfigService.queryList(query);
		int total = sysConfigService.queryTotal(query);
		
		PageUtils pageUtil = new PageUtils(configList, total, query.getLimit(), query.getPage());
		
		return JsonResponse.success().put("page", pageUtil);
	}
	
	
	/**
	 * 配置信息
	 */
	@ApiOperation(value = "配置信息")
	@GetMapping("/info/{id}")
	@RequiresPermissions("sys:config:info")
	public JsonResponse info(@PathVariable("id") Long id){
		SysConfig config = sysConfigService.queryObject(id);
		
		return JsonResponse.success().put("config", config);
	}
	
	/**
	 * 保存配置
	 */
	@Log("保存配置")
	@ApiOperation(value = "保存配置")
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
	@Log("修改配置")
	@ApiOperation(value = "修改配置")
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
	@Log("删除配置")
	@ApiOperation(value = "删除配置")
	@PostMapping("/delete")
	@RequiresPermissions("sys:config:delete")
	public JsonResponse delete(@RequestBody Long[] ids){
		sysConfigService.deleteBatch(ids);
		
		return JsonResponse.success();
	}

}
