package com.king.dal.gen.model.smp;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 系统日志
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@ApiModel("系统日志")
public class SysLog implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long id;
	//用户名
	@ApiModelProperty("用户名")
	private String username;
	//用户操作
	@ApiModelProperty("用户操作")
	private String operation;
	//请求方法
	@ApiModelProperty("请求方法")
	private String method;
	//请求参数
	@ApiModelProperty("请求参数")
	private String params;
	
	@ApiModelProperty("结果")
	private String result;
	
	@ApiModelProperty("执行状态")
	private String status;
	
	//执行时长(毫秒)
	@ApiModelProperty("执行时长(毫秒)")
	private Long time;
	//IP地址
	@ApiModelProperty("IP地址")
	private String ip;
	//创建时间
	@ApiModelProperty("创建时间")
	private Date createDate;

	/**
	 * 设置：
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：
	 */
	public Long getId() {
		return id;
	}
	/**
	 * 设置：用户名
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * 获取：用户名
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * 设置：用户操作
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
	/**
	 * 获取：用户操作
	 */
	public String getOperation() {
		return operation;
	}
	/**
	 * 设置：请求方法
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * 获取：请求方法
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * 设置：请求参数
	 */
	public void setParams(String params) {
		this.params = params;
	}
	/**
	 * 获取：请求参数
	 */
	public String getParams() {
		return params;
	}
	/**
	 * 设置：IP地址
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	/**
	 * 获取：IP地址
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * 设置：创建时间
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCreateDate() {
		return createDate;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
