package com.chinatower.platform.mapper;

import com.chinatower.platform.entity.WaterLevelInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: WaterLevelMapper
 * @description: 水位球服数据传输层
 * @author: fengty
 * @create: 2024-12-13 16:10
 */
@Mapper
public interface WaterLevelMapper {

    List<WaterLevelInfo> exportWaterLevel(WaterLevelInfo waterLevelRequestVO);
}
