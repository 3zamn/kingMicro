package com.king.common.mongodb.log.repo;

import com.king.common.mongodb.log.model.SysLogVO;
import com.king.common.mongodb.mongo.BaseMongoRepository;

/**
 * 日志仓库接口
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月19日
 */
public interface SysLogRepo extends BaseMongoRepository<SysLogVO, String> {

}
