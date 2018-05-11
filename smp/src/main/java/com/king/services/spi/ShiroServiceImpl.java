package com.king.services.spi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.api.smp.ShiroService;
import com.king.common.utils.constant.Constant;
import com.king.common.utils.redis.RedisKeys;
import com.king.common.utils.redis.RedisUtils;
import com.king.common.utils.spring.SpringContextUtils;
import com.king.dal.gen.model.smp.SysMenu;
import com.king.dal.gen.model.smp.SysUser;
import com.king.dao.SysMenuDao;
import com.king.dao.SysUserDao;

@Service("shiroService")
public class ShiroServiceImpl implements ShiroService {
    @Autowired
    private SysMenuDao sysMenuDao;
    @Autowired
    private SysUserDao sysUserDao;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = true)
    public Set<String> getUserPermissions(long userId,boolean cache,String token) {
        List<String> permsList;

        //系统管理员，拥有最高权限
        if(userId == Constant.SUPER_ADMIN){
            List<SysMenu> menuList = sysMenuDao.queryList(new HashMap<>());
            permsList = new ArrayList<>(menuList.size());
            for(SysMenu menu : menuList){
                permsList.add(menu.getPerms());
            }
        }else{
            permsList = sysUserDao.queryAllPerms(userId);
        }
        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        if(cache){     	
        	String permKey =RedisKeys.getPermsKey(userId,token);
        	RedisUtils redisUtils=SpringContextUtils.getBean(RedisUtils.class);
        	permsSet = (Set)redisUtils.sget(permKey);
        }else{
        	 for(String perms : permsList){
                 if(StringUtils.isBlank(perms)){
                     continue;
                 }
                 permsSet.addAll(Arrays.asList(perms.trim().split(",")));
             }
        }
       
        return permsSet;
    }

    @Transactional(readOnly = true)
    public SysUser queryUser(Long userId) {
        return sysUserDao.queryObject(userId);
    }
}
