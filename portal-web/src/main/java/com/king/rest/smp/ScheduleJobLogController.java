package com.king.rest.smp;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.king.api.smp.ScheduleJobLogService;
import com.king.common.utils.PageUtils;
import com.king.common.utils.Query;
import com.king.common.utils.R;
import com.king.dal.gen.model.smp.ScheduleJobLog;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 定时任务日志
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@RestController
@Api(value = "定时任务日志", description = "定时任务日志")
@RequestMapping("/sys/scheduleLog")
public class ScheduleJobLogController {
	@Autowired
	private ScheduleJobLogService scheduleJobLogService;
	
	/**
	 * 定时任务日志列表
	 */
	@ApiOperation(value = "定时任务日志列表")
	@GetMapping("/list")
	@RequiresPermissions("sys:schedule:log")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
		Query query = new Query(params);
		List<ScheduleJobLog> jobList = scheduleJobLogService.queryList(query);
		int total = scheduleJobLogService.queryTotal(query);
		
		PageUtils pageUtil = new PageUtils(jobList, total, query.getLimit(), query.getPage());
		
		return R.ok().put("page", pageUtil);
	}
	
	/**
	 * 定时任务日志信息
	 */
	@ApiOperation(value = "定时任务日志信息")
	@GetMapping("/info/{logId}")
	public R info(@PathVariable("logId") Long logId){
		ScheduleJobLog log = scheduleJobLogService.queryObject(logId);
		
		return R.ok().put("log", log);
	}
}
