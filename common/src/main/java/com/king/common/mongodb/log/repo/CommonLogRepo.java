package com.king.common.mongodb.log.repo;

import com.king.common.mongodb.log.model.CommonLogVO;
import com.king.common.mongodb.mongo.BaseMongoRepository;

/**
 * 通用日志接口
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月19日
 */
public interface CommonLogRepo extends BaseMongoRepository<CommonLogVO, String> {

}
