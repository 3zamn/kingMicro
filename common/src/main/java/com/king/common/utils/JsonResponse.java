package com.king.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据--默认code：200 成功；500失败;
 * @author King chen
 * @date 2017年12月25日
 */
public class JsonResponse extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public JsonResponse() {
		put("code", 200);
	}
	
	public static JsonResponse error() {
		return error(500, "未知异常，请联系管理员");
	}
	
	public static JsonResponse error(String msg) {
		return error(500, msg);
	}
	
	public static JsonResponse error(int code, String msg) {
		JsonResponse r = new JsonResponse();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}
	
	public static JsonResponse error(int code, String msg,Object data) {
		JsonResponse r = new JsonResponse();
		r.put("code", code);
		r.put("msg", msg);
		r.put("data", data);
		return r;
	}

/*	public static JsonResponse success(String msg) {
		JsonResponse r = new JsonResponse();
		r.put("msg", msg);
		return r;
	}*/
	
	public static JsonResponse success(String msg,Object data) {
		JsonResponse r = new JsonResponse();
		r.put("msg", msg);
		r.put("data", data);
		return r;
	}
	
	public static JsonResponse success(Map<String, Object> map) {
		JsonResponse r = new JsonResponse();
		r.put("data", map);
		return r;
	}
	
	public static JsonResponse success() {
		JsonResponse r = new JsonResponse();
		r.put("msg", "success");
		return r;
	}
	
	public static JsonResponse success(Object data) {
		JsonResponse r = new JsonResponse();
		r.put("msg", "success");
		r.put("data", data);
		return r;
	}

	public JsonResponse put(String key, Object value) {
		super.put(key, value);
		return this;
	}
	
	public JsonResponse putPage(Object page, Object... value) {
		super.put("page", page);
		return this;
	}
}
