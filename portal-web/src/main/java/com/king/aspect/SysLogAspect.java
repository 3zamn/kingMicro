package com.king.aspect;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.king.common.annotation.Log;
import com.king.common.mongodb.log.model.ExceptionLogVO;
import com.king.common.mongodb.log.model.SysLogVO;
import com.king.common.mongodb.log.repo.ExceptionLogRepo;
import com.king.common.mongodb.log.repo.SysLogRepo;
import com.king.common.utils.exception.ExceptionUtils;
import com.king.common.utils.pattern.StringToolkit;
import com.king.common.utils.spring.SpringContextUtils;
import com.king.dal.gen.model.smp.SysUser;
import com.king.dal.gen.service.BaseService;
import com.king.utils.HttpContextUtils;
import com.king.utils.IPUtils;
import com.king.utils.ShiroUtils;

import io.swagger.annotations.ApiModelProperty;
import net.sf.json.JSONArray;


/**
 * 系统操作日志，切面处理
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@Aspect
@Component
public class SysLogAspect {
	@Autowired
	private SysLogRepo sysLogRepo;
	@Autowired
	private ExceptionLogRepo exceptionLogRepo;
	private static String ipAddress = "127.0.0.1";
	private static Configuration configs;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static ExecutorService executorService =Executors. newFixedThreadPool(20);//后面适当调整大小
	
	@Pointcut("@annotation(com.king.common.annotation.Log)")
	public void logPointCut() {
		//日志切面
	}

	/**
	 * 记录操作异常日志--错误流水号方便去mongodb查询
	 * @param point
	 * @param e
	 */
	@AfterThrowing(pointcut = "execution(* com.king.rest.*.*.*(..))", throwing = "e")
	public void afterThrowing(JoinPoint point, Exception e) {
		// 执行方法
		String exception = null;
		String stackTrace = null;
		String username = null;
		String seriNO=null;
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
				seriNO=UUID.randomUUID().toString();
				exception = "异常"+"【"+seriNO+"】"+e.toString();
				addExceptionLog(stackTrace, point, "portal-web", seriNO);
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("msg", exception);
			saveSysLog(point, jsonObject, username, true);
			
		} catch (Throwable e1) {
			logger.error("未知错误", e1);
		}
	}

	/**
	 * 操作日志记录-异步写入
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Around("logPointCut()")
	public Object Around(ProceedingJoinPoint  point) throws Throwable {
		// 执行方法
		Object result=null;
		Boolean isSave=true;
		String username = null;
		Object data=null;
		String params = null;
		
		MethodSignature signature = (MethodSignature) point.getSignature();
		Method method = signature.getMethod();
		BaseService service=null;	
		Object[] args = point.getArgs();
		try {		
			com.king.common.annotation.Log log = method.getAnnotation(com.king.common.annotation.Log.class);
			if (log != null) {
				if(log.update() || log.delete()){//更新操作					 
					service =(BaseService)SpringContextUtils.getBean(log.serviceClass());	
				}
			}
			if(service!=null&&args!=null){
				params=record(args[0], service,log);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		result=point.proceed();
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
		 HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
		 String ip=IPUtils.getIpAddr(request);
		 if(isSave){
			 JSONObject jsonObject = (JSONObject) JSONObject.toJSON(result);
			 jsonObject.put("result", params);
				// 保存日志
			 executorService.execute(new SaveLog(point, jsonObject, username,ip,true));
		 }else{
			 executorService.execute(new SaveLog(point, data, username,ip,false));
		 }
		 
		return result;
	}

	/**
	 * 保存系统操作日志存mongodb
	 * 
	 * @param joinPoint
	 * @param jsonObject
	 * @param username
	 */
	public class SaveLog implements Runnable {
		private ProceedingJoinPoint joinPoint;
		private Object object;
		private String username;
		private Boolean formJson;
		private String ip;
		
		public  SaveLog(ProceedingJoinPoint joinPoint,Object object,String username,String ip,Boolean formJson) {
			this.joinPoint=joinPoint;
			this.object=object;
			this.username=username;
			this.ip=ip;
			this.formJson=formJson;	
		}

		@Override
		public void run() {
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();
			SysLogVO sysLog = new SysLogVO();
			com.king.common.annotation.Log log = method.getAnnotation(com.king.common.annotation.Log.class);
			if (log != null) {
				sysLog.setOperation(log.value());
			}
			String data = null;
			String status=null;
			String params = null;
			if(object!=null){
				if(formJson){
					 JSONObject jsonObject = (JSONObject) JSONObject.toJSON(object);
					 if(jsonObject!=null){
						 data = jsonObject.getString("data");
					 }
					 if(jsonObject.getString("msg")!=null?jsonObject.getString("msg").equals("success"):false){
						 status="success";
						 if(jsonObject.getString("result")!=null){
							 data=jsonObject.getString("result");
						 }					 
					 }else{
						 data=jsonObject.getString("msg");
						 status="error";
					 }
				}else{
					data=StringToolkit.getObjectString(JSONArray.fromObject(object));
					status="success";
				}
			}
			Object[] args = joinPoint.getArgs();
			try {
				params = new Gson().toJson(args[0]);
				sysLog.setParams(params);
			} catch (Exception e) {
			//	logger.error(e.getMessage());
			}
			String methodName = signature.getName();
			sysLog.setMethod(joinPoint.getSignature()+"");
			sysLog.setResult(data);
			sysLog.setStatus(status);	
			sysLog.setIp(ip);
			if (username == null) {
				if (params != null) {// 登录
					sysLog.setUsername(params.replaceAll("\"", ""));
				} else if (data != null && methodName.equals("logout")) {// 退出登录
					sysLog.setUsername(data);
				}else if (data != null && methodName.equals("login")) {//登录
					sysLog.setUsername(StringToolkit.getObjectString(args[0]));
				}
			} else {
				sysLog.setUsername(username);
			}
			sysLog.setCreateDate(new Date());
			sysLogRepo.insert(sysLog);		
		}		
	}
	
	/**
	 * 保存系统操作日志存
	 * 
	 * @param joinPoint
	 * @param jsonObject
	 * @param username
	 */
	private void saveSysLog(JoinPoint joinPoint, Object object, String username,Boolean formJson) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		SysLogVO sysLog = new SysLogVO();

		com.king.common.annotation.Log log = method.getAnnotation(com.king.common.annotation.Log.class);
		if (log != null) {
			sysLog.setOperation(log.value());
		}
		String data = null;
		String status=null;
		if(object!=null){
			if(formJson){
				 JSONObject jsonObject = (JSONObject) JSONObject.toJSON(object);
				 if(jsonObject!=null){
					 data = jsonObject.getString("data");
				 }
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
		}
		String methodName = signature.getName();
		sysLog.setMethod(joinPoint.getSignature()+"");
		sysLog.setResult(data);
		sysLog.setStatus(status);
		Object[] args = joinPoint.getArgs();
		String params = null;
		try {
			params = new Gson().toJson(args[0]);
			sysLog.setParams(params);
		} catch (Exception e) {
		//	logger.error(e.getMessage());
		}
		HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
		sysLog.setIp(IPUtils.getIpAddr(request));
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
		sysLogRepo.insert(sysLog);
	}
	
	/**
	 * 反射对比变更的内容
	 * @param old_object
	 * @param new_object
	 * @return
	 */
	@SuppressWarnings({"rawtypes" })
	public <T> String record(Object new_object, BaseService service,Log log) {
		StringBuffer result= new StringBuffer();
		try {
			Field[] fields = new_object.getClass().getDeclaredFields();
			Object old_object = null;
			for (Field field : fields) {
				field.setAccessible(true); // 设置些属性是可以访问的
				if (field.getAnnotation(Id.class) != null) {//主键Id
					old_object = service.queryObject(field.get(new_object));
					break;//只取第一个主键注解
				}
			}		
			if(log.update()){//更新
				Field[] fs = old_object.getClass().getDeclaredFields();	
				for (Field field : fs) {
					field.setAccessible(true); // 设置些属性是可以访问的
					Object val_old = field.get(old_object);// 得到此属性的修改前值
					Object val_new = field.get(new_object);// 得到此属性的修改后值	
					if(val_old!=null&&val_new!=null&&!val_old.equals(val_new)){
						ApiModelProperty attr=field.getAnnotation(ApiModelProperty.class);
						result.append("【"+attr.value()+"】"+"从"+"【"+val_old+"】"+"改为了"+"【"+val_new+"】;");
					//	System.getProperty("line.separator");//换行
					}			
				}
			}else if(log.delete()){//删除
				result.append("删除了");
				if (new_object.getClass().isArray()) {
				    int length = Array.getLength(new_object);
				    result.append(length+"条记录【");
				    Object[] os = new Object[length];
				    for (int i = 0; i < os.length; i++) {
				        os[i] = Array.get(new_object, i);
				        old_object = service.queryObject(os[i]);
				        result.append(new Gson().toJson(old_object)+"；");
				    }
				}
				result.append("】");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result.toString();
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
			//	logger.error(e.getMessage());
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
