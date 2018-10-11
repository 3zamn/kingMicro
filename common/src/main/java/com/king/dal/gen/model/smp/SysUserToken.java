package com.king.dal.gen.model.smp;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 系统用户Token
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年1月11日
 */
@ApiModel("系统用户Token")
public class SysUserToken implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	//用户名
	private String userName;
	//用户ID
	private Long userId;
	//token
	@ApiModelProperty("token")
	private String token;
	//IP
	private String ip;
	//终端
    private String  userAgent;
  //地址
    private String address;
	//过期时间
	@ApiModelProperty("过期时间")
	private Date expireTime;
	//更新时间
	@ApiModelProperty("更新时间")
	private Date updateTime;

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 设置：用户ID
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * 获取：用户ID
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * 设置：token
	 */
	public void setToken(String token) {
		this.token = token;
	}
	/**
	 * 获取：token
	 */
	public String getToken() {
		return token;
	}
	/**
	 * 设置：过期时间
	 */
	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}
	/**
	 * 获取：过期时间
	 */
	public Date getExpireTime() {
		return expireTime;
	}
	/**
	 * 设置：更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	/**
	 * 获取：更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
