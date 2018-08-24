package com.king.rest.smp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.king.api.smp.ScheduleJobService;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Page;
import com.king.common.utils.exception.RRException;
import com.king.common.utils.spring.SpringContextUtils;
import com.king.dal.gen.model.Response;
import com.king.dal.gen.model.smp.ScheduleJobLog;
import com.king.utils.Query;
import com.king.utils.excel.ExcelUtil;

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
	private ScheduleJobService scheduleJobService;
	/**
	 * 定时任务日志列表
	 */
	@ApiOperation(value = "定时任务日志列表",response=Response.class, notes = "权限编码（sys:schedule:log）")
	@GetMapping("/list")
	@RequiresPermissions("sys:schedule:log")
	public JsonResponse list(@RequestParam Map<String, Object> params){
		Query query = new Query(params,ScheduleJobLog.class.getSimpleName());	
		Page page = scheduleJobService.getPageScheduleJobLog(query);
		return JsonResponse.success(page);
	}
	
	/**
	 * 定时任务日志信息
	 */
	@ApiOperation(value = "定时任务日志信息",response=Response.class,notes = "权限编码（sys:scheduleLog:list）")
	@GetMapping("/info/{logId}")
	@RequiresPermissions("sys:scheduleLog:list")
	public JsonResponse info(@PathVariable("logId") Object logId){
		ScheduleJobLog log = scheduleJobService.queryScheduleJobLog(logId);
		
		return JsonResponse.success(log);
	}
	
	/**
	 * 导入
	 */
	@ApiOperation(value = "导入定时任务日志",response=Response.class,notes = "权限编码（sys:scheduleLog:upload）")
	@PostMapping("/upload")
	@RequiresPermissions("sys:scheduleLog:upload")
	public JsonResponse upload(@RequestParam(value="file",required=false) MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			throw new RRException("上传文件不能为空");
		}
		LinkedHashMap<Field, Object> map= new LinkedHashMap<>();//可自定义校验
		Method method=SpringContextUtils.getBean(ScheduleJobService.class).getClass().getMethod("saveBatch", List.class);
		ExcelUtil<ScheduleJobLog> upload = new ExcelUtil<>(ScheduleJobLog.class);
		JsonResponse result=upload.importExcel(1, file, map, ScheduleJobService.class, method);	
		
		return result;
	}
	
	/**
	 * 导出
	 */
	@ApiOperation(value = "导入定时任务日志",response=Response.class,notes = "权限编码（sys:scheduleLog:export）")
	@GetMapping("/export")
	@RequiresPermissions("sys:scheduleLog:export")
	public void exportExcel(@RequestParam Map<String, Object> params,HttpServletRequest request, HttpServletResponse response) throws Exception {
		Query query = new Query(params,ScheduleJobLog.class.getSimpleName());
		ExcelUtil<ScheduleJobLog> export = new ExcelUtil<>(ScheduleJobLog.class);
		Method method=SpringContextUtils.getBean(ScheduleJobService.class).getClass().getMethod("queryScheduleJobLogList", Map.class);
		export.exportExcel("定时任务日志", "定时任务日志", ScheduleJobService.class, method, query,response);
	}
	
}
