package com.king.rest.smp;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.king.common.mongodb.model.SysLogVO;
import com.king.common.mongodb.repo.ExceptionLogRepo;
import com.king.common.utils.JsonResponse;
import com.king.dal.gen.model.Response;
import com.mongodb.BasicDBObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 统计
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年7月27日
 */

@RestController
@Api(value = "数据统计", description = "数据统计")
@RequestMapping("/sys/statistics")
public class StatisticsController {
	@Autowired
	private MongoTemplate  mongoTemplate;
	@Autowired
	private ExceptionLogRepo exceptionLogRepo;
	
	/**
	 * 在线用户统计
	 */
	@ApiOperation(value = "在线用户统计",response=Response.class, notes = "权限编码（sys:statistics:showUser）")
	@GetMapping("/showUser")
//	@RequiresPermissions("sys:statistics:showUser")
	public JsonResponse exceptionList(@RequestParam Map<String, Object> params){
		org.springframework.data.mongodb.core.query.Query  query= new org.springframework.data.mongodb.core.query.Query();
        TypedAggregation<SysLogVO> agg1 = Aggregation.newAggregation(SysLogVO.class,
                Aggregation.group("username")
                    //    sum("age").as("agesum").
                     //   first("age").as("agefirst").
                      //  addToSet("age").as("agess")
                );
        	List<BasicDBObject> results = mongoTemplate.aggregate(agg1, SysLogVO.class.getSimpleName(), BasicDBObject.class).getMappedResults();
		return JsonResponse.success(results);
	}

}
