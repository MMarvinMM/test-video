package com.chinatower.device.api.deprecated;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.factory.RecorderDeviceFallbackFactory;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.platform.entity.device.*;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(contextId = "reorderDeviceService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = RecorderDeviceFallbackFactory.class)
public interface IReorderDeviceServiceMigrate {


    @PostMapping("/recorder/device/getList")
    Result<List<RecorderDeviceDTO>> getList(@RequestBody RecorderDeviceVO recorderDeviceVO);


    @PostMapping("/recorder/device/detail")
    Result<RecorderDeviceDTO> detail(@RequestBody RecorderDeviceVO recorderDeviceVO);


    @PostMapping("/recorder/device/getRecorderMonitorOrgTree")
    Result<List<DeviceTreeVO>> getRecorderMonitorOrgTree(@RequestBody DeviceReqDTO deviceReqDTO);


    @PostMapping("/recorder/device/getRecorderDevicesBySiteCode")
    Result<List<DeviceSiteVO>> getRecorderDevicesBySiteCode(@RequestBody SiteRequestDTO siteRequestDTO);


    @PostMapping("/recorder/device/getRecorderDeviceDistanceInfo")
    Result<List<DeviceDistanceVO>> getRecorderDeviceDistanceInfo(@RequestBody DeviceDistanceDTO param);


    @PostMapping("/recorder/device/getRecorderMonitorAreaTree")
    Result<List<DeviceTreeVO>> getRecorderMonitorAreaTree(@RequestBody DeviceReqDTO deviceReqDTO);


    @PostMapping("/recorder/device/getRecorderSites")
    Result<List<DeviceTreeVO>> getRecorderSites(@RequestBody SiteRequestDTO siteRequestDTO);

    @PostMapping("/recorder/device/getRecorderDeviceListPage")
    Result<PageInfo<RecorderDeviceDTO>> getRecorderDeviceListPage(@RequestBody RecorderDeviceVO recorderDeviceVO);
}
