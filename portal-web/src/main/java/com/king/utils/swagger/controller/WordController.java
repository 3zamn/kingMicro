package com.king.utils.swagger.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.king.common.utils.JsonResponse;
import com.king.utils.swagger.entity.Body;
import com.king.utils.swagger.service.WordService;

import java.util.List;


/**
 * 将响应内容json转换doc
 * @author King chen
 * @emai 396885563@qq.com
 * @data 2018年8月10日
 */
@RestController
public class WordController {

    @Autowired
    private WordService tableService;

    @RequestMapping("/exportDoc")
    public JsonResponse getJson(Model model){
        List<Body> list = tableService.tableList();

        return JsonResponse.success(list);
    }
}
