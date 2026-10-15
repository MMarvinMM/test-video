package com.chinatower.platform.mapper;


import com.chinatower.platform.entity.RadarAlarmBlackWhiteExportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RadarAlarmConfigExportMapper {

    List<RadarAlarmBlackWhiteExportVO> getRadarAlarmBlackWhiteExportList(@Param("templateId") String templateId,
                                                                         @Param("listType") Integer listType);
}