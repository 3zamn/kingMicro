package com.king.dao;

import com.king.dal.gen.dao.BaseDao;
import com.king.dal.gen.model.smp.SysUserToken;

/**
 * 系统用户Token
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年1月11日
 */
public interface SysUserTokenDao extends BaseDao<SysUserToken> {
    
    SysUserToken queryByUserId(Long userId);

    SysUserToken queryByToken(String token);
	
}
