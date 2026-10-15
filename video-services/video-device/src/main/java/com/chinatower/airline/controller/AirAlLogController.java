package com.chinatower.airline.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.airline.service.AirAlgorithmsService;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.AirAlTaskLogReq;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/21 11:25
 */
@RestController
@Api(value = "飞行算法配置模块", tags = "飞行算法配置模块")
@Slf4j
@RequestMapping("/air/algorithm")
public class AirAlLogController {
    @Resource
    private AirAlgorithmsService airAlgorithmsService;

    /**
     * 无人机实时流算法日志补偿重试
     *
     * @param
     * @return
     */
    @PostMapping("/retryAirAlSync")
    public Result retryAirAlSync(@RequestBody List<AirAlTaskLogReq> airAlTaskLogReqs) {
        log.info("AirAlLogController-retryAirAlSync-req:{}", JSON.toJSONString(airAlTaskLogReqs));
        airAlgorithmsService.retryAirAlSync(airAlTaskLogReqs);
        return Result.success("");
    }
}
