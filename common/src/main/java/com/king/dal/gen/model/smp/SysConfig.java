package com.king.dal.gen.model.smp;


import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 系统配置信息
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@ApiModel("系统配置信息表")
public class SysConfig implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	@ApiModelProperty("key")
	@NotBlank(message="参数名不能为空")
	private String key;
	
	@ApiModelProperty("value")
	@NotBlank(message="参数值不能为空")
	private String value; 
	
	//状态   0：隐藏   1：显示
	@ApiModelProperty("状态   0：隐藏   1：显示")
	private Integer status;
	
	@ApiModelProperty("备注")
	private String remark;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
