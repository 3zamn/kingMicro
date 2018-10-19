package com.king.rest.smp;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.king.api.smp.SysUserService;
import com.king.common.annotation.Log;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.network.NetUtils;
import com.king.common.utils.pattern.StringToolkit;
import com.king.common.utils.redis.RedisKeys;
import com.king.common.utils.redis.RedisUtils;
import com.king.common.utils.redis.TokenGenerator;
import com.king.common.utils.security.SecurityUtil;
import com.king.common.utils.security.crypto.Sha256Hash;
import com.king.dal.gen.model.smp.SysUser;
import com.king.dal.gen.model.smp.SysUserToken;
import com.king.utils.AbstractController;
import com.king.utils.GatewayUtils;
import com.king.utils.HttpContextUtils;
import com.king.utils.IPUtils;
import com.king.utils.ShiroUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * 登录相关
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@RestController
@Api(value = "系统登录", description = "系统登录")
public class SysLoginController extends AbstractController {
	@Autowired
	private Producer producer;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private TokenGenerator tokenGenerator;
	@Autowired
	private RedisUtils redisUtils;
	private Logger logger = LoggerFactory.getLogger(getClass());

	
	/**
	 * 验证码
	 */
	@SuppressWarnings("deprecation")
	@ApiOperation(value = "获取验证码")
	@GetMapping("captcha.jpg")
	public void captcha(HttpServletResponse response)throws ServletException, IOException {
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setContentType("image/jpeg");
		//生成文字验证码
		String text = producer.createText();
		//生成图片验证码
		BufferedImage image = producer.createImage(text);
		//保存到shiro session
		ShiroUtils.setSessionAttribute(Constants.KAPTCHA_SESSION_KEY, text);
		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(image, "jpg", out);
		IOUtils.closeQuietly(out);
	}

	/**
	 * 登录
	 */
	@Log("用户登录")
	@ApiOperation(value = "用户登录", notes = "输入用户名、验证码和密码登录")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK",response=SysUser.class,responseContainer="sysUser"),@ApiResponse(code = 405, message = "输入登录信息不正确") })
	@PostMapping("/sys/login")
	public Map<String, Object> login(String username, String password, String captcha)throws IOException {
		String kaptcha = ShiroUtils.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
		if(captcha==null || !captcha.equalsIgnoreCase(kaptcha)){
			return JsonResponse.error(405,"验证码不正确");
		}
		SysUser user = sysUserService.queryByUserName(username);
		String PW=user!=null?new Sha256Hash(password, user.getSalt()).toHex():null;
		HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
		// 获取IP地址,如果出现登录卡顿请去掉
		String ip =IPUtils.getIpAddr(request);
		String userAgent=NetUtils.getUserAgent(request.getHeader("User-Agent"));
		String errorIPKey= RedisKeys.getErrorIPKey(ip, username);
		String value=redisUtils.get(errorIPKey);
		String errorKey= RedisKeys.getLoginKey(username);
		String errorValue=redisUtils.get(errorKey);
		if(value!=null && errorValue!=null) {//防通过代理ip方式暴力破解、限制同一ip错误数次/同一帐号错误次数
			if(Integer.parseInt(value)>Constant.LOGIN_IP_COUNT || Integer.parseInt(value)>Constant.LOGIN_COUNT) {
				return JsonResponse.error(408,"错误次数过多！锁住30分钟,请稍后重试","连续输出登录错误次数过多！锁住30分钟、稍后重试。");
			}
		}
		if(user == null || !user.getPassword().equals(PW)) {
			AtomicInteger error_count = new AtomicInteger(1);
			if(value!=null) {//同一个IP同一个帐号							
				Integer count =Integer.parseInt(value);
				AtomicInteger countValue = new AtomicInteger(count); 
				countValue.getAndIncrement();
				error_count=countValue;
				redisUtils.getset(errorIPKey, countValue, Constant.HALF_HOUR);
				logger.error("用户:"+username+",IP:"+ip+"连续登录错误数次："+countValue);
			}else {
				logger.error("用户:"+username+",IP:"+ip+"连续登录错误数次："+1);
				redisUtils.set(errorIPKey, 1, Constant.HALF_HOUR);
			}
			if(errorValue!=null) {//同一个帐号							
				Integer errorCount =Integer.parseInt(errorValue);
				AtomicInteger errors= new AtomicInteger(errorCount); 
				errors.getAndIncrement();			
				redisUtils.getset(errorKey, errors, Constant.HALF_HOUR);	
				logger.error("该帐号:"+username+"连续登录错误数次："+errors);
			}else {	
				redisUtils.set(errorKey, 1, Constant.HALF_HOUR);
				logger.error("该帐号:"+username+"连续登录错误数次："+1);
			}	
			return JsonResponse.error(405,"账号或密码不正确","用户:"+username+",IP:"+ip+"连续登录错误数次："+error_count);
		}
		//账号锁定
		if(user.getStatus() == false){
			return JsonResponse.error(405,"账号已被锁定,请联系管理员",user.getUsername()+"：该账号已被锁定,请联系管理员");
		}
		if(value!=null) {//登录成功清除错误次数
			redisUtils.delete(errorIPKey);
		}
		if(errorValue!=null) {//登录成功清除错误次数
			redisUtils.delete(errorKey);
		}
		JsonResponse r =new JsonResponse();
		String sessionId =HttpContextUtils.getHttpServletRequest().getSession().getId();		
		String rawKey =RedisKeys.getReqId(SecurityUtil.encryptSHA(sessionId+ kaptcha));	
		Boolean exsit =redisUtils.luaScript_Setnx(rawKey,rawKey,rawKey);
		if (exsit) {
			r.put("msg", "请不要重复点击登录");
			r.put("code",500);
		}else{
			r= sysUserService.createToken(user.getUserId(),ip,userAgent);
			/*token同步到网关-后面优化网关直接从redis中取token*/
			new GatewayUtils().post_key_auth(JSONObject.parseObject(StringToolkit.getObjectString(r.get("data"))).getString("token"));
		}
		
		return r;
	}


	/**
	 * 退出
	 */
	@Log("退出登录")
	@ApiOperation(value = "退出登录")
	@PostMapping("/sys/logout")
	public JsonResponse logout() {
		String currentUser=getUser().getUsername();
		SysUserToken sysUserToken = tokenGenerator.get(getUser().getToken());
		sysUserService.logout(sysUserToken);
		ShiroUtils.logout();
		return JsonResponse.success(currentUser);
	}
	
}
