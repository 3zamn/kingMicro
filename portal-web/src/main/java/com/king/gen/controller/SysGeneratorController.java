package com.king.gen.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.king.common.annotation.Log;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Page;
import com.king.common.utils.Query;
import com.king.common.utils.date.DateUtils;
import com.king.dal.gen.model.smp.SysConfig;
import com.king.gen.service.SysGeneratorService;
import com.king.utils.XssHttpServletRequestWrapper;

import io.swagger.annotations.ApiOperation;

/**
 * 代码生成器
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年3月12日
 */
@RestController
@RequestMapping("/sys/generator")
public class SysGeneratorController {
	@Autowired
	private SysGeneratorService sysGeneratorService;
	
	/**
	 * 列表
	 */

	@GetMapping("/list")
	@RequiresPermissions("sys:generator:list")
	public JsonResponse list(@RequestParam Map<String, Object> params){
		//查询列表数据
		Query query = new Query(params);
		Page page = sysGeneratorService.getPage(query);
		return JsonResponse.success(page);
	}
	
	/**
	 * 配置信息
	 */

	@GetMapping("/info/{id}")
	@RequiresPermissions("sys:generator:info")
	public JsonResponse info(@PathVariable("id") String id){
		List<Map<String, String>> columns= sysGeneratorService.queryColumns(id);
	//	JSONArray array = JSONArray.parseArray(columns.toString());
		JSONArray jsonArray = new JSONArray();
		for(Map<String, String> column: columns){
			String jsonObject = JSONUtils.toJSONString(column);
			JSONObject object = JSONObject.parseObject(jsonObject);
			jsonArray.add(object);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("columns", jsonArray);
		return JsonResponse.success(jsonObject);
	}
	
	/**
	 * 生成代码
	 */
	@GetMapping("/code")
	@RequiresPermissions("sys:generator:code")
	public void code(HttpServletRequest request, HttpServletResponse response) throws IOException{
		//获取表名，不进行xss过滤
		HttpServletRequest orgRequest = XssHttpServletRequestWrapper.getOrgRequest(request);
		String tables = orgRequest.getParameter("tables");
		String[] tableNames = new Gson().fromJson(tables, String[].class);
		byte[] data = sysGeneratorService.generatorCode(tableNames);	
		StringBuffer filename = new StringBuffer("king");
		filename.append(DateUtils.getDefaultDateTimeSec());
		filename.append(".zip");
		response.reset();  
        response.setHeader("Content-Disposition", "attachment; filename="+"\""+filename+"\"");  
        response.addHeader("Content-Length", "" + data.length);  
        response.setContentType("application/octet-stream; charset=UTF-8");  
  
        IOUtils.write(data, response.getOutputStream());  
	}
}
