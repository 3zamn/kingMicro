package com.king.api.smp;


import java.util.Set;

import com.king.dal.gen.model.smp.SysUser;
import com.king.dal.gen.model.smp.SysUserToken;

/**
 * shiro相关接口
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年1月11日
 */
public interface ShiroService {
    /**
     * 获取用户权限列表
     */
    Set<String> getUserPermissions(long userId);

    SysUserToken queryByToken(String token);

    /**
     * 根据用户ID，查询用户
     * @param userId
     */
    SysUser queryUser(Long userId);
}
