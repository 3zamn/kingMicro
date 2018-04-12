package com.king.services.spi;


import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.api.smp.ShiroService;
import com.king.api.smp.SysRoleService;
import com.king.api.smp.SysUserService;
import com.king.common.annotation.DataFilter;
import com.king.common.utils.Constant;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.RedisKeys;
import com.king.common.utils.RedisUtils;
import com.king.common.utils.SpringContextUtils;
import com.king.common.utils.TokenGenerator;
import com.king.dal.gen.model.smp.SysUser;
import com.king.dal.gen.model.smp.SysUserToken;
import com.king.dal.gen.service.BaseServiceImpl;
import com.king.dao.SysUserDao;

/**
 * 系统用户
 * @author King chen
 * @email 396885563@qq.com
 * @date 2017年12月29日
 */
@Transactional
@Service("sysUserService")
public class SysUserServiceImpl extends BaseServiceImpl<SysUser> implements SysUserService  {

	@Autowired
	private TokenGenerator tokenGenerator;
	@Autowired
	private SysUserDao sysUserDao;
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private ShiroService shiroService;

	
	@Transactional(readOnly = true)
	public List<String> queryAllPerms(Long userId) {
		return sysUserDao.queryAllPerms(userId);
	}

	@Transactional(readOnly = true)
	public List<Long> queryAllMenuId(Long userId) {
		return sysUserDao.queryAllMenuId(userId);
	}

	@Transactional(readOnly = true)
	public SysUser queryByUserName(String username) {
		return sysUserDao.queryByUserName(username);
	}
	
	@Transactional(readOnly = true)
	@DataFilter(tableAlias = "u", user = false)
	public List<SysUser> queryList(Map<String, Object> map){
		return sysUserDao.queryList(map);
	}
	
	@Transactional(readOnly = true)
	@DataFilter(tableAlias = "u", user = false)
	public int queryTotal(Map<String, Object> map) {
		return sysUserDao.queryTotal(map);
	}

	@Override
	public void save(SysUser user) {
		user.setCreateTime(new Date());
		//sha256加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		user.setSalt(salt);
		user.setPassword(new Sha256Hash(user.getPassword(), user.getSalt()).toHex());
		sysUserDao.save(user);
		
		//保存用户与角色关系
		sysRoleService.saveOrUpdate_R_U(user.getUserId(), user.getRoleIdList());
	}

	@Override
	public void update(SysUser user) {
		if(StringUtils.isBlank(user.getPassword())){
			user.setPassword(null);
		}else{
			user.setPassword(new Sha256Hash(user.getPassword(), user.getSalt()).toHex());
		}
		sysUserDao.update(user);	
		//保存用户与角色关系
		sysRoleService.saveOrUpdate_R_U(user.getUserId(), user.getRoleIdList());
		String permKey =RedisKeys.getPermsKey(user.getUserId(),user.getToken());
    	RedisUtils redisUtils=SpringContextUtils.getBean(RedisUtils.class);
    	String pattern = RedisKeys.getPermsKey(user.getUserId(),"");
    	Set<String> permKeys=redisUtils.likeKey(pattern);
    	Iterator<String> its = permKeys.iterator();  
    	while (its.hasNext()) {
    		permKey=its.next();
    		redisUtils.delete(permKey);
        	Set<String> perms=shiroService.getUserPermissions(user.getUserId(), false,user.getToken());
        	Iterator<String> it = perms.iterator();  
        	while (it.hasNext()) {  
        	  redisUtils.sset(permKey, it.next(),Constant.TOKEN_EXPIRE/1000);
        	} 
      	}     	
	}

	@Override
	public int updatePassword(Long userId, String password, String newPassword) {
		Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("password", password);
		map.put("newPassword", newPassword);
		return sysUserDao.updatePassword(map);
	}


	@Override
	public JsonResponse createToken(long userId) {
		//生成一个token
		String token = TokenGenerator.generateValue();

		//当前时间
		Date now = new Date();
		//过期时间
		Date expireTime = new Date(now.getTime() + Constant.TOKEN_EXPIRE);

		//判断是否生成过token
		SysUserToken tokenEntity =  new SysUserToken();
		tokenEntity.setUserId(userId);
		tokenEntity.setToken(token);
		tokenEntity.setUpdateTime(now);
		tokenEntity.setExpireTime(expireTime);
		tokenGenerator.saveOrUpdate(tokenEntity);
		//缓存权限
		String permKey =RedisKeys.getPermsKey(userId,token);
    	RedisUtils redisUtils=SpringContextUtils.getBean(RedisUtils.class);
    	redisUtils.delete(permKey);
    	Set<String> perms=shiroService.getUserPermissions(userId, false,token);
    	Iterator<String> it = perms.iterator();  
    	while (it.hasNext()) {  
    	  redisUtils.sset(permKey, it.next(),Constant.TOKEN_EXPIRE/1000);
    	}   

		JsonResponse r = JsonResponse.success().put("token", token).put("expire", Constant.TOKEN_EXPIRE/1000);

		return r;
	}

	@Override
	public void logout(SysUserToken token) {
		String permsKey = RedisKeys.getPermsKey(token.getUserId(),token.getToken());
		RedisUtils redisUtils=SpringContextUtils.getBean(RedisUtils.class);
		redisUtils.delete(permsKey);
		tokenGenerator.delete(token.getToken());
	}
}
