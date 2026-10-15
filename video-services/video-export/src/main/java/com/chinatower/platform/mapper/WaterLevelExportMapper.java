package com.chinatower.platform.mapper;

import com.chinatower.platform.entity.WaterLevelQueryVO;

import java.util.List;

public interface WaterLevelExportMapper {

    List<WaterLevelQueryVO> queryWaterLevelDeviceInfoList(WaterLevelQueryVO waterLevelQueryVO);
}
