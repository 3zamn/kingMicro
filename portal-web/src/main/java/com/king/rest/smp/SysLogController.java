package com.king.rest.smp;


import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.king.api.smp.SysLogService;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Page;
import com.king.common.utils.Query;
import com.king.dal.gen.model.smp.SysLog;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 *  系统日志
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@Controller
@Api(value = "系统日志", description = "系统日志")
@RequestMapping("/sys/log")
public class SysLogController {
	@Autowired
	private SysLogService sysLogService;
	
	/**
	 * 日志列表
	 */
	@ApiOperation(value = "日志列表", notes = "权限编码（sys:log:list）")
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("sys:log:list")
	public JsonResponse list(@RequestParam Map<String, Object> params){
		//查询列表数据
		Query query = new Query(params,SysLog.class.getSimpleName());
		Page page = sysLogService.getPage(query);	
		return JsonResponse.success().put("page", page);
	}
	
}
