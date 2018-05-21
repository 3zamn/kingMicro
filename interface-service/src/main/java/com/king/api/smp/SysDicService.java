package com.king.api.smp;


import java.util.List;

import com.king.dal.gen.model.smp.SysDic;
import com.king.dal.gen.model.smp.SysDicTerm;
import com.king.dal.gen.service.BaseService;

/**
 * 数据字典明细表
 * 
 * @author king chen
 * @email 396885563@qq.com
 * @date 2018-05-08 17:26:32
 */
public interface SysDicService extends BaseService<SysDic>{
	
	/**
	 * 根据字典编码查询字典项
	 * @param code
	 * @return
	 */
	List<SysDicTerm> queryDicTerm(Object code);
	
	/**
	 * 根据父节点查询
	 * @param parentd
	 * @return
	 */
	List<SysDic> queryParentList(Object parentId);
}
