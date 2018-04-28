package com.king.common.mongodb.log.repo;

import org.springframework.stereotype.Repository;

import com.king.common.mongodb.log.model.CommonLogVO;
import com.king.common.mongodb.mongo.BaseMongoRepositoryImpl;

/**
 * 通用日志接口实现类
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月19日
 */
@Repository
public class CommonLogRepoImpl extends BaseMongoRepositoryImpl<CommonLogVO, String> implements CommonLogRepo {


}
