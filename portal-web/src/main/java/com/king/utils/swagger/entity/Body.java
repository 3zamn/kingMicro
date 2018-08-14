package com.king.utils.swagger.entity;

import java.util.List;

/**
 * 接口方法内容实体
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年8月10日
 */
public class Body {

    private String title;

    private String tag;

    private String url;

    private String description;

    private String requestForm;

    private String responseForm;

    private String requestType;

    private List<Request> requestList;

    private List<Response> responseList;

    private String requestParam;

    private String responseParam;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResponseForm() {
        return responseForm;
    }

    public void setResponseForm(String responseForm) {
        this.responseForm = responseForm;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public List<Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<Request> requestList) {
        this.requestList = requestList;
    }

    public List<Response> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<Response> responseList) {
        this.responseList = responseList;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public String getResponseParam() {
        return responseParam;
    }

    public void setResponseParam(String responseParam) {
        this.responseParam = responseParam;
    }

    public String getRequestForm() {
        return requestForm;
    }

    public void setRequestForm(String requestForm) {
        this.requestForm = requestForm;
    }
}
