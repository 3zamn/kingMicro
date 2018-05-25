package com.king.dal.gen.model;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 返回数据格式定义
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年5月25日
 */
@ApiModel("返回数据格式")
public class Response  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6175672064246664533L;
	
	/**
	 * code
	 */
	@ApiModelProperty(example="200",position = 0 ,notes="响应码,默认200:成功.500:失败")
	private Integer code;
	
	/**
	 * msg
	 */
	@ApiModelProperty(example="success",position = 1 ,notes="响应信息")
	private String msg;
	
	/**
	 * data
	 */
	@ApiModelProperty(position = 2 ,notes="返回json数据")
	private Object data;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	

}
