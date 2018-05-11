package com.king.dal.gen.model.smp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;



/**
 * 数据字典明细
 * 
 * @author king chen
 * @email 396885563@qq.com
 * @date 2018-05-10 15:36:46
 */
 @ApiModel("数据字典明细")
public class SysDic implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	@ApiModelProperty("")
	private Long id;
	//字典名称
	@ApiModelProperty("字典名称")
	private String name;
	//字典编码
	@ApiModelProperty("字典编码")
	private String code;
	//项-值
	@ApiModelProperty("项-值")
	private String value;
	//项
	@ApiModelProperty("项")
	private String text;
	//类型 0：目录，1：字典项
	@ApiModelProperty("类型 0：目录，1：字典项")
	private Integer type;
	//上级目录Id
	@ApiModelProperty("上级目录Id")
	private Long parentId;
	//上级目录名称
	@ApiModelProperty("上级目录名称")
	private String parentName;
	//排序
	@ApiModelProperty("排序")
	private Integer sortNo;
	//是否可编辑。1：是。0：否
	@ApiModelProperty("是否可编辑。1：是。0：否")
	private Integer editable;
	//状态。1启用。0禁用
	@ApiModelProperty("状态。1启用。0禁用")
	private Integer enable;
	//备注
	@ApiModelProperty("备注")
	private String remark;
	//创建者
	@ApiModelProperty("创建者")
	private String createBy;
	//创建时间
	@ApiModelProperty("创建时间")
	private Date createTime;
	//更新者
	@ApiModelProperty("更新者")
	private String updateBy;
	//更新时间
	@ApiModelProperty("更新时间")
	private Date updateTime;
	
	/**
	 * ztree属性
	 */
	private Boolean open;
	
	private List<?> list;

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
	 * 设置：字典名称
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取：字典名称
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置：字典编码
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 获取：字典编码
	 */
	public String getCode() {
		return code;
	}
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
	 * 设置：类型 0：目录，1：字典项
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * 获取：类型 0：目录，1：字典项
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * 设置：上级目录Id
	 */
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	/**
	 * 获取：上级目录Id
	 */
	public Long getParentId() {
		return parentId;
	}
	
	
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
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
	/**
	 * 设置：是否可编辑。1：是。0：否
	 */
	public void setEditable(Integer editable) {
		this.editable = editable;
	}
	/**
	 * 获取：是否可编辑。1：是。0：否
	 */
	public Integer getEditable() {
		return editable;
	}
	/**
	 * 设置：状态。1启用。0禁用
	 */
	public void setEnable(Integer enable) {
		this.enable = enable;
	}
	/**
	 * 获取：状态。1启用。0禁用
	 */
	public Integer getEnable() {
		return enable;
	}
	/**
	 * 设置：备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * 获取：备注
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * 设置：创建者
	 */
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	/**
	 * 获取：创建者
	 */
	public String getCreateBy() {
		return createBy;
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
	/**
	 * 设置：更新者
	 */
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	/**
	 * 获取：更新者
	 */
	public String getUpdateBy() {
		return updateBy;
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
	public Boolean getOpen() {
		return open;
	}
	public void setOpen(Boolean open) {
		this.open = open;
	}
	public List<?> getList() {
		return list;
	}
	public void setList(List<?> list) {
		this.list = list;
	}
	
	
	
}
