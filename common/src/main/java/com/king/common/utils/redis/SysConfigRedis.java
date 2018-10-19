package com.king.common.utils.redis;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.king.dal.gen.model.smp.SysConfig;


/**
 * 系统配置Redis
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@Component
public class SysConfigRedis {
    @Autowired
    private RedisUtils redisUtils;

    public void saveOrUpdate(SysConfig config) {
        if(config == null){
            return ;
        }
        String key = RedisKeys.getSysConfigKey(config.getKey());
        redisUtils.set(key, config);
        if(!config.getStatus()){
        	redisUtils.delete(key);
        }
    }

    public void delete(String configKey) {
        String key = RedisKeys.getSysConfigKey(configKey);
        redisUtils.delete(key);
    }

    public SysConfig get(String configKey){
        String key = RedisKeys.getSysConfigKey(configKey);
        return redisUtils.get(key, SysConfig.class);
    }
}
