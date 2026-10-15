package com.chinatower.gis.modules.gis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.gis.api.entity.layer.GetFeaturesByFilterAPIReq;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: wjd
 * @Description:
 * @Date: Created in 15:55 2022/3/14
 * @Modified by:
 */
public interface GisLayerMapper {

    @DS("postgres")
    String getFeaturesByFilterAPI(@Param("params") GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq);

    @DS("postgres")
    String getFeaturesByFilterAPI2(@Param("params") GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq);

    @DS("postgres")
    Integer getFeaturesByFilterAPICount(@Param("params") GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq);

    @DS("postgres")
    String tableField(@Param("params") GetFeaturesByFilterAPIReq getFeaturesByFilterAPIReq);


}
