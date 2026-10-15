package com.chinatower.platform.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.WaterLevelQueryVO;

import javax.servlet.http.HttpServletResponse;

public interface WaterLevelExportService {

    Result exportLatestWaterLevel(WaterLevelQueryVO waterLevelQueryVO, HttpServletResponse response);

    Result exportWaterLevelRecord(WaterLevelQueryVO waterLevelQueryVO, HttpServletResponse response);
}
