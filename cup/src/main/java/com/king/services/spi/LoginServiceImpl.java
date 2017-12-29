package com.demo.services.spi;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dobbu.login.api.LoginService;
import com.dobbu.login.entity.Good;

/**
 * @author king chen
 * @date 2017年12月6日
 */
@Component
public class LoginServiceImpl implements LoginService {

	public boolean login(String username, String password) {
		if(StringUtils.hasText(username)&&StringUtils.hasText(password)){
			if(username.equals(password)){
				return true;
			}
		}
		return false;
	}

	public List<Good> getGoodList() {
		List<Good> goodList=new ArrayList<Good>();
		goodList.add(new Good("牛奶", 28));
		goodList.add(new Good("乳酸菌", 15));
		goodList.add(new Good("面包", 30));
		return goodList;
	}
}
