package com.king.oauth2;


import org.apache.shiro.authc.AuthenticationToken;

/**
 * token
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年1月11日
 */
public class OAuth2Token implements AuthenticationToken {
    /**
	 * 
	 */
	private static final long serialVersionUID = -475327548398408650L;
	private String token;

    public OAuth2Token(String token){
        this.token = token;
    }

    @Override
    public String getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
