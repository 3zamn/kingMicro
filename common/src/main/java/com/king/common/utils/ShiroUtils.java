package com.king.common.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.king.common.exception.RRException;
import com.king.dal.gen.model.smp.SysConfig;
import com.king.dal.gen.model.smp.SysUser;


/**
 * Shiro工具类
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
public class ShiroUtils {

	/**  加密算法 */
	public final static String hashAlgorithmName = "SHA-256";
	/**  循环次数 */
	public final static int hashIterations = 16;

	public static String sha256(String password, String salt) {
		return new SimpleHash(hashAlgorithmName, password, salt, hashIterations).toString();
	}

	public static Session getSession() {
		RedisUtils redisUtils=SpringContextUtils.getBean(RedisUtils.class);
	//	  String key = RedisKeys.getShiroSessionKey(configKey);
	 //     redisUtils.get(key, SysConfig.class);
		return SecurityUtils.getSubject().getSession();
	}

	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	public static SysUser getUserEntity() {
		return (SysUser)SecurityUtils.getSubject().getPrincipal();
	}

	public static Long getUserId() {
		return getUserEntity().getUserId();
	}
	
	public static void setSessionAttribute(Object key, Object value) {
		getSession().setAttribute(key, value);
		RedisUtils redisUtils=SpringContextUtils.getBean(RedisUtils.class);
		String sessionId = RedisKeys.getKaptchaKey(getSession().getId().toString());
		redisUtils.set(sessionId, value);
	}

	public static Object getSessionAttribute(Object key) {
		RedisUtils redisUtils=SpringContextUtils.getBean(RedisUtils.class);
		String kaptchaKey = RedisKeys.getKaptchaKey(getSession().getId().toString());
		return getSession().getAttribute(key) != null ? getSession().getAttribute(key):redisUtils.get(kaptchaKey);
	}

	public static boolean isLogin() {
		return SecurityUtils.getSubject().getPrincipal() != null;
	}

	public static void logout() {
		String sessionId = getSession().getId().toString();
		SecurityUtils.getSubject().logout();
		RedisUtils redisUtils=SpringContextUtils.getBean(RedisUtils.class);
		String sessionKey = RedisKeys.getShiroSessionKey(sessionId);
		String kaptchaKey = RedisKeys.getKaptchaKey(sessionId);
		redisUtils.delete(sessionKey);
		redisUtils.delete(kaptchaKey);
	}
	
	public static String getKaptcha(String key) {
		Object kaptcha = getSessionAttribute(key);
		if(kaptcha == null){
			throw new RRException("验证码已失效");
		}
		getSession().removeAttribute(key);
		RedisUtils redisUtils=SpringContextUtils.getBean(RedisUtils.class);
		String kaptchaKey = RedisKeys.getKaptchaKey(key);
		redisUtils.delete(kaptchaKey);
		return kaptcha.toString();
	}

	
}
