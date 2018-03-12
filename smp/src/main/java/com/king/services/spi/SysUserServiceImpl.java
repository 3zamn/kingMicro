package com.king.services.spi;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.king.api.smp.SysRoleService;
import com.king.api.smp.SysUserService;
import com.king.common.annotation.DataFilter;
import com.king.common.utils.Constant;
import com.king.common.utils.JsonResponse;
import com.king.common.utils.TokenGenerator;
import com.king.dal.gen.model.smp.SysUser;
import com.king.dal.gen.model.smp.SysUserToken;
import com.king.dal.gen.service.BaseServiceImpl;
import com.king.dao.SysUserDao;
import com.king.dao.SysUserTokenDao;

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
	private SysUserTokenDao sysUserTokenDao;
	@Value("#{new Boolean('${king.redis.open}')}")
	private Boolean reids_open;
	@Autowired
	private TokenGenerator tokenGenerator;
	@Autowired
	private SysUserDao sysUserDao;
	@Autowired
	private SysRoleService sysRoleService;

	@Override
	public List<String> queryAllPerms(Long userId) {
		return sysUserDao.queryAllPerms(userId);
	}

	@Override
	public List<Long> queryAllMenuId(Long userId) {
		return sysUserDao.queryAllMenuId(userId);
	}

	@Override
	public SysUser queryByUserName(String username) {
		return sysUserDao.queryByUserName(username);
	}
	
	@Override
	@DataFilter(tableAlias = "u", user = false)
	public List<SysUser> queryList(Map<String, Object> map){
		return sysUserDao.queryList(map);
	}
	
	@Override
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
	public SysUserToken queryByUserId(Long userId) {
		return sysUserTokenDao.queryByUserId(userId);
	}

	@Override
	public void saveUserToken(SysUserToken token){
		sysUserTokenDao.save(token);
	}
	
	@Override
	public void updateUserToken(SysUserToken token){
		sysUserTokenDao.update(token);
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
		SysUserToken tokenEntity = queryByUserId(userId);
		if(tokenEntity == null){
			tokenEntity = new SysUserToken();
			tokenEntity.setUserId(userId);
			tokenEntity.setToken(token);
			tokenEntity.setUpdateTime(now);
			tokenEntity.setExpireTime(expireTime);

			//保存token
			saveUserToken(tokenEntity);	
		}else{
			tokenEntity.setToken(token);
			tokenEntity.setUpdateTime(now);
			tokenEntity.setExpireTime(expireTime);

			//更新token
			updateUserToken(tokenEntity);
		}
		if(reids_open){
			tokenGenerator.saveOrUpdate(tokenEntity);
		}

		JsonResponse r = JsonResponse.success().put("token", token).put("expire", Constant.TOKEN_EXPIRE/1000);

		return r;
	}

	@Override
	public void logout(long userId) {
		//生成一个token
		String token = TokenGenerator.generateValue();
		SysUserToken userToken =queryByUserId(userId);
		//修改token
		SysUserToken tokenEntity = new SysUserToken();
		tokenEntity.setUserId(userId);
		tokenEntity.setToken(token);
		updateUserToken(tokenEntity);
		if(reids_open){
			//删除redis中token
			tokenGenerator.delete(userToken.getToken());
		}
	}
}
