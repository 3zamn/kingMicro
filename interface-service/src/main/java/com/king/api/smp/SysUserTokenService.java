package com.king.api.smp;

import com.king.common.utils.JsonResponse;
import com.king.dal.gen.model.smp.SysUserToken;

/**
 * 用户Token
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年1月11日
 */
public interface SysUserTokenService {

	SysUserToken queryByUserId(Long userId);

	void save(SysUserToken token);
	
	void update(SysUserToken token);

	/**
	 * 生成token
	 * @param userId  用户ID
	 */
	JsonResponse createToken(long userId);

	/**
	 * 退出，修改token值
	 * @param userId  用户ID
	 */
	void logout(long userId);

}
