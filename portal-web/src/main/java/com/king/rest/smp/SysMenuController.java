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

import com.king.api.smp.ShiroService;
import com.king.api.smp.SysMenuService;
import com.king.common.annotation.Log;
import com.king.common.exception.RRException;
import com.king.common.utils.Constant;
import com.king.common.utils.JsonResponse;
import com.king.dal.gen.model.smp.SysMenu;
import com.king.utils.TokenHolder;

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
	@ApiOperation(value = "导航菜单")
	@GetMapping("/nav")
	public JsonResponse nav(){
		List<SysMenu> menuList = sysMenuService.getUserMenuList(getUserId());
		Set<String> permissions = shiroService.getUserPermissions(getUserId(),true,TokenHolder.token.get());
		return JsonResponse.success().put("menuList", menuList).put("permissions", permissions);
	}
	
	/**
	 * 所有菜单列表
	 */
	@ApiOperation(value = "菜单列表")
	@GetMapping("/list")
	@RequiresPermissions("sys:menu:list")
	public List<SysMenu> list(){
		List<SysMenu> menuList = sysMenuService.queryList(new HashMap<String, Object>());

		return menuList;
	}
	
	/**
	 * 选择菜单(添加、修改菜单)
	 */
	@ApiOperation(value = "菜单选择")
	@GetMapping("/select")
	@RequiresPermissions("sys:menu:select")
	public JsonResponse select(){
		//查询列表数据
		List<SysMenu> menuList = sysMenuService.queryNotButtonList();
		
		//添加顶级菜单
		SysMenu root = new SysMenu();
		root.setMenuId(0L);
		root.setName("一级菜单");
		root.setParentId(-1L);
		root.setOpen(true);
		menuList.add(root);
		
		return JsonResponse.success().put("menuList", menuList);
	}
	
	/**
	 * 菜单信息
	 */
	@ApiOperation(value = "菜单信息")
	@GetMapping("/info/{menuId}")
	@RequiresPermissions("sys:menu:info")
	public JsonResponse info(@PathVariable("menuId") Long menuId){
		SysMenu menu = sysMenuService.queryObject(menuId);
		return JsonResponse.success().put("menu", menu);
	}
	
	/**
	 * 保存
	 */
	@Log("保存菜单")
	@ApiOperation(value = "保存菜单")
	@PostMapping("/save")
	@RequiresPermissions("sys:menu:save")
	public JsonResponse save(@RequestBody SysMenu menu){
		//数据校验
		verifyForm(menu);
		
		sysMenuService.save(menu);
		
		return JsonResponse.success();
	}
	
	/**
	 * 修改
	 */
	@Log("修改菜单")
	@ApiOperation(value = "修改菜单")
	@PostMapping("/update")
	@RequiresPermissions("sys:menu:update")
	public JsonResponse update(@RequestBody SysMenu menu){
		//数据校验
		verifyForm(menu);
				
		sysMenuService.update(menu);
		
		return JsonResponse.success();
	}
	
	/**
	 * 删除
	 */
	@Log("删除菜单")
	@ApiOperation(value = "删除菜单")
	@PostMapping("/delete")
	@RequiresPermissions("sys:menu:delete")
	public JsonResponse delete(long menuId){
		//判断是否有子菜单或按钮
		List<SysMenu> menuList = sysMenuService.queryListParentId(menuId);
		if(menuList.size() > 0){
			return JsonResponse.error("请先删除子菜单或按钮");
		}

		sysMenuService.deleteBatch(new Long[]{menuId});
		
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
