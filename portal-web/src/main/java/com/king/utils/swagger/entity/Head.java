package com.king.utils.swagger.entity;

import java.util.List;

/**
 * 接口头(类)-名称、描述
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年8月14日
 */
public class Head {
	private String name;
	private String description;
	private List<Body> bodyList;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Body> getBodyList() {
		return bodyList;
	}
	public void setBodyList(List<Body> bodyList) {
		this.bodyList = bodyList;
	}

	
}
