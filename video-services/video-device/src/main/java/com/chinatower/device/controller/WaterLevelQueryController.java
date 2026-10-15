package com.chinatower.device.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.device.dto.WaterLevelLatestMongo;
import com.chinatower.device.dto.WaterLevelQueryVO;
import com.chinatower.device.dto.WaterLevelRecordMongo;
import com.chinatower.device.service.WaterLevelQueryService;
import com.chinatower.mongodb.request.Page;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: 水位监测查询controller
 * @Author: wangyt
 * @CreateTime: 2024-12-14  16:42
 * @Description: TODO
 * @Version: 1.0
 */
@Api(tags = "水位")
@RestController
@Slf4j
public class WaterLevelQueryController {

    @Autowired
    private WaterLevelQueryService waterLevelQueryService;

    /**
     * @Description: 最新水位查询接口
     * @Author: wangyt
     * @Date: 2024/12/17
     * @param waterLevelQueryVO
     * @return com.chinatower.mongodb.request.Page<com.chinatower.device.dto.WaterLevelLatestMongo>
     **/
    @PostMapping("/device/video/waterLevel/queryLatestWaterLevelPageData")
    public Result queryLatestWaterLevelPageData(@RequestBody WaterLevelQueryVO waterLevelQueryVO) {
        return Result.data(waterLevelQueryService.queryLatestWaterLevelPageData(waterLevelQueryVO));
    }

    /**
     * @Description: 水位历史记录查询接口
     * @Author: wangyt
     * @Date: 2024/12/17
     * @param waterLevelQueryVO
     * @return com.chinatower.mongodb.request.Page<com.chinatower.device.dto.WaterLevelRecordMongo>
     **/
    @PostMapping("/device/video/waterLevel/queryWaterLevelRecordPageData")
    public Result queryWaterLevelRecordPageData(@RequestBody WaterLevelQueryVO waterLevelQueryVO) {
        return Result.data(waterLevelQueryService.queryWaterLevelRecordPageData(waterLevelQueryVO));
    }
}
