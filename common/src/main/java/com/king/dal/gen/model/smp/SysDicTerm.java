package com.king.dal.gen.model.smp;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 数据字典项
 * 
 * @author king chen
 * @email 396885563@qq.com
 * @date 2018-05-10 15:36:46
 */
 @ApiModel("数据字典项")
public class SysDicTerm implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//项-值
	@ApiModelProperty("项-值")
	private String value;
	//项
	@ApiModelProperty("项")
	private String text;
	
	@ApiModelProperty("排序")
	private Integer sortNo;
	

	/**
	 * 设置：项-值
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * 获取：项-值
	 */
	public String getValue() {
		return value;
	}
	/**
	 * 设置：项
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * 获取：项
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * 设置：排序
	 */
	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}
	/**
	 * 获取：排序
	 */
	public Integer getSortNo() {
		return sortNo;
	}
	
}
