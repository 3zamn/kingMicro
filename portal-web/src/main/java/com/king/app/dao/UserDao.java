package com.king.app.dao;

import org.mapstruct.Mapper;

import com.king.app.entity.UserEntity;
import com.king.dal.gen.dao.BaseDao;

/**
 * 用户dao
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月23日
 */
@Mapper
public interface UserDao extends BaseDao<UserEntity> {

    UserEntity queryByMobile(String mobile);
}
