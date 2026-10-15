package com.chinatower.device.service;

import com.chinatower.device.dto.WaterLevelLatestMongo;
import com.chinatower.device.dto.WaterLevelQueryVO;
import com.chinatower.device.dto.WaterLevelRecordMongo;
import com.chinatower.mongodb.request.Page;

public interface WaterLevelQueryService {

    Page<WaterLevelLatestMongo> queryLatestWaterLevelPageData(WaterLevelQueryVO waterLevelVO);

    Page<WaterLevelRecordMongo> queryWaterLevelRecordPageData(WaterLevelQueryVO waterLevelQueryVO);
}
