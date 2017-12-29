package com.dobbu.login.api;

import java.util.List;

import com.dobbu.login.entity.Good;

/**
 * @author Administrator
 *
 */
public interface LoginService {
	boolean login(String username,String password);
	List<Good> getGoodList();
}
