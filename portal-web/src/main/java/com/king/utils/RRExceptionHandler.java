package com.king.utils;

import java.io.IOException;

import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.alibaba.fastjson.JSONObject;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.exception.RRException;

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
	
	/**
	 * 重复主键异常
	 * @param e
	 * @return
	 */
	@ExceptionHandler(DuplicateKeyException.class)
	public JsonResponse handleDuplicateKeyException(DuplicateKeyException e){
		logger.error(e.getMessage(), e);
		return JsonResponse.error("数据库中已存在该记录");
	}
	

	/**
	 * 没有权限
	 * @param e
	 * @return
	 */
	@ExceptionHandler(AuthorizationException.class)
	public JsonResponse handleAuthorizationException(AuthorizationException e){
		logger.error(e.getMessage());
		return JsonResponse.error(403, "没有权限，请联系管理员授权");
	}
	 

	/**
	 * 未知异常
	 * @param e
	 * @return
	 */
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
				return JsonResponse.error(500,RRException!=null?RRException:"【服务调用未知异常】--"+e.getMessage());
			}		
		}
		logger.error("异常提示 "+"："+(e instanceof MethodArgumentTypeMismatchException? getException((MethodArgumentTypeMismatchException)e):""),e);
		return JsonResponse.error(getExceptionType(e).getIntValue("code"),getExceptionType(e).getString("msg")+e.getMessage());
	}
	
	/**
	 * 异常类型详情
	 * @param e
	 * @return
	 */
	public static JSONObject getExceptionType(Exception e){
		int code=500;
		StringBuffer msg= new StringBuffer("");
		 if(e instanceof  ClassCastException){
			code=500;
			msg.append("类型转换异常,");		
		}else if(e instanceof  IOException){
			code=1003;
			msg.append("IO异常,");
			
		}else if(e instanceof  NoSuchMethodException){
			code=1004;
			msg.append("未知方法异常,");
			
		}else if(e instanceof  IndexOutOfBoundsException){
			code=1005;
			msg.append("数组越界异常,");
		}else if(e instanceof  HttpMessageNotReadableException){
			code=400;
			msg.append("HTTP请求参数异常,");
		}else if(e instanceof  TypeMismatchException){
			code=400;
			msg.append("类型不匹配异常,");
		}else if(e instanceof  MissingServletRequestParameterException){
			code=400;
			msg.append("Servlet请求参数异常,");
		}else if(e instanceof  HttpRequestMethodNotSupportedException){
			code=405;
			msg.append("http请求方法不支持异常,");
		}else if(e instanceof  HttpMediaTypeNotAcceptableException){
			code=406;
			msg.append("http请求头参数异常,");
		}else if(e instanceof  NullPointerException){
			code=404;
			msg.append("空指针异常,");
		}else if(e instanceof  IllegalArgumentException){
			code=404;
			msg.append("非法参数异常,");
		}else if(e instanceof  MethodArgumentNotValidException){
			code=404;
			msg.append("接口数据校验异常,");
		}else if(e instanceof  RuntimeException){
			code=500;
			msg.append("运行时异常,");		
		}else{
			msg.append("发生未知异常");
		}
		 JSONObject json= new JSONObject();
		 json.put("code", code);
		 json.put("msg", msg);
		return json;
		
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
