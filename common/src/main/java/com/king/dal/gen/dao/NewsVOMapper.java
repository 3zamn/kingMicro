package com.demo.dal.gen.dao;

import com.demo.dal.gen.model.NewsVO;
import com.demo.dal.gen.model.NewsVOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface NewsVOMapper {
    int countByExample(NewsVOExample example);

    int deleteByExample(NewsVOExample example);

    int deleteByPrimaryKey(Long id);

    int insert(NewsVO record);

    int insertSelective(NewsVO record);

    List<NewsVO> selectByExampleWithBLOBsWithRowbounds(NewsVOExample example, RowBounds rowBounds);

    List<NewsVO> selectByExampleWithBLOBs(NewsVOExample example);

    List<NewsVO> selectByExampleWithRowbounds(NewsVOExample example, RowBounds rowBounds);

    List<NewsVO> selectByExample(NewsVOExample example);

    NewsVO selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") NewsVO record, @Param("example") NewsVOExample example);

    int updateByExampleWithBLOBs(@Param("record") NewsVO record, @Param("example") NewsVOExample example);

    int updateByExample(@Param("record") NewsVO record, @Param("example") NewsVOExample example);

    int updateByPrimaryKeySelective(NewsVO record);

    int updateByPrimaryKeyWithBLOBs(NewsVO record);

    int updateByPrimaryKey(NewsVO record);
}