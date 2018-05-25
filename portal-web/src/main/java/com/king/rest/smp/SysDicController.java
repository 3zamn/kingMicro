package com.king.rest.smp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.king.api.smp.SysDicService;
import com.king.common.annotation.Log;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Query;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.exception.RRException;
import com.king.dal.gen.controller.AbstractController;
import com.king.dal.gen.model.Response;
import com.king.dal.gen.model.smp.SysDic;
import com.king.dal.gen.model.smp.SysDicTerm;
import com.king.dal.gen.model.smp.SysUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * 数据字典明细表
 * 
 * @author king chen
 * @email 396885563@qq.com
 * @date 2018-05-08 17:26:32
 */
@RestController
@Api(value = "数据字典", description = "数据字典")
@RequestMapping("/sys/dic")
public class SysDicController extends AbstractController{
	@Autowired
	private SysDicService sysDicService;
	
	/**
	 * 列表
	 */
	@ApiOperation(value = "数据字典列表",notes = "权限编码（sysdic:list）")
	@GetMapping("/list")
	@RequiresPermissions("sys:dic:list")
	public List<SysDic> list(@RequestParam Map<String, Object> params){
		//查询列表数据
		List<SysDic> dics = null;
        Query query = new Query(params,SysDic.class.getSimpleName());
		dics = sysDicService.queryList(query);
		return dics;
	}

	/**
	 *  根据字典编码查询字典
	 * @param code
	 * @return
	 */
	@ApiOperation(value = "根据字典编码查询数据字典")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success",response=Response.class)})
	@GetMapping("/query/{code}")
	public JsonResponse query(@ApiParam(name="code",value="字典编码",required=true) @PathVariable("code") Object code){ 
		List<SysDicTerm> dics = sysDicService.queryDicTerm(code);
		return JsonResponse.success(dics);
	}
	
	/**
	 * 字典目录(添加、修改字典)
	 */
	@Log("字典目录")
	@ApiOperation(value = "字典目录选择", notes = "权限编码（sys:dic:select）")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success",response=Response.class)})
	@GetMapping("/select")
	@RequiresPermissions("sys:dic:select")
	public JsonResponse select(){
		//查询目录类型数据
		Map<String, Object> params = new HashMap<>();
		params.put("type", 0);
		params.put("enable", 1);
		 Query query = new Query(params,SysDic.class.getSimpleName());
		List<SysDic> menuList = sysDicService.queryList(query);
		
		//添加顶级菜单
		SysDic root = new SysDic();
		root.setId(0L);
		root.setName("一级菜单");
		root.setParentId(-1L);
		root.setOpen(true);
		menuList.add(root);
		return JsonResponse.success(menuList);
	}
	
	
	/**
	 * 信息
	 */
	@Log("数据字典信息")
    @ApiOperation(value = "查询信息",notes = "权限编码（sysdic:info）")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success",response=Response.class)})
	@GetMapping("/info/{id}")
	@RequiresPermissions("sys:dic:info")
	public JsonResponse info(@PathVariable("id") Object id){
		SysDic sysDic = sysDicService.queryObject(id);
		
		return JsonResponse.success(sysDic);
	}
	
	/**
	 * 保存
	 */
	@Log("数据字典保存")
	@ApiOperation(value = "保存",notes = "权限编码（sysdic:save）")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success",response=Response.class)})
	@PostMapping("/save")
	@RequiresPermissions("sys:dic:save")
	public JsonResponse save(@RequestBody SysDic sysDic){
		sysDic.setCreateBy(getUser().getUsername());
		sysDic.setCreateTime(new Date());
		verifyForm(sysDic);
		sysDicService.save(sysDic);
		
		return JsonResponse.success();
	}
	
	/**
	 * 修改
	 */
	@Log("数据字典修改")
	@ApiOperation(value = "修改",notes = "权限编码（sysdic:update）")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success",response=Response.class)})
	@PostMapping("/update")
	@RequiresPermissions("sys:dic:update")
	public JsonResponse update(@RequestBody SysDic sysDic){
		verifyForm(sysDic);
		sysDic.setUpdateBy(getUser().getUsername());
		sysDic.setUpdateTime(new Date());
		sysDicService.update(sysDic);
		
		return JsonResponse.success();
	}
	
	/**
	 * 删除
	 */
	@Log("数据字典删除")
	@ApiOperation(value = "删除",notes = "权限编码（sysdic:delete）")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success",response=Response.class)})
	@PostMapping("/delete")
	@RequiresPermissions("sys:dic:delete")
	public JsonResponse delete(Long id){
		
		List<SysDic> sysDic = sysDicService.queryParentList(id);
		if(sysDic.size() > 0){
			return JsonResponse.error("请先删除字典项再删目录");
		}
		sysDicService.deleteBatch(new Object[]{id});
		return JsonResponse.success();
	}
	
	/**
	 * 验证参数是否正确
	 */
	private void verifyForm(SysDic sysDic){
		if(sysDic.getType() == Constant.DicType.CATALOG.getValue()){//字典目录
			if(StringUtils.isBlank(sysDic.getName())){
				throw new RRException("字典名称不能为空");
			}
			if(StringUtils.isBlank(sysDic.getCode())){
				throw new RRException("字典编码不能为空");
			}
		}
		if(sysDic.getType() == Constant.DicType.TERM.getValue()){//字典项
			if(StringUtils.isBlank(sysDic.getValue())){
				throw new RRException("字典值不能为空");
			}
			if(StringUtils.isBlank(sysDic.getText())){
				throw new RRException("字典项不能为空");
			}
			if(StringUtils.isBlank(sysDic.getParentName())){
				throw new RRException("字典所在目录不能为空");
			}	
		}
			
		//上级目录类型
		int parentType = Constant.DicType.CATALOG.getValue();
		if(sysDic.getParentId() != 0){
			SysDic parentSysDic = sysDicService.queryObject(sysDic.getParentId());
			parentType = parentSysDic.getType();
		}
		
		//字典项
		if(sysDic.getType() == Constant.DicType.TERM.getValue()){
			if(parentType != Constant.DicType.CATALOG.getValue()){
				throw new RRException("上级目录只能为目录类型");
			}
			return ;
		}
	}
}
