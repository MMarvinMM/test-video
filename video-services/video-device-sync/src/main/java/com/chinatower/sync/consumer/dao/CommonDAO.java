package com.chinatower.sync.consumer.dao;


import com.chinatower.sync.consumer.entity.AlgorithmInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 喇叭管理
 */
@Mapper
public interface CommonDAO {



    int updateAlgrithmInfoBatch(@Param("list") List<AlgorithmInfoDO> list);



}
