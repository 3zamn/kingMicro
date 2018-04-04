package com.king.utils;

import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
	

	@ExceptionHandler(AuthorizationException.class)
	public JsonResponse handleAuthorizationException(AuthorizationException e){
		logger.error(e.getMessage());
		return JsonResponse.error(403, "没有权限，请联系管理员授权");
	}

	@ExceptionHandler(Exception.class)
	public JsonResponse handleException(Exception e){
		String RRException=null;
		if(e.getMessage() !=null){
			if(e.getMessage().contains("DuplicateKeyException")){
				logger.error("数据库中已存在该记录");
				return JsonResponse.error("数据库中已存在该记录");
			}	
			if(e.getMessage().contains("RRException")){
				RRException =e.getMessage().substring(e.getMessage().indexOf("服务调用时"), e.getMessage().indexOf("，请联系管理员"));
			}		
		}
		logger.error("错误提示 "+"："+(e instanceof MethodArgumentTypeMismatchException? getException((MethodArgumentTypeMismatchException)e):""),e);
		return JsonResponse.error(RRException!=null?RRException:"【服务调用内部错误】--"+e.getMessage());
	}
	
	
	/**
	 * 参数类型错误
	 * @param e
	 * @return
	 */
	public JSONObject getException(MethodArgumentTypeMismatchException e){
	
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("className",e.getParameter().getContainingClass());
		jsonObject.put("method", e.getParameter().getContainingClass()+"."+e.getParameter().getMethod().getName()+"("+e.getParameter().getParameterType()+")");
		jsonObject.put("cause", e.getName()+"："+e.getMessage());
		return jsonObject;
		
	}
}
