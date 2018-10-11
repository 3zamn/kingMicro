package com.king.utils.swagger.entity;


/**
 * 请求实体
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年8月10日
 */
public class Request {

    private String name;

    private String type;

    private String paramType;

    private Boolean require;

    private String remark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRequire() {
        return require;
    }

    public void setRequire(Boolean require) {
        this.require = require;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }
}
