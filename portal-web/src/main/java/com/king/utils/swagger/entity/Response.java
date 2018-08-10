package com.king.utils.swagger.entity;


/**
 * 响应实体
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年8月10日
 */
public class Response {
 
    private String description;

    private String name;

    private String remark;

    public  Response(){

    }

    public Response(String description, String name, String remark) {
        this.description = description;
        this.name = name;
        this.remark = remark;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
