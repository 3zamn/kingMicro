package com.king.common.mongodb.log.repo;

import com.king.common.mongodb.log.model.ExceptionLogVO;
import com.king.common.mongodb.mongo.BaseMongoRepository;

/**
 * 异常日志仓库接口
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月19日
 */
public interface ExceptionLogRepo extends BaseMongoRepository<ExceptionLogVO, String> {

}
