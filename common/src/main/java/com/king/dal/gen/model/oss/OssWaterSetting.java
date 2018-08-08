package com.king.dal.gen.model.oss;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 水印设置
 * @author king chen
 * @email 396885563@qq.com
 * @date 2018-07-30 10:51:12
 */
 @ApiModel("水印设置")
public class OssWaterSetting implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	private Long id;
	//用户Id
	@Id
	@ApiModelProperty("用户Id")
	private Long userId;
	//启用状态,0:否，1:是
	@ApiModelProperty("启用状态,0:否，1:是")
	private Boolean enable;
	//是否生成图片,0:否，1:是
	@ApiModelProperty("启用状态,0:否，1:是")
	private Boolean isConvertImg;
	//水印类型，1:二维码水印，2:文字水印
	@ApiModelProperty("水印类型，1:二维码水印，2:文字水印")
	private Integer type;
	//字体大小
	@ApiModelProperty("字体大小")
	private String fontSize;
	//水印颜色
	@ApiModelProperty("水印颜色")
	private Integer waterColor;
	//水印内容
	@ApiModelProperty("水印内容")
	private String waterContent;
	//水印位置
	@ApiModelProperty("水印位置")
	private String waterPosition;
	//图片宽
	@ApiModelProperty("图片宽")
	private Integer waterWidth;
	//图片高
	@ApiModelProperty("图片高")
	private Integer waterHeigth;
	//x轴边距
	@ApiModelProperty("x轴边距")
	private Integer marginX;
	//y轴边距
	@ApiModelProperty("y轴边距")
	private Integer marginY;
	//创建者
	@ApiModelProperty("创建者")
	private String creator;
	//创建时间
	@ApiModelProperty("创建时间")
	private Date createTime;

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
	 * 设置：用户Id
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * 获取：用户Id
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * 设置：启用状态,0:否，1:是
	 */
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	/**
	 * 获取：启用状态,0:否，1:是
	 */
	public Boolean getEnable() {
		return enable;
	}
	
	public Boolean getIsConvertImg() {
		return isConvertImg;
	}
	public void setIsConvertImg(Boolean isConvertImg) {
		this.isConvertImg = isConvertImg;
	}
	/**
	 * 设置：水印类型，1:二维码水印，2:文字水印
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * 获取：水印类型，1:二维码水印，2:文字水印
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * 设置：字体大小
	 */
	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}
	/**
	 * 获取：字体大小
	 */
	public String getFontSize() {
		return fontSize;
	}
	/**
	 * 设置：水印颜色
	 */
	public void setWaterColor(Integer waterColor) {
		this.waterColor = waterColor;
	}
	/**
	 * 获取：水印颜色
	 */
	public Integer getWaterColor() {
		return waterColor;
	}
	/**
	 * 设置：水印内容
	 */
	public void setWaterContent(String waterContent) {
		this.waterContent = waterContent;
	}
	/**
	 * 获取：水印内容
	 */
	public String getWaterContent() {
		return waterContent;
	}
	/**
	 * 设置：水印位置
	 */
	public void setWaterPosition(String waterPosition) {
		this.waterPosition = waterPosition;
	}
	/**
	 * 获取：水印位置
	 */
	public String getWaterPosition() {
		return waterPosition;
	}
	/**
	 * 设置：图片宽
	 */
	public void setWaterWidth(Integer waterWidth) {
		this.waterWidth = waterWidth;
	}
	/**
	 * 获取：图片宽
	 */
	public Integer getWaterWidth() {
		return waterWidth;
	}
	/**
	 * 设置：图片高
	 */
	public void setWaterHeigth(Integer waterHeigth) {
		this.waterHeigth = waterHeigth;
	}
	/**
	 * 获取：图片高
	 */
	public Integer getWaterHeigth() {
		return waterHeigth;
	}
	/**
	 * 设置：x轴边距
	 */
	public void setMarginX(Integer marginX) {
		this.marginX = marginX;
	}
	/**
	 * 获取：x轴边距
	 */
	public Integer getMarginX() {
		return marginX;
	}
	/**
	 * 设置：y轴边距
	 */
	public void setMarginY(Integer marginY) {
		this.marginY = marginY;
	}
	/**
	 * 获取：y轴边距
	 */
	public Integer getMarginY() {
		return marginY;
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
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}
}
