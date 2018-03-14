package com.king.utils;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.king.common.exception.RRException;
import com.king.common.utils.JsonResponse;

/**
 * 异常处理器
 * @author King chen
 * @date 2017年12月25日
 */
@RestControllerAdvice
public class RRExceptionHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(RRException.class)
	public JsonResponse handleRRException(RRException e){
		JsonResponse r = new JsonResponse();
		r.put("code", e.getCode());
		r.put("msg", e.getMessage());

		return r;
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public JsonResponse handleDuplicateKeyException(DuplicateKeyException e){
		logger.error(e.getMessage(), e);
		return JsonResponse.error("数据库中已存在该记录");
	}
	
/*	@ExceptionHandler(AuthenticationException.class)
	public JsonResponse handleAuthenticationException(AuthenticationException e){
		logger.error(e.getMessage());
		return JsonResponse.error(401, "token失效，请重新登录");
	}*/
	

	@ExceptionHandler(AuthorizationException.class)
	public JsonResponse handleAuthorizationException(AuthorizationException e){
		logger.error(e.getMessage());
		return JsonResponse.error(403, "没有权限，请联系管理员授权");
	}

	@ExceptionHandler(Exception.class)
	public JsonResponse handleException(Exception e){
		logger.error(e.getMessage(), e);
		return JsonResponse.error();
	}
}
