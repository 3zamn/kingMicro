package com.king.utils.swagger.service;


import java.util.List;

import com.king.utils.swagger.entity.Body;
import com.king.utils.swagger.entity.Head;
import com.king.utils.swagger.entity.Info;

/**
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年8月10日
 */
public interface WordService {
    List<Body> bodyList();
    List<Head> headList();
    Info getinfo();
}
