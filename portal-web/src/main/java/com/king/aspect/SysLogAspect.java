package com.king.aspect;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.king.api.smp.SysLogService;
import com.king.common.mongodb.log.model.ExceptionLogVO;
import com.king.common.mongodb.log.repo.ExceptionLogRepo;
import com.king.common.utils.exception.ExceptionUtils;
import com.king.common.utils.pattern.StringToolkit;
import com.king.dal.gen.model.smp.SysLog;
import com.king.dal.gen.model.smp.SysUser;
import com.king.utils.HttpContextUtils;
import com.king.utils.IPUtils;
import com.king.utils.ShiroUtils;

import net.sf.json.JSONArray;


/**
 * 系统日志，切面处理类
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@Aspect
@Component
public class SysLogAspect {
	@Autowired
	private SysLogService sysLogService;
	@Autowired
	private ExceptionLogRepo exceptionLogRepo;
	private static String ipAddress = "127.0.0.1";
	private static Configuration configs;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Pointcut("@annotation(com.king.common.annotation.Log)")
	public void logPointCut() {
		//日志切面
	}

	/**
	 * 日常日志记录--错误流水号方便去mongodb查询
	 * @param point
	 * @param e
	 */
	@AfterThrowing(pointcut = "execution(* com.king.rest.*.*.*(..))", throwing = "e")
	public void afterThrowing(JoinPoint point, Exception e) {
		// 执行方法
		String exception = null;
		String stackTrace = null;
		String username = null;
		try {
			if (ShiroUtils.getSubject().getPrincipal() != null) {
				username = ((SysUser) ShiroUtils.getSubject().getPrincipal()).getUsername();
			}
			if (e.toString().contains("RRException")) {//自定义异常
				if (e.getMessage().contains("服务调用时") && e.getMessage().contains("请联系管理员")) {// 由rpc异常返回
					exception = e.getMessage().substring(e.getMessage().indexOf("服务调用时"),
							e.getMessage().indexOf("，请联系管理员"));
				} else {
					stackTrace = ExceptionUtils.makeStackTrace(e);
					exception = e.toString();
				}
			} else {// 本地异常
				stackTrace = ExceptionUtils.makeStackTrace(e);
				exception = e.toString();
				addExceptionLog(stackTrace, point, "portal-web", UUID.randomUUID().toString());
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("msg", exception);
			saveSysLog(point, jsonObject, username, true);
			
		} catch (Throwable e1) {
			logger.error("未知错误", e1);
		//	e1.printStackTrace();
		}
	}

	@AfterReturning(pointcut = "logPointCut()", returning = "result")
	public Object afterReturning(JoinPoint point, Object result) {
		// 执行方法
		Boolean isSave=true;
		String username = null;
		Object data=null;
		if (ShiroUtils.getSubject().getPrincipal() != null) {
			username = ((SysUser) ShiroUtils.getSubject().getPrincipal()).getUsername();
		}
		 try {//校验返回数据是否json格式。	
			 JSONObject.parseObject(StringToolkit.getObjectString(JSONObject.toJSON(result)));
		        data =result;
		   } catch (Exception e) {
			   data= result;
		       isSave= false;
		  }
		 if(isSave){
			 JSONObject jsonObject = (JSONObject) JSONObject.toJSON(result);
				// 保存日志
			saveSysLog(point, jsonObject, username,true);
		 }else{
			 saveSysLog(point, data, username,false);
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
	private void saveSysLog(JoinPoint joinPoint, Object object, String username,Boolean formJson) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		SysLog sysLog = new SysLog();

		com.king.common.annotation.Log log = method.getAnnotation(com.king.common.annotation.Log.class);
		if (log != null) {
			// 注解上的描述
			sysLog.setOperation(log.value());
		}
		String data = null;
		String status=null;
		if(formJson){
			 JSONObject jsonObject = (JSONObject) JSONObject.toJSON(object);
			 data = jsonObject.getString("data");
			 if(jsonObject.getString("msg")!=null?jsonObject.getString("msg").equals("success"):false){
				 status="success";
			 }else{
				 data=jsonObject.getString("msg");
				 status="error";
			 }
		}else{
			data=StringToolkit.getObjectString(JSONArray.fromObject(object));
			status="success";
		}

		// 请求的方法名
		String methodName = signature.getName();
		sysLog.setMethod(joinPoint.getSignature()+"");
		sysLog.setResult(data);
		sysLog.setStatus(status);
		// 请求的参数
		Object[] args = joinPoint.getArgs();
		String params = null;
		try {
			params = new Gson().toJson(args[0]);
			sysLog.setParams(params);
		} catch (Exception e) {
		//	logger.info(e.getMessage());
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
	

	/**
	 * 异常保存
	 * @param errMsg
	 * @param joinPoint
	 * @param appcode
	 * @param serialNo
	 * @throws Throwable
	 */
	private void addExceptionLog(String errMsg, JoinPoint joinPoint,String appcode, String serialNo) throws Throwable {
		try {
			ExceptionLogVO vo = new ExceptionLogVO();
			
			String apiName = joinPoint.getTarget().getClass().getName() + "#" + joinPoint.getSignature().getName();
			Object[] args = joinPoint.getArgs();
			try {
				String params = new Gson().toJson(args[0]);
				vo.setOutputData(StringToolkit.getObjectString(joinPoint.getTarget()));
				vo.setInputData(params);
			} catch (Exception e) {

			}
			String logCode = appcode + "-" + DateTimeUtils.currentTimeMillis();
			vo.setAppCode(appcode);
			vo.setSeriaNo(serialNo);
			vo.setApiName(apiName);
			vo.setCreateTime(new Date());
			vo.setIp(ipAddress);
			vo.setLogCode(logCode);
			vo.setExceptionMsg(errMsg);
			exceptionLogRepo.insert(vo);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//本机IP
	static {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			ipAddress = inetAddress.getHostAddress();
			configs = new PropertiesConfiguration("settings.properties");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}
