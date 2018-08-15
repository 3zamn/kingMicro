package com.king.oauth2;

import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.king.api.smp.ShiroService;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.redis.RedisKeys;
import com.king.common.utils.redis.RedisUtils;
import com.king.common.utils.redis.TokenGenerator;
import com.king.common.utils.spring.SpringContextUtils;
import com.king.dal.gen.model.smp.SysUser;
import com.king.dal.gen.model.smp.SysUserToken;
import com.king.utils.ShiroUtils;

/**
 * shiro认证
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年1月11日
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {
	@Autowired
	private TokenGenerator tokenGenerator;
	
    @Autowired
    private ShiroService shiroService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 授权(验证权限时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SysUser user = (SysUser)principals.getPrimaryPrincipal();
        Long userId = user.getUserId();

        //用户权限列表
        Set<String> permsSet = shiroService.getUserPermissions(userId,true,ShiroUtils.getUserEntity().getToken());
        //刷新失效时间
    	String permKey =RedisKeys.getPermsKey(userId,ShiroUtils.getUserEntity().getToken());
    	RedisUtils redisUtils=SpringContextUtils.getBean(RedisUtils.class);
    	redisUtils.expire(permKey, Constant.PERMS_EXPIRE);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证(登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String accessToken = (String) token.getPrincipal();
        //根据accessToken，查询用户信息
        SysUserToken userToken = null;
        //token失效
        userToken= tokenGenerator.get(accessToken);
	   	 if(userToken == null || userToken.getExpireTime().getTime() < System.currentTimeMillis()){
	            throw new IncorrectCredentialsException("token失效，请重新登录");
	        }else{
	        	tokenGenerator.saveOrUpdate(userToken); 
	        }
        
        //查询用户信息
        SysUser user = shiroService.queryUser(userToken.getUserId());
        user.setToken(accessToken);
        //账号锁定
        if(user.getStatus() == false){
            throw new LockedAccountException("账号已被锁定,请联系管理员");
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, accessToken, getName());
        return info;
    }
}
