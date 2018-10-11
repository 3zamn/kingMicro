package com.king.utils.swagger.controller;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.king.common.utils.JsonResponse;
import com.king.utils.swagger.entity.Body;
import com.king.utils.swagger.entity.Head;
import com.king.utils.swagger.entity.Info;
import com.king.utils.swagger.service.WordService;

import icepdf.h;

import java.util.LinkedList;
import java.util.List;


/**
 * 将响应内容json转换doc
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年8月10日
 */
@RestController
public class WordController {

    @Autowired
    private WordService wordService;

    @GetMapping(value="/exportDoc",produces = "application/json;charset=UTF-8")
    @RequiresPermissions("sys:exportApiDoc")
    public JsonResponse getJson(){
        List<Body> bodys = wordService.bodyList();
        List<Head> heads = wordService.headList();
        for(Head head:heads){
        	LinkedList<Body> b= new LinkedList<>();
        	for(Body body:bodys){
        		if(head.getName().equals(body.getTitle())){
        			b.add(body);
        		}
        	}
        	head.setBodyList(b);
        }
        Info info =wordService.getinfo();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("head", heads);
        jsonObject.put("info", info);
        return JsonResponse.success(jsonObject);
    }
}
