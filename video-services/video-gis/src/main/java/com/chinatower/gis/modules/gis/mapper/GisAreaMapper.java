package com.chinatower.gis.modules.gis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.gis.api.entity.layer.GetFeaturesByFilterAPIReq;
import com.chinatower.gis.api.entity.layer.StatisticDkmjReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: wjd
 * @Description:
 * @Date: Created in 15:55 2022/3/14
 * @Modified by:
 */
public interface GisAreaMapper {

    @DS("postgres")
    String statisticDkmj(@Param("tableName")String tableName, @Param("featureIDs") List featureIDs, @Param("areaField")String areaField);

    @DS("postgres")
    String statisticGdxzmj(@Param("layerNames")List<String> layerNames, @Param("farmlandType")String farmlandType);

}
