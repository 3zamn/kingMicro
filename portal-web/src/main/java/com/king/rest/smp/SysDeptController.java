package com.king.rest.smp;

import java.util.HashMap;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.king.api.smp.SysDeptService;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.Query;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.validator.ValidatorUtils;
import com.king.common.utils.validator.group.AddGroup;
import com.king.common.utils.validator.group.UpdateGroup;
import com.king.dal.gen.model.smp.SysDept;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * 部门管理
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@RestController
@Api(value = "部门管理", description = "部门管理")
@RequestMapping("/sys/dept")
public class SysDeptController extends AbstractController {
	@Autowired
	private SysDeptService sysDeptService;
	
	/**
	 * 列表
	 */
	@ApiOperation(value = "部门列表", notes = "权限编码（sys:dept:list）")
	@GetMapping("/list")
	@RequiresPermissions("sys:dept:list")
	public List<SysDept> list(){
		Query query = new Query(new HashMap<String, Object>());
		List<SysDept> deptList = sysDeptService.queryList(query);

		return deptList;
	}

	/**
	 * 选择部门(添加、修改菜单)
	 */
	@ApiOperation(value = "选择部门", notes = "权限编码（sys:dept:select）")
	@GetMapping("/select")
	@RequiresPermissions("sys:dept:select")
	public JsonResponse select(){
		Query query = new Query(new HashMap<String, Object>());
		List<SysDept> deptList = sysDeptService.queryList(query);

		//添加一级部门
		if(getUserId() == Constant.SUPER_ADMIN){
			SysDept root = new SysDept();
			root.setDeptId(0L);
			root.setName("一级部门");
			root.setParentId(-1L);
			root.setOpen(true);
			deptList.add(root);
		}

		return JsonResponse.success(deptList);
	}

	/**
	 * 上级部门Id(管理员则为0)
	 */
	@ApiOperation(value = "部门树列表", notes = "权限编码（sys:dept:list）")
	@GetMapping("/info")
	@RequiresPermissions("sys:dept:list")
	public JsonResponse info(){
		long deptId = 0;
		if(getUserId() != Constant.SUPER_ADMIN){
			SysDept dept = sysDeptService.queryObject(getDeptId());
			deptId = dept.getParentId();
		}

		return JsonResponse.success(deptId);
	}
	
	/**
	 * 信息
	 */
	@ApiOperation(value = "部门信息", notes = "权限编码（sys:dept:info）")
	@GetMapping("/info/{deptId}")
	@RequiresPermissions("sys:dept:info")
	public JsonResponse info(@PathVariable("deptId") Long deptId){
		SysDept dept = sysDeptService.queryObject(deptId);
		
		return JsonResponse.success(dept);
	}
	
	/**
	 * 保存
	 */
	@ApiOperation(value = "保存部门", notes = "权限编码（sys:dept:save）")
	@PostMapping("/save")
	@RequiresPermissions("sys:dept:save")
	public JsonResponse save(@RequestBody SysDept dept){
		ValidatorUtils.validateEntity(dept, AddGroup.class);
		sysDeptService.save(dept);
		
		return JsonResponse.success();
	}
	
	/**
	 * 修改
	 */
	@ApiOperation(value = "修改部门", notes = "权限编码（sys:dept:update）")
	@PostMapping("/update")
	@RequiresPermissions("sys:dept:update")
	public JsonResponse update(@RequestBody SysDept dept){
		ValidatorUtils.validateEntity(dept, UpdateGroup.class);
		sysDeptService.update(dept);
		
		return JsonResponse.success();
	}
	
	/**
	 * 删除
	 */
	@ApiOperation(value = "删除部门", notes = "权限编码（sys:dept:delete）")
	@PostMapping("/delete")
	@RequiresPermissions("sys:dept:delete")
	public JsonResponse delete(long deptId){
		//判断是否有子部门
		List<Long> deptList = sysDeptService.queryDetpIdList(deptId);
		if(deptList.size() > 0){
			return JsonResponse.error("请先删除子部门");
		}

		sysDeptService.delete(deptId);
		
		return JsonResponse.success();
	}
	
}
