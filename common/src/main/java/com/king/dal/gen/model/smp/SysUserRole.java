package com.king.dal.gen.model.smp;


import java.io.Serializable;

import org.springframework.data.annotation.Id;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *  用户与角色对应关系
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@ApiModel("用户与角色对应关系")
public class SysUserRole implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7319382256676238163L;

	@Id
	private Long id;

	/**
	 * 用户ID
	 */
	@ApiModelProperty("用户ID")
	private Long userId;

	/**
	 * 角色ID
	 */
	@ApiModelProperty("角色ID")
	private Long roleId;

	/**
	 * 设置：
	 * @param id 
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取：
	 * @return Long
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * 设置：用户ID
	 * @param userId 用户ID
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * 获取：用户ID
	 * @return Long
	 */
	public Long getUserId() {
		return userId;
	}
	
	/**
	 * 设置：角色ID
	 * @param roleId 角色ID
	 */
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	/**
	 * 获取：角色ID
	 * @return Long
	 */
	public Long getRoleId() {
		return roleId;
	}
	
}
