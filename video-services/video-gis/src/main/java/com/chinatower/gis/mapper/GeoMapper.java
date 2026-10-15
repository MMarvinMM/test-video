package com.chinatower.gis.mapper;

import com.chinatower.gis.dataobject.Latitude;
import com.chinatower.gis.dto.ScreenDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName GeoMapper
 * @Description
 * @Author chenshaowu
 * @Date 2022/2/18
 **/

@Mapper
public interface GeoMapper {

    List<Latitude> getNumByProvinceTT(@Param("param") ScreenDTO screenDTO);

    List<String> getSubLevelCode(List<String> provinceList);

    List<Latitude> getNumByCityTT(@Param("param") ScreenDTO screenDTO);
}
