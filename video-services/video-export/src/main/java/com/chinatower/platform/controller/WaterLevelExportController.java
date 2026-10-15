package com.chinatower.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.WaterLevelQueryVO;
import com.chinatower.platform.service.WaterLevelExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName: 水位监测导出controller
 * @Author: wangyt
 * @CreateTime: 2024-12-16  14:38
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@RestController
public class WaterLevelExportController {

    @Autowired
    private WaterLevelExportService waterLevelExportService;

    /**
     * @Description: 最新水位数据导出
     * @Author: wangyt
     * @Date: 2024/12/17
     * @param waterLevelQueryVO
     * @return com.chinatower.common.entity.Result
     **/
    @PostMapping("/device/video/waterLevel/exportLatestWaterLevel")
    public Result exportLatestWaterLevel(@RequestBody WaterLevelQueryVO waterLevelQueryVO, HttpServletResponse response) {
        log.info("导出最新水位数据入参==={}", JSONObject.toJSONString(waterLevelQueryVO));
        return waterLevelExportService.exportLatestWaterLevel(waterLevelQueryVO, response);
    }

    /**
     * @Description: 水位历史记录导出
     * @Author: wangyt
     * @Date: 2024/12/17
     * @param waterLevelQueryVO
     * @return com.chinatower.common.entity.Result
     **/
    @PostMapping("/device/video/waterLevel/exportWaterLevelRecord")
    public Result exportWaterLevelRecord(@RequestBody WaterLevelQueryVO waterLevelQueryVO, HttpServletResponse response) {
        log.info("导出水位历史数据入参==={}", JSONObject.toJSONString(waterLevelQueryVO));
        return waterLevelExportService.exportWaterLevelRecord(waterLevelQueryVO, response);
    }
}
