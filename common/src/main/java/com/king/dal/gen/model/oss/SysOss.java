package com.king.dal.gen.model.oss;

import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;



/**
 * 文件上传
 * 
 * @author king chen
 * @email 396885563@qq.com
 * @date 2018-05-23 14:25:16
 */
 @ApiModel("文件上传")
public class SysOss implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	@ApiModelProperty("")
	private Long id;
	//名称
	@ApiModelProperty("名称")
	private String name;
	//类型
	@ApiModelProperty("类型")
	private String type;
	//URL地址
	@ApiModelProperty("URL地址")
	private String url;
	//创建者
	@ApiModelProperty("创建者")
	private String creator;
	//文件大小
	@ApiModelProperty("文件大小")
	private String size;
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
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 设置：类型
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * 获取：类型
	 */
	public String getType() {
		return type;
	}
	/**
	 * 设置：URL地址
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 获取：URL地址
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * 设置：创建者
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	/**
	 * 获取：创建者
	 */
	public String getCreator() {
		return creator;
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
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
}
