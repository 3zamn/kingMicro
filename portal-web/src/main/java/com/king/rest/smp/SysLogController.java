package com.king.rest.smp;


import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.king.common.utils.Query;
import com.king.common.utils.pattern.StringToolkit;
import com.king.dal.gen.model.smp.SysLog;
import com.mongodb.DBObject;

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
	@ApiOperation(value = "日志列表", notes = "权限编码（sys:log:list）")
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
	@ApiOperation(value = "系统异常列表", notes = "权限编码（sys:exception:list）")
	@ResponseBody
	@GetMapping("/exception")
	@RequiresPermissions("sys:exception:list")
	public JsonResponse exceptionList(@RequestParam Map<String, Object> params){
		//查全部数据量大有可能内存溢出--因此限制只查最后10000条
		org.springframework.data.mongodb.core.query.Query  query= new org.springframework.data.mongodb.core.query.Query();
		Sort sort = new Sort(Direction.DESC, "createTime");
		query.limit(10000);
		query.with(sort);	
		String seriaNO= StringToolkit.getObjectString(params.get("seriaNo"));
		if(StringUtils.isNotBlank(seriaNO)){//异常流水号精确查询
			query.addCriteria(Criteria.where("seriaNo").is(seriaNO));
		}
		
		Iterable<ExceptionLogVO> exceptionLogVO=exceptionLogRepo.findAll(query);
		List<ExceptionLogVO> exceptionLog = Lists.newArrayList();	
		//使用jdk8新特性Lambda表达式
		exceptionLogVO.forEach(single ->{exceptionLog.add(single);});	
		int totalCount= exceptionLog.size();
		int pageSize= Integer.parseInt(StringToolkit.getObjectString(params.get("limit")));
		int currPage= Integer.parseInt(StringToolkit.getObjectString(params.get("page")));
		List<ExceptionLogVO> list = exceptionLog.subList((currPage-1)*pageSize, (currPage*pageSize)<=totalCount?currPage*pageSize:totalCount);
		Page page = new Page(list, totalCount, pageSize, currPage);
		return JsonResponse.success(page);
	}
	
	/**
	 * 系统异常详细
	 */
	@ApiOperation(value = "系统异常详细", notes = "权限编码（sys:exception:detail）")
	@ResponseBody
	@GetMapping("/exceptionDetail/{id}")
	@RequiresPermissions("sys:exception:detail")
	public JsonResponse exceptionDetail(@PathVariable("id") Object id){
		ExceptionLogVO ExceptionLogVO=exceptionLogRepo.findOne(StringToolkit.getObjectString(id));		
		return JsonResponse.success(ExceptionLogVO);
	}
	
}
