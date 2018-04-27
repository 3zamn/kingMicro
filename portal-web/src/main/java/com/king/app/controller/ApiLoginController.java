package com.king.app.controller;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.king.app.annotation.Login;
import com.king.app.annotation.LoginUser;
import com.king.app.entity.UserEntity;
import com.king.app.service.UserService;
import com.king.app.utils.JwtUtils;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.validator.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 第三方应用登录授权
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月23日
 */
@RestController
@RequestMapping("/app")
@Api(value = "第三方应用登录接口", description = "第三方应用登录接口")
public class ApiLoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 登录
     */
    @PostMapping("login")
    @ApiOperation("登录")
    public JsonResponse login(String mobile, String password){
        Assert.isBlank(mobile, "手机号不能为空");
        Assert.isBlank(password, "密码不能为空");
        //用户登录
        long userId = userService.login(mobile, password);
        //生成token
        String token = jwtUtils.generateToken(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("expire", jwtUtils.getExpire());
        return JsonResponse.success(map);
    }
    
    /**
     * 注册
     * @param mobile
     * @param password
     * @return
     */
    @PostMapping("register")
    @ApiOperation("注册")
    public JsonResponse register(String mobile, String password){
        Assert.isBlank(mobile, "手机号不能为空");
        Assert.isBlank(password, "密码不能为空");
        userService.save(mobile, password);
        return JsonResponse.success();
    }
    
    
    /**
     * 获取用户信息
     * @param user
     * @return
     */
    @Login
    @GetMapping("userInfo")
    @ApiOperation("获取用户信息")
    public JsonResponse userInfo(@LoginUser UserEntity user){
        return JsonResponse.success(user);
    }
    

    /**
     * 获取用户ID
     * @param userId
     * @return
     */
    @Login
    @GetMapping("userId")
    @ApiOperation("获取用户ID")
    public JsonResponse userInfo(@RequestAttribute("userId") Integer userId){
        return JsonResponse.success(userId);
    }

}
