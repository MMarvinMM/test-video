package com.chinatower.event.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.HornDeviceDTO;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.event.service.impl.DeviceService;
import com.chinatower.event.vo.entity.DeviceTypeStatisticsVO;
import com.chinatower.event.vo.entity.*;
import com.chinatower.platform.entity.device.RecorderDeviceVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.event.vo.entity.DeviceTypeInfoVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * @author ysc
 * @date 2026/5/7
 */
@Slf4j
@RestController
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    /**
     * 统计摄像机数量
     * @param deviceInfoVO (year 年 month 月 day 日)
     *
     * @return deviceCountTotal 摄像机总数
     * @return devCountOnline 摄像机在线数
     * @return devCountOffline 摄像机离线数
     * @return channelCountTotal 通道总数
     * @return channelCountOnline   通道在线数
     * @return channelCountOffline 通道离线数
     */
    @ApiOperation(value = "统计摄像机数量")
    @PostMapping("/workbench/getDeviceStatistics")
    public Result<IotDeviceStatisticsVo> getDeviceStatistics(@RequestBody DeviceInfoVO deviceInfoVO) {
        return deviceService.getDeviceStatistics(deviceInfoVO);
    }

    @ApiOperation(value = "摄像机类别数量信息查询")
    @PostMapping("/workbench/getDeviceTypeList")
    public Result<List<DeviceTypeInfoVO>> getDeviceTypeList(@RequestBody DeviceInfoVO deviceInfoVO) {
        return deviceService.getDeviceTypeList(deviceInfoVO);
    }

    /**
     * 统计无人机设备数量
     * @param uavDeviceDTO
     * @return
     */
    @ApiOperation(value = "统计无人机设备数量")
    @PostMapping("/workbench/getUavDeviceStatistics")
    public Result<IotDeviceStatisticsVo> getUavDeviceStatistics(@RequestBody UavDeviceDTO uavDeviceDTO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        uavDeviceDTO.setUserId(loginUser.getUser().getUserId());
        uavDeviceDTO.setTenantId(loginUser.getUser().getTenantId());
        uavDeviceDTO.setIndustryCode(loginUser.getUser().getIndustryCode());
        return deviceService.getUavDeviceStatistics(uavDeviceDTO);
    }

    /**
     * 查询无人机数量根据设备类型分组
     * */
    @ApiOperation(value = "查询无人机数量根据设备类型分组")
    @PostMapping("/workbench/getUavDeviceStatisticsByType")
    public Result<List<DeviceTypeStatisticsVO>> getUavDeviceStatisticsByType(@RequestBody UavDeviceDTO uavDeviceDTO) {
        return deviceService.getUavDeviceStatisticsByType(uavDeviceDTO);
    }


    /**
     * 统计喇叭设备数量
     * @param hornDeviceDTO
     * @return
     */
    @ApiOperation(value = "统计喇叭设备数量")
    @PostMapping("/workbench/getHornDeviceStatistics")
    public Result<IotDeviceStatisticsVo> getHornDeviceStatistics(@RequestBody HornDeviceDTO hornDeviceDTO) {
        return deviceService.getHornDeviceStatistics(hornDeviceDTO);
    }

    /**
     * 查询喇叭数量根据设备类型分组
     * */
    @ApiOperation(value = "查询喇叭数量根据设备类型分组")
    @PostMapping("/workbench/getHornDeviceStatisticsByType")
    public Result<List<DeviceTypeStatisticsVO>> getHornDeviceStatisticsByType(@RequestBody HornDeviceDTO hornDeviceDTO) {
        return deviceService.getHornDeviceStatisticsByType(hornDeviceDTO);
    }



    /**
     * 统计雷达设备数量
     * @param deviceInfoVO
     * @return
     */
    @ApiOperation(value = "统计雷达设备数量")
    @PostMapping("/workbench/getRadarDeviceStatistics")
    public Result<IotDeviceStatisticsVo> getRadarDeviceStatistics(@RequestBody IotDeviceVO deviceInfoVO) {
        return deviceService.getRadarDeviceStatistics(deviceInfoVO);
    }

    /**
     * 查询雷达设备数量根据设备类型分组
     * */
    @ApiOperation(value = "查询雷达设备数量根据设备类型分组")
    @PostMapping("/workbench/getRadarDeviceStatisticsByType")
    public Result<List<DeviceTypeStatisticsVO>> getRadarDeviceStatisticsByType(@RequestBody IotDeviceVO deviceInfoVO) {
        return deviceService.getRadarDeviceStatisticsByType(deviceInfoVO);
    }


    /**
     * 统计物联设备数量
     * @param deviceInfoVO
     * @return
     */
    @ApiOperation(value = "统计物联设备数量")
    @PostMapping("/workbench/getIotDeviceStatistics")
    public Result<IotDeviceStatisticsVo> getIotDeviceStatistics(@RequestBody IotDeviceVO deviceInfoVO) {
        return deviceService.getIotDeviceStatistics(deviceInfoVO);
    }

    /**
     * 查询物联设备数量根据设备类型分组
     * */
    @ApiOperation(value = "查询物联设备数量根据设备类型分组")
    @PostMapping("/workbench/getIotDeviceStatisticsByType")
    public Result<List<DeviceTypeStatisticsVO>> getIotDeviceStatisticsByType(@RequestBody IotDeviceVO deviceInfoVO) {
        return deviceService.getIotDeviceStatisticsByType(deviceInfoVO);
    }

    /**
     * 统计执法记录仪设备数量
     * 统计执法记录仪设备数量接口
     * @param recorderDeviceVO
     * @return
     */
    @ApiOperation(value = "统计执法记录仪设备数量")
    @PostMapping("/workbench/getRecorderDeviceStatistics")
    public Result<IotDeviceStatisticsVo> getRecorderDeviceStatistics(@RequestBody RecorderDeviceVO recorderDeviceVO) {
        return deviceService.getRecorderDeviceStatistics(recorderDeviceVO);
    }
}
