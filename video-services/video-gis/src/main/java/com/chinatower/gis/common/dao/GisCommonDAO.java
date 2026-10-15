package com.chinatower.gis.common.dao;

import feign.Param;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GisCommonDAO {
    /**
     * 获取接口路径
     *
     * @param interfaceCode
     * @return
     */
    String getInterfaceUrl(@Param("interfaceCode") String interfaceCode);
}
