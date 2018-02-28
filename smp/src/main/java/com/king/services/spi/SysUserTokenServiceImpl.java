package com.king.services.spi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.king.api.smp.SysUserTokenService;
import com.king.common.utils.Constant;
import com.king.common.utils.R;
import com.king.common.utils.TokenGenerator;
import com.king.dal.gen.model.smp.SysUserToken;
import com.king.dao.SysUserTokenDao;

import java.util.Date;


@Service("sysUserTokenService")
public class SysUserTokenServiceImpl implements SysUserTokenService {
	@Autowired
	private SysUserTokenDao sysUserTokenDao;
	@Value("#{new Boolean('${king.redis.open}')}")
	private Boolean reids_open;
	@Autowired
	private TokenGenerator tokenGenerator;


	@Override
	public SysUserToken queryByUserId(Long userId) {
		return sysUserTokenDao.queryByUserId(userId);
	}

	@Override
	public void save(SysUserToken token){
		sysUserTokenDao.save(token);
	}
	
	@Override
	public void update(SysUserToken token){
		sysUserTokenDao.update(token);
	}

	@Override
	public R createToken(long userId) {
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
			save(tokenEntity);	
		}else{
			tokenEntity.setToken(token);
			tokenEntity.setUpdateTime(now);
			tokenEntity.setExpireTime(expireTime);

			//更新token
			update(tokenEntity);
		}
		if(reids_open){
			tokenGenerator.saveOrUpdate(tokenEntity);
		}

		R r = R.ok().put("token", token).put("expire", Constant.TOKEN_EXPIRE/1000);

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
		update(tokenEntity);
		if(reids_open){
			//删除redis中token
			tokenGenerator.delete(userToken.getToken());
		}
	}
}
