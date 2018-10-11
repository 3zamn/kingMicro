package com.king.rest.smp;

import java.util.Date;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.king.common.utils.JsonResponse;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.redis.RedisKeys;
import com.king.common.utils.redis.RedisUtils;
import com.king.common.utils.security.SecurityUtil;
import com.king.dal.gen.model.Response;
import com.king.utils.ShiroUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * 产生唯一reqId、用于校验表单重复提交
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年9月29日
 */

@RestController
@Api(value = "产生reqId", description = "产生reqId")
@RequestMapping("/sys/gen")
public class GenRequstIdController {
	
	@Autowired
	private RedisUtils redisUtils;

	/**
	 * 产生唯一reqId、用于校验表单重复提交
	 */
	@ApiOperation(value = "产生唯一reqId",response=Response.class, notes = "权限编码（sys:gen:reqId）")
	@GetMapping("/reqId")
	@RequiresPermissions("sys:gen:reqId")
	public JsonResponse genReqId(){
		String rawKey = ShiroUtils.getUserEntity().getToken()+ new Date().getTime();
		String key =RedisKeys.getReqId(SecurityUtil.encryptSHA(rawKey));
		redisUtils.set(key, ShiroUtils.getUserEntity().getToken(), Constant.PERMS_EXPIRE);
		return JsonResponse.success(key);
	}


}
