package com.king.utils;

import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.king.api.smp.SysConfigService;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.network.HttpUtils;
import com.king.common.utils.network.ResponseWrap;
import com.king.common.utils.spring.SpringContextUtils;

/**
 * 网关相关操作工具类
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年10月17日
 */
public class GatewayUtils {
	private static Configuration configs ;
	 private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	static {
		try {
			configs = new PropertiesConfiguration("settings.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取网关相关配置
	 * @return
	 */
	public  String gatewayConfig(){

		return (SpringContextUtils.getBean("sysConfigService",SysConfigService.class)).getValue(Constant.GATEWAY_CONFIG);
	}
	
	/**
	 * 同步应用的token到网关认证、同时清楚网关key_auth缓存
	 */
	public void sync_key_auth(){
		String gateway_url=null;
		if(gatewayConfig()!=null){
			JSONObject json = JSONObject.parseObject(gatewayConfig());
			if(json!=null){
				gateway_url = json.getString("gateway_url");
			}
		}	
	}
	
	/**
	 * 发送token到网关
	 */
	public void post_key_auth(String token){
		String gateway_url="";//http://chenhx.cn:7777/key_auth/selectors/fa0ffe2c-aabf-4b53-a626-8319bb2c7fd5/rules
		if(StringUtils.isNotBlank(gatewayConfig())){
			JSONObject json = JSONObject.parseObject(gatewayConfig());
			if(json!=null){
				gateway_url = json.getString("gateway_url");
			}
		}
		if(StringUtils.isNotBlank(gateway_url)){
			JSONObject key_auth= new JSONObject();
			key_auth.put("name", token);
			JSONObject judge= new JSONObject();
			judge.put("type", 2);//0:url；1:header；2:query；3:postparams；4:ip；5:userAgent；6:host；7:referer
			JSONArray conditions= new JSONArray();
			JSONObject condition = new JSONObject();
			condition.put("type", "URI");
			condition.put("operator", "match");
			condition.put("value", "");
			conditions.add(condition);
			judge.put("conditions", conditions);
			key_auth.put("judge", judge);
			JSONObject handle = new JSONObject();
			JSONArray credentials = new JSONArray();
			JSONObject credential = new JSONObject();
			credential.put("type", 1);
			credential.put("key", "token");
			credential.put("target_value", token);
			credentials.add(credential);
			handle.put("credentials", credentials);
			handle.put("code", 401);//网关错误编码
			handle.put("log", true);//是否开启日志
			key_auth.put("handle", handle);
			key_auth.put("enable", true);
			HttpUtils http = HttpUtils.post(gateway_url);
	        http.setContentType("application/x-www-form-urlencoded", Consts.UTF_8);
	        http.addParameter("rule", key_auth.toString());	       
	        String authorization=configs.getString("gateway.authorization");
	        //添加base加密
	        String Basic =com.alibaba.druid.util.Base64.byteArrayToBase64((authorization).getBytes());
	        http.addHeader("Authorization", "Basic "+Basic);
	        ResponseWrap response= http.execute();
	        logger.info("发送token到网关："+response.getString());	    
		}
		
	}
	
	public static void main(String[] args) {
		new GatewayUtils().post_key_auth(UUID.randomUUID().toString());
	}
	
	/**
	 * 更新token到网关
	 */
	public void put_key_auth(){
		
	}
	
	/**
	 * 删除网关token
	 */
	public void delete_key_auth(){
		
		
	}

}
