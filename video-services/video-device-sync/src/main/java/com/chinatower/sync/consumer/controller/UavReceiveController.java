package com.chinatower.sync.consumer.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.UavFlyReq;
import com.chinatower.device.entity.dto.UavUfcpInfoReq;
import com.chinatower.sync.consumer.entity.UavAlgorithmRequest;
import com.chinatower.sync.consumer.entity.UavStatusRequest;
import com.chinatower.sync.consumer.service.UavReceiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author WLF
 * @description
 * @date 2024/11/9 22:21
 */
@RestController
@Slf4j
@RequestMapping("/uavReceive")
public class UavReceiveController {
    @Resource
    UavReceiveService uavReceiveService;

    /**
     * 无人机航线计划任务状态变更
     *
     * @param request
     * @return
     */
    @PostMapping("/receiveTaskStatus")
    public Result receiveAirLineTaskStatus(@RequestBody UavStatusRequest request) {
        uavReceiveService.receiveAirLineTaskStatus(request);
        return Result.success("");
    }


    /**
     * 无人机算法任务状态同步
     *
     * @param request
     * @return
     */
    @PostMapping("/receiveAlgorithmStatus")
    public Result editAirTaskStatus(@RequestBody UavAlgorithmRequest request) {
        uavReceiveService.editAirTaskStatus(request);
        return Result.success("");
    }


    /**
     *
     *
     * @param uavFlyReq
     * @return
     */
    @PostMapping("/uavUfcpInfo")
    public Result uavUfcpInfo(@RequestBody UavUfcpInfoReq uavFlyReq) {
        UavFlyReq uavFlyReqs =  uavFlyReq.getFly();
        uavFlyReqs.setDeviceCode(uavFlyReq.getDeviceCode());
        uavReceiveService.insertUavUfcpInfo(uavFlyReqs);
        return Result.success("");
    }
}
