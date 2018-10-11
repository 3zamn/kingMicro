package com.king.app.service;


import java.util.List;
import java.util.Map;

import com.king.app.entity.AppUser;

/**
 * 用户service
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年4月23日
 */
public interface UserService {

	AppUser queryObject(Long userId);
	
	List<AppUser> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(String mobile, String password);
	
	void update(AppUser user);
	
	void delete(Long userId);
	
	void deleteBatch(Long[] userIds);

	AppUser queryByMobile(String mobile);

	/**
	 * 用户登录
	 * @param mobile    手机号
	 * @param password  密码
	 * @return          返回用户ID
	 */
	long login(String mobile, String password);
}
