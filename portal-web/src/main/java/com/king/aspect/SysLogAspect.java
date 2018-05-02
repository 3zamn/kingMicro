package com.king.aspect;

import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.king.api.smp.SysLogService;
import com.king.common.utils.network.HttpContextUtils;
import com.king.common.utils.network.IPUtils;
import com.king.common.utils.security.ShiroUtils;
import com.king.dal.gen.model.smp.SysLog;
import com.king.dal.gen.model.smp.SysUser;


/**
 * 系统日志，切面处理类
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@Aspect
//@EnableAspectJAutoProxy
@Component
public class SysLogAspect {
	@Autowired
	private SysLogService sysLogService;

	@Pointcut("@annotation(com.king.common.annotation.Log)")
	public void logPointCut() {

	}

	/**
	 * 日常日志记录--错误流水号方便去mongodb查询
	 * @param point
	 * @param e
	 */
	@AfterThrowing(pointcut = "logPointCut()", throwing = "e")
	public void afterThrowing(JoinPoint point, Exception e) {
		// 执行方法
		String username = null;
		if (ShiroUtils.getSubject().getPrincipal() != null) {
			username = ((SysUser) ShiroUtils.getSubject().getPrincipal()).getUsername();
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("data", e.getMessage());
		jsonObject.put("msg", "error");
		saveSysLog(point, jsonObject, username);

	}

	@AfterReturning(pointcut = "logPointCut()", returning = "result")
	public Object afterReturning(JoinPoint point, Object result) {
		// 执行方法
		String username = null;
		if (ShiroUtils.getSubject().getPrincipal() != null) {
			username = ((SysUser) ShiroUtils.getSubject().getPrincipal()).getUsername();
		}
		try {
			// 输出
			JSONObject jsonObject = (JSONObject) JSONObject.toJSON(result);
			// 保存日志
			saveSysLog(point, jsonObject, username);

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

		return result;
	}

	/**
	 * 保存系统操作日志--后面考虑存mongodb
	 * 
	 * @param joinPoint
	 * @param jsonObject
	 * @param username
	 */
	private void saveSysLog(JoinPoint joinPoint, JSONObject jsonObject, String username) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		SysLog sysLog = new SysLog();

		com.king.common.annotation.Log log = method.getAnnotation(com.king.common.annotation.Log.class);
		if (log != null) {
			// 注解上的描述
			sysLog.setOperation(log.value());
		}
		String data = null;
		String msg = null;
		if (jsonObject != null) {
			data = jsonObject.getString("data");
			msg = jsonObject.getString("msg");
		}
		// 请求的方法名
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = signature.getName();
		sysLog.setMethod(className + "." + methodName + "()");
		sysLog.setResult(data);
		sysLog.setStatus(msg);
		// 请求的参数
		Object[] args = joinPoint.getArgs();
		String params = null;
		try {
			params = new Gson().toJson(args[0]);
			sysLog.setParams(params);
		} catch (Exception e) {

		}
		// 获取request
		HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
		// 设置IP地址
		sysLog.setIp(IPUtils.getIpAddr(request));
		// 用户名
		if (username == null) {
			if (params != null) {// 登录
				sysLog.setUsername(params.replaceAll("\"", ""));
			} else if (data != null && methodName.equals("logout")) {// 退出登录
				sysLog.setUsername(data);
			}
		} else {
			sysLog.setUsername(username);
		}
		sysLog.setCreateDate(new Date());
		// 保存系统日志
		sysLogService.save(sysLog);
	}
}
