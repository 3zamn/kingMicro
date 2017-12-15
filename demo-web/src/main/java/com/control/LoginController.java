package com.control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dobbu.login.api.LoginService;
import com.dobbu.login.entity.Good;

/**
 * @author Administrator
 *
 */
@Controller
public class LoginController {
	
	@Autowired
	private LoginService loginService;
	
	@RequestMapping("login")
	public String login(String username,String password,Model model){
		boolean flag=loginService.login(username, password);
		if(flag){
			List<Good> goodList=loginService.getGoodList();
			return goodList.toString();
		}
		return "调用失败";
	}
}