package com.king.aspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import com.king.common.annotation.DuplicateFilter;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.redis.RedisUtils;
import com.king.utils.HttpContextUtils;

/**
 * 防止表单重复提交切面处理
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年9月30日
 */
@Aspect
@Component
public class DuplicateAspect {

    @SuppressWarnings("rawtypes")
	@Autowired
    private  RedisTemplate redisTemplate;
	@Autowired
	private RedisUtils redisUtils;
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Pointcut("@annotation(com.king.common.annotation.DuplicateFilter)")
	public void aspect() {
		//重复提交过滤切面
	}
	
	/**
	 * 校验reqId合法性
	 * @param point
	 */
	@Around("aspect()")
	public Object Around(ProceedingJoinPoint point) {
		Object result = null;
		Object object = point.getTarget();
		String methodName = point.getSignature().getName();
		Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
		Method method;
		try {
			method = object.getClass().getMethod(methodName, parameterTypes);
			DuplicateFilter duplicate = method.getAnnotation(DuplicateFilter.class);
			if (duplicate != null && duplicate.check()) {
				String reqId = HttpContextUtils.getHttpServletRequest().getHeader("reqId");
				if (StringUtils.isNotBlank(reqId)) {
					Boolean exsit =redisUtils.luaScript_Setnx(reqId,reqId,reqId);
					/*synchronized (this) {//同步效率较低、建议用上面的lua方式执行（有待优化）					
						Boolean exsit = redisUtils.setnx(reqId, new Date().getTime() + "");
						redisUtils.delete(reqId);						
					}*/
					if (exsit) {
						return JsonResponse.error(500, "reqId参数无效 ,请勿重复提交！");
					} else {
						result = point.proceed();
					}
				} else {
					return JsonResponse.error(500, "请求头reqId不能为空！");
				}
			} else {
				result = point.proceed();
			}

		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage());
		} catch (SecurityException e) {
			logger.error(e.getMessage());
		} catch (Throwable e) {
			logger.error(e.getMessage());
		}
		return result;

	}
}
