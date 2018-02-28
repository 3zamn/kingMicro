package com.king.oauth2;

import java.util.Date;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.king.api.smp.ShiroService;
import com.king.api.smp.SysUserTokenService;
import com.king.common.utils.Constant;
import com.king.common.utils.EnttyMapperResolver;
import com.king.common.utils.RedisUtils;
import com.king.common.utils.SpringContextUtils;
import com.king.common.utils.TokenGenerator;
import com.king.dal.gen.model.smp.SysUser;
import com.king.dal.gen.model.smp.SysUserToken;

/**
 * shiro认证
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年1月11日
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {
	@Value("#{new Boolean('${king.redis.open}')}")
	private Boolean redisOpen;
	
	@Autowired
	private TokenGenerator tokenGenerator;
	
    @Autowired
    private ShiroService shiroService;
    
    @Autowired
    private SysUserTokenService sysUserTokenService;

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
        Set<String> permsSet = shiroService.getUserPermissions(userId);

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
      /*  RedisUtils redisUtils =(RedisUtils)SpringContextUtils.getBean("redisUtils");
    	if(redisUtils !=null){
    		if(((EnttyMapperResolver)SpringContextUtils.getBean("enttyMapperRedis")).isExistAttribute("00", "000")){
 				System.out.println("000");		
 			}	
    		
    	}*/
        if(redisOpen){//是否开启redis
        	userToken= tokenGenerator.get(accessToken);
        	 if(userToken == null || userToken.getExpireTime().getTime() < System.currentTimeMillis()){
                 throw new IncorrectCredentialsException("token失效，请重新登录");
             }else{
             	tokenGenerator.saveOrUpdate(userToken); 
             }
        }else{
        	userToken=shiroService.queryByToken(accessToken); 
        	 if(userToken == null || userToken.getExpireTime().getTime() < System.currentTimeMillis()){
                 throw new IncorrectCredentialsException("token失效，请重新登录");
             }else{
            	 Date expireTime = new Date(userToken.getExpireTime().getTime()+Constant.TOKEN_EXPIRE);
             	userToken.setExpireTime(expireTime);
             	sysUserTokenService.update(userToken);
             }
        	
        }
        
        //查询用户信息
        SysUser user = shiroService.queryUser(userToken.getUserId());
        //账号锁定
        if(user.getStatus() == 0){
            throw new LockedAccountException("账号已被锁定,请联系管理员");
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, accessToken, getName());
        return info;
    }
}
