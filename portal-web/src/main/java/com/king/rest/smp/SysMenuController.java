package com.king.rest.smp;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.king.api.smp.ShiroService;
import com.king.api.smp.SysDicService;
import com.king.api.smp.SysMenuService;
import com.king.common.annotation.Log;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.exception.RRException;
import com.king.dal.gen.model.Response;
import com.king.dal.gen.model.smp.SysMenu;
import com.king.utils.AbstractController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 系统菜单
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@RestController
@Api(value = "菜单管理", description = "菜单管理")
@RequestMapping("/sys/menu")
public class SysMenuController extends AbstractController {
	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private ShiroService shiroService;
	
	/**
	 * 导航菜单
	 */
	@ApiOperation(value = "导航菜单",response=Response.class)
	@GetMapping("/nav")
	public JsonResponse nav(){
		List<SysMenu> menuList = sysMenuService.getUserMenuList(getUserId());
		Set<String> permissions = shiroService.getUserPermissions(getUserId(),true,getUser().getToken());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("menuList", menuList);
		jsonObject.put("permissions", permissions);
		return JsonResponse.success(jsonObject);
	}
	
	/**
	 * 所有菜单列表
	 */
	@ApiOperation(value = "菜单列表", notes = "权限编码（sys:menu:list）")
	@GetMapping("/list")
	@RequiresPermissions("sys:menu:list")
	public List<SysMenu> list(){
		List<SysMenu> menuList = null;
		if(getUserId().equals(Constant.SUPER_ADMIN)){
			menuList=sysMenuService.queryList(new HashMap<String, Object>());
		}else{
			menuList=sysMenuService.queryUserList(getUserId());
		}
		return menuList;
	}
	
	
	/**
	 * 选择菜单(添加、修改菜单)
	 */
	@Log("菜单选择")
	@ApiOperation(value = "菜单选择",response=Response.class, notes = "权限编码（sys:menu:select）")
	@GetMapping("/select")
	@RequiresPermissions("sys:menu:select")
	public JsonResponse select(){
		List<SysMenu> menuList = sysMenuService.queryNotButtonList();
		//添加顶级菜单
		SysMenu root = new SysMenu();
		root.setMenuId(0L);
		root.setName("一级菜单");
		root.setParentId(-1L);
		root.setOpen(true);
		menuList.add(root);
		return JsonResponse.success(menuList);
	}
	
	/**
	 * 菜单信息
	 */
	@Log("菜单信息")
	@ApiOperation(value = "菜单信息",response=Response.class, notes = "权限编码（sys:menu:info）")
	@GetMapping("/info/{menuId}")
	@RequiresPermissions("sys:menu:info")
	public JsonResponse info(@PathVariable("menuId") Object menuId){
		SysMenu menu = sysMenuService.queryObject(menuId);
		return JsonResponse.success(menu);
	}
	
	/**
	 * 保存
	 */
	@Log("保存菜单")
	@ApiOperation(value = "保存菜单",response=Response.class, notes = "权限编码（sys:menu:save）")
	@PostMapping("/save")
	@RequiresPermissions("sys:menu:save")
	public JsonResponse save(@RequestBody(required = false) SysMenu menu){
		//数据校验
		verifyForm(menu);		
		sysMenuService.save(menu);	
		return JsonResponse.success();
	}
	
	/**
	 * 修改
	 */
	@Log(value="修改菜单",update=true,serviceClass=SysMenuService.class)
	@ApiOperation(value = "修改菜单",response=Response.class, notes = "权限编码（sys:menu:update）")
	@PostMapping("/update")
	@RequiresPermissions("sys:menu:update")
	public JsonResponse update(@RequestBody(required = false) SysMenu menu){
		//数据校验
		verifyForm(menu);			
		sysMenuService.update(menu);
		return JsonResponse.success();
	}
	
	/**
	 * 删除
	 */
	@Log(value="删除菜单",delete=true,serviceClass=SysMenuService.class)
	@ApiOperation(value = "删除菜单",response=Response.class, notes = "权限编码（sys:menu:delete）")
	@PostMapping("/delete")
	@RequiresPermissions("sys:menu:delete")
	public JsonResponse delete(Long menuId){
		//判断是否有子菜单或按钮
		List<SysMenu> menuList = sysMenuService.queryListParentId(menuId);
		if(menuList.size() > 0){
			return JsonResponse.error("请先删除子菜单或按钮");
		}
		sysMenuService.deleteBatch(new Object[]{menuId});
		
		return JsonResponse.success();
	}
	
	/**
	 * 验证参数是否正确
	 */
	private void verifyForm(SysMenu menu){
		if(StringUtils.isBlank(menu.getName())){
			throw new RRException("菜单名称不能为空");
		}		
		if(menu.getParentId() == null){
			throw new RRException("上级菜单不能为空");
		}	
		//菜单
		if(menu.getType() == Constant.MenuType.MENU.getValue()){
			if(StringUtils.isBlank(menu.getUrl())){
				throw new RRException("菜单URL不能为空");
			}
		}		
		//上级菜单类型
		int parentType = Constant.MenuType.CATALOG.getValue();
		if(menu.getParentId() != 0){
			SysMenu parentMenu = sysMenuService.queryObject(menu.getParentId());
			parentType = parentMenu.getType();
		}		
		//目录、菜单
		if(menu.getType() == Constant.MenuType.CATALOG.getValue() ||
				menu.getType() == Constant.MenuType.MENU.getValue()){
			if(parentType != Constant.MenuType.CATALOG.getValue()){
				throw new RRException("上级菜单只能为目录类型");
			}
			return ;
		}	
		//按钮
		if(menu.getType() == Constant.MenuType.BUTTON.getValue()){
			if(parentType != Constant.MenuType.MENU.getValue()){
				throw new RRException("上级菜单只能为菜单类型");
			}
			return ;
		}
	}
}
