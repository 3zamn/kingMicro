package com.king.dal.gen.model.oss;

import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;



/**
 * 文档转pdf、生成图片
 * 
 * @author king chen
 * @email 396885563@qq.com
 * @date 2018-07-25 10:14:40
 */
 @ApiModel("文档转pdf、生成图片")
public class OssDoc2pdf implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	@ApiModelProperty("")
	private Long id;
	//
	@ApiModelProperty("")
	private String name;
	//1:七牛云;2:阿里云;3:腾讯云
	@ApiModelProperty("1:七牛云;2:阿里云;3:腾讯云")
	private String type;
	//URL地址
	@ApiModelProperty("URL地址")
	private String url;
	//转pdf地址
	@ApiModelProperty("转pdf地址")
	private String pdf;
	//转图片地址
	@ApiModelProperty("转图片地址")
	private String img;
	//
	@ApiModelProperty("")
	private String size;
	//创建者
	@ApiModelProperty("创建者")
	private String creator;
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
	 * 设置：
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取：
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置：1:七牛云;2:阿里云;3:腾讯云
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * 获取：1:七牛云;2:阿里云;3:腾讯云
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
	 * 设置：转pdf地址
	 */
	public void setPdf(String pdf) {
		this.pdf = pdf;
	}
	/**
	 * 获取：转pdf地址
	 */
	public String getPdf() {
		return pdf;
	}
	/**
	 * 设置：转图片地址
	 */
	public void setImg(String img) {
		this.img = img;
	}
	/**
	 * 获取：转图片地址
	 */
	public String getImg() {
		return img;
	}
	/**
	 * 设置：
	 */
	public void setSize(String size) {
		this.size = size;
	}
	/**
	 * 获取：
	 */
	public String getSize() {
		return size;
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
}
