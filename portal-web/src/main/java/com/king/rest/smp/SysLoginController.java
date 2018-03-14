package com.king.rest.smp;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.king.api.smp.SysUserService;
import com.king.common.annotation.Log;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.ShiroUtils;
import com.king.dal.gen.model.smp.SysUser;

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


	/**
	 * 验证码
	 */
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
		//用户信息
		SysUser user = sysUserService.queryByUserName(username);
		String PW=user!=null?new Sha256Hash(password, user.getSalt()).toHex():null;
		//账号不存在、密码错误
		if(user == null || !user.getPassword().equals(PW)) {
			return JsonResponse.error(405,"账号或密码不正确");
		}

		//账号锁定
		if(user.getStatus() == 0){
			return JsonResponse.error(405,"账号已被锁定,请联系管理员");
		}

		//生成token，并保存到数据库
		JsonResponse r = sysUserService.createToken(user.getUserId());
		
		return r;
	}


	/**
	 * 退出
	 */
	@Log("退出登录")
	@ApiOperation(value = "退出登录")
	@PostMapping("/sys/logout")
	public JsonResponse logout() {	
		sysUserService.logout(getUserId());
		ShiroUtils.logout();
		return JsonResponse.success();
	}
	
}
