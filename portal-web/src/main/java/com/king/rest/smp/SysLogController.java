package com.king.rest.smp;


import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.king.api.smp.SysLogService;
import com.king.common.utils.PageUtils;
import com.king.common.utils.Query;
import com.king.common.utils.R;
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
	@ApiOperation(value = "日志列表")
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("sys:log:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
		Query query = new Query(params);
		List<SysLog> sysLogList = sysLogService.queryList(query);
		int total = sysLogService.queryTotal(query);
		
		PageUtils pageUtil = new PageUtils(sysLogList, total, query.getLimit(), query.getPage());
		
		return R.ok().put("page", pageUtil);
	}
	
}
