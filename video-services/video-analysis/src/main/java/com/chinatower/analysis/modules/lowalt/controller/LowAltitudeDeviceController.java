package com.chinatower.analysis.modules.lowalt.controller;

import com.chinatower.analysis.api.lowalt.domain.DeviceStatsDTO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceDistributionVO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceOnlineRateVO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceStatsVO;
import com.chinatower.analysis.modules.lowalt.service.LowaltDeviceStatsService;
import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 设备统计控制器
 */
@RestController
@RequestMapping("/lowalt/device")
@RequiredArgsConstructor
@Slf4j
public class LowAltitudeDeviceController extends TowerBaseController {

    private final LowaltDeviceStatsService lowaltDeviceStatsService;

    /**
     * 设备数量统计
     */
    @PostMapping("/deviceStatistics")
    public Result<List<DeviceStatsVO>> deviceStatistics(@RequestBody DeviceStatsDTO deviceStatsDTO) {
        List<DeviceStatsVO> result = lowaltDeviceStatsService.deviceStatistics(deviceStatsDTO);
        return Result.data(result);
    }

    /**
     * 设备分布Top5
     */
    @PostMapping("/deviceDistributionTop5")
    public Result<List<DeviceDistributionVO>> getDeviceDistributionTop5(@RequestBody DeviceStatsDTO deviceStatsDTO) {
        List<DeviceDistributionVO> result = lowaltDeviceStatsService.getDeviceDistributionTop5(deviceStatsDTO);
        return Result.data(result);
    }

    /**
     * 设备在线率统计
     */
    @PostMapping("/deviceOnlineRate")
    public Result<List<DeviceOnlineRateVO>> getDeviceOnlineRate(@RequestBody DeviceStatsDTO deviceStatsDTO) {
        List<DeviceOnlineRateVO> result = lowaltDeviceStatsService.getDeviceOnlineRate(deviceStatsDTO);
        return Result.data(result);
    }

}