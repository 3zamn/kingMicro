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

import com.king.api.smp.ScheduleJobService;
import com.king.common.annotation.Log;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Page;
import com.king.common.utils.Query;
import com.king.common.utils.validator.ValidatorUtils;
import com.king.dal.gen.model.Response;
import com.king.dal.gen.model.smp.ScheduleJob;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * 定时任务
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@RestController
@Api(value = "定时任务", description = "定时任务")
@RequestMapping("/sys/schedule")
public class ScheduleJobController {
	@Autowired
	private ScheduleJobService scheduleJobService;
	
	/**
	 * 定时任务列表
	 */
	@ApiOperation(value = "定时任务列表", notes = "权限编码（sys:schedule:list）")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success",response=Response.class)})
	@GetMapping("/list")
	@RequiresPermissions("sys:schedule:list")
	public JsonResponse list(@ApiParam(name="params",value="query string",required=true)@RequestParam Map<String, Object> params){
		//查询列表数据
		Query query = new Query(params,ScheduleJob.class.getSimpleName());
		Page page = scheduleJobService.getPage(query);
		return JsonResponse.success(page);
	}
	
	/**
	 * 定时任务信息
	 */
	@Log("查看定时任务详情")
	@ApiOperation(value = "定时任务详情", notes = "权限编码（sys:schedule:info）")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success",response=Response.class)})
	@GetMapping("/info/{jobId}")
	@RequiresPermissions("sys:schedule:info")
	public JsonResponse info(@PathVariable("jobId") Object jobId){
		ScheduleJob schedule = scheduleJobService.queryObject(jobId);
		
		return JsonResponse.success(schedule);
	}
	
	/**
	 * 保存定时任务
	 */
	@Log("保存定时任务")
	@ApiOperation(value = "保存定时任务", notes = "权限编码（sys:schedule:save）")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success",response=Response.class)})
	@PostMapping("/save")
	@RequiresPermissions("sys:schedule:save")
	public JsonResponse save(@RequestBody ScheduleJob scheduleJob){
		ValidatorUtils.validateEntity(scheduleJob);
		
		scheduleJobService.save(scheduleJob);
		
		return JsonResponse.success();
	}
	
	/**
	 * 修改定时任务
	 */
	@Log("修改定时任务")
	@ApiOperation(value = "修改定时任务", notes = "权限编码（sys:schedule:update）")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success",response=Response.class)})
	@PostMapping("/update")
	@RequiresPermissions("sys:schedule:update")
	public JsonResponse update(@RequestBody ScheduleJob scheduleJob){
		ValidatorUtils.validateEntity(scheduleJob);
				
		scheduleJobService.update(scheduleJob);
		
		return JsonResponse.success();
	}
	
	/**
	 * 删除定时任务
	 */
	@Log("删除定时任务")
	@ApiOperation(value = "删除定时任务", notes = "权限编码（sys:schedule:delete）")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success",response=Response.class)})
	@PostMapping("/delete")
	@RequiresPermissions("sys:schedule:delete")
	public JsonResponse delete(@ApiParam(name="jobIds",value="定时任务Id",required=true) @RequestBody Object[] jobIds){
		scheduleJobService.deleteBatch(jobIds);
		
		return JsonResponse.success();
	}
	
	/**
	 * 立即执行任务
	 */
	@Log("立即执行任务")
	@ApiOperation(value = "立即执行任务", notes = "权限编码（sys:schedule:run）")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success",response=Response.class)})
	@PostMapping("/run")
	@RequiresPermissions("sys:schedule:run")
	public JsonResponse run(@ApiParam(name="jobIds",value="定时任务Id",required=true) @RequestBody Object[] jobIds){
		scheduleJobService.run(jobIds);
		
		return JsonResponse.success();
	}
	
	/**
	 * 暂停定时任务
	 */
	@Log("暂停定时任务")
	@ApiOperation(value = "暂停执行任务", notes = "权限编码（sys:schedule:pause）")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success",response=Response.class)})
	@PostMapping("/pause")
	@RequiresPermissions("sys:schedule:pause")
	public JsonResponse pause(@ApiParam(name="jobIds",value="定时任务Id",required=true) @RequestBody Object[] jobIds){
		scheduleJobService.pause(jobIds);
		
		return JsonResponse.success();
	}
	
	/**
	 * 恢复定时任务
	 */
	@Log("恢复定时任务")
	@ApiOperation(value = "恢复定时任务", notes = "权限编码（sys:schedule:resume）")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success",response=Response.class)})
	@PostMapping("/resume")
	@RequiresPermissions("sys:schedule:resume")
	public JsonResponse resume(@ApiParam(name="jobIds",value="定时任务Id",required=true) @RequestBody Object[] jobIds){
		scheduleJobService.resume(jobIds);
		
		return JsonResponse.success();
	}

}
