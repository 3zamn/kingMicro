package com.king.common.mongodb.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 系统日志
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */

@Document(collection = "SysLogVO")
@CompoundIndexes({//模糊查询索引
    @CompoundIndex(name = "username_operation_method", def = "{'username': 1, 'operation': 1,'method': 1}"),
    @CompoundIndex(name = "ip_username_createDate", def = "{'ip': 1,'username': 1,'createDate': 1}")
})
public class SysLogVO implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String username;
	private String operation;
	private String method;
	private String params;
	private String result;
	@Indexed
	private String status;
	private Long time;
	private String ip;
	@Indexed
	private Date createDate;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
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
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
