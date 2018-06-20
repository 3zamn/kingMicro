package com.king.rest.smp;


import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.king.api.smp.SysLogService;
import com.king.common.mongodb.log.model.ExceptionLogVO;
import com.king.common.mongodb.log.repo.ExceptionLogRepo;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Page;
import com.king.common.utils.pattern.StringToolkit;
import com.king.dal.gen.model.Response;
import com.king.dal.gen.model.smp.SysLog;
import com.king.utils.Query;

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
	@Autowired
	private ExceptionLogRepo exceptionLogRepo;
	
	/**
	 * 日志列表
	 */
	@ApiOperation(value = "日志列表",response=Response.class, notes = "权限编码（sys:log:list）")
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("sys:log:list")
	public JsonResponse list(@RequestParam Map<String, Object> params){
		//查询列表数据
		Query query = new Query(params,SysLog.class.getSimpleName());
		Page page = sysLogService.getPage(query);	
		return JsonResponse.success(page);
	}
	
	/**
	 * 系统异常列表
	 */
	@ApiOperation(value = "系统异常列表",response=Response.class, notes = "权限编码（sys:exception:list）")
	@ResponseBody
	@GetMapping("/exception")
	@RequiresPermissions("sys:exception:list")
	public JsonResponse exceptionList(@RequestParam Map<String, Object> params){
		int pageSize= Integer.parseInt(StringToolkit.getObjectString(params.get("limit")));
		int currPage= Integer.parseInt(StringToolkit.getObjectString(params.get("page")));
		org.springframework.data.mongodb.core.query.Query  query= new org.springframework.data.mongodb.core.query.Query();
		Sort sort = new Sort(Direction.DESC, "createTime");
		query.with(sort);	
		String seriaNO= StringToolkit.getObjectString(params.get("seriaNo"));
		if(StringUtils.isNotBlank(seriaNO)){//异常流水号精确查询
			query.addCriteria(Criteria.where("seriaNo").is(seriaNO));
		}	
		com.king.common.mongodb.mongo.Page pageable= new com.king.common.mongodb.mongo.Page();
		pageable.setCurrPage(currPage);
		pageable.setPageSize(pageSize);			
		pageable.setSort(sort);
		pageable.setOffset((currPage-1)*pageSize);
		org.springframework.data.domain.Page<ExceptionLogVO> exceptionLogVO=exceptionLogRepo.findAll(query,pageable);
		com.king.common.utils.Page   page = new Page(exceptionLogVO.getContent(), (int)exceptionLogVO.getTotalElements(), exceptionLogVO.getSize(), exceptionLogVO.getNumber());
		return JsonResponse.success(page);
	}
	
	/**
	 * 系统异常详细
	 */
	@ApiOperation(value = "系统异常详细",response=Response.class, notes = "权限编码（sys:exception:detail）")
	@ResponseBody
	@GetMapping("/exceptionDetail/{id}")
	@RequiresPermissions("sys:exception:detail")
	public JsonResponse exceptionDetail(@PathVariable("id") Object id){
		ExceptionLogVO ExceptionLogVO=exceptionLogRepo.findOne(StringToolkit.getObjectString(id));		
		return JsonResponse.success(ExceptionLogVO);
	}
	
}
