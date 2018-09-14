package com.king.rest.smp;


import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.king.common.mongodb.model.ExceptionLogVO;
import com.king.common.mongodb.model.SysLogVO;
import com.king.common.mongodb.repo.ExceptionLogRepo;
import com.king.common.mongodb.repo.SysLogRepo;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Page;
import com.king.common.utils.date.DateUtils;
import com.king.common.utils.pattern.StringToolkit;
import com.king.dal.gen.model.Response;

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
	private SysLogRepo sysLogRepo;
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
		int pageSize= Integer.parseInt(StringToolkit.getObjectString(params.get("limit")!=null?params.get("limit"):1));
		int currPage= Integer.parseInt(StringToolkit.getObjectString(params.get("page")!=null?params.get("page"):1));
		org.springframework.data.mongodb.core.query.Query  query= new org.springframework.data.mongodb.core.query.Query();
		Sort sort = new Sort(Direction.DESC, "createDate");
		query.with(sort);	
		 //多字段模糊查询
        if(params.get("keyParam")!=null && params.get("searchKey")!=null){
        	Pattern pattern = Pattern.compile("^.*"+StringToolkit.getObjectString(params.get("searchKey"))+".*$", Pattern.CASE_INSENSITIVE);
        	String[] keyParam =params.get("keyParam").toString().replace("[","").replace("]", "").replace("\"", "").split(",");
			if (keyParam != null && !params.get("searchKey").toString().trim().isEmpty()) {
				if (keyParam.length > 0) {
					Criteria[] criterias = new Criteria[keyParam.length];
					int i = 0;
					for (Object o : keyParam) {
						criterias[i] = Criteria.where(o.toString()).regex(pattern);
						i = i + 1;
					}
					Criteria criteria = new Criteria();
					criteria.orOperator(criterias);
					query.addCriteria(criteria);
				}
			}
        }
		String status= StringToolkit.getObjectString(params.get("status"));//根据状态精确查询
		if(StringUtils.isNotBlank(status)){//异常流水号精确查询
			query.addCriteria(Criteria.where("status").is(status));
		}
		JSONObject jsonObject= JSONObject.parseObject(StringToolkit.getObjectString(params.get("createDate")));//时间范围查询
		if(jsonObject!=null){
			if(StringUtils.isNotBlank(jsonObject.getString("begin"))&& StringUtils.isNotBlank(jsonObject.getString("end"))){
				String begin=jsonObject.getString("begin");
				Date beginTime=DateUtils.parse(begin, "yyyy-MM-dd HH:mm:ss");
				String end=jsonObject.getString("end");
				Date endTime=DateUtils.parse(end, "yyyy-MM-dd HH:mm:ss");
				query.addCriteria(Criteria.where("createDate").gte(beginTime).lte(endTime));
			}
		}	
		com.king.common.mongodb.mongo.Page pageable= new com.king.common.mongodb.mongo.Page();
		pageable.setCurrPage(currPage);
		pageable.setPageSize(pageSize);			
		pageable.setSort(sort);
		pageable.setOffset((currPage-1)*pageSize);
		org.springframework.data.domain.Page<SysLogVO> sysLogVO=sysLogRepo.findAll(query,pageable);
		com.king.common.utils.Page   page = new Page(sysLogVO.getContent(), (int)sysLogVO.getTotalElements(), sysLogVO.getSize(), sysLogVO.getNumber());
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
		int pageSize= Integer.parseInt(StringToolkit.getObjectString(params.get("limit")!=null?params.get("limit"):1));
		int currPage= Integer.parseInt(StringToolkit.getObjectString(params.get("page")!=null?params.get("page"):1));
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
