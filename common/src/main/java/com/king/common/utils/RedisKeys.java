package com.king.common.utils;

/**
 *  Redis所有Keys
 * @author King chen
 * @date 2017年12月25日
 */
public class RedisKeys {

    public static String getSysConfigKey(String key){
        return "sys:config:" + key;
    }

    public static String getShiroSessionKey(String key){
        return "sessionid:" + key;
    }
    
    public static String getTokenKey(String key){
        return "token:" + key;
    }
    
    public static String getEnttyKey(String key){
        return "entty:" + key;
    }
    
    public static String getKaptchaKey(String key){
        return "kaptcha:" + key;
    }
    
    public static String getSerialNoKey(String key){
        return "serialNo:" + key;
    }
}
