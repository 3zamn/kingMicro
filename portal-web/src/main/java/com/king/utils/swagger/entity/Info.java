package com.king.utils.swagger.entity;

import java.util.Date;

/**
 * 接口描述
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年8月14日
 */
public class Info {	
	private String description;
	private String version;
	private String title;
	private String termsOfService;
	private String host;
	private String basePath;
	private String author;
	private String email;
	private Date date;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTermsOfService() {
		return termsOfService;
	}
	public void setTermsOfService(String termsOfService) {
		this.termsOfService = termsOfService;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getBasePath() {
		return basePath;
	}
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	
}
