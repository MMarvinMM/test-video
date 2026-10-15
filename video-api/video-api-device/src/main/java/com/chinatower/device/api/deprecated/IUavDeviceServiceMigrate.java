package com.chinatower.device.api.deprecated;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.factory.UavDeviceFallbackFactory;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.dto.UavSecureStrategyInfoVO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "uavDeviceService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = UavDeviceFallbackFactory.class)
public interface IUavDeviceServiceMigrate {


    @Deprecated
    @PostMapping("/uav/device/qryUavDeviceInfo/{deviceCode}")
    Result<UavDeviceQueryVO> qryUavDeviceInfo(@PathVariable("deviceCode") String deviceCode);

    @Deprecated
    @PostMapping("/uav/device/getUavMonitorAreaTree")
    Result<List<DeviceTreeVO>> getUavMonitorAreaTree(@RequestBody DeviceReqDTO deviceReqDTO);

    @PostMapping("/uav/device/getUavMonitorOrgTree")
    @Deprecated
    Result<List<DeviceTreeVO>> getUavMonitorOrgTree(@RequestBody DeviceReqDTO deviceReqDTO);

    @Deprecated
    @PostMapping("/uav/device/queryUavSitesForWE")
    Result<List<SiteResultVO>> queryUavSitesForWE(@RequestBody SiteRequestDTO siteRequestDTO);

    @Deprecated
    @PostMapping("/uav/device/queryUavSites")
    Result<List<SiteResultVO>> queryUavSites(@RequestBody SiteRequestDTO siteRequestDTO);
    @Deprecated
    @PostMapping("/uav/device/queryUavDevicesBySiteCode")
    Result<List<DeviceSiteVO>> queryUavDevicesBySiteCode(@RequestBody SiteRequestDTO siteRequestDTO);

    @PostMapping("/uav/strategy/queryUavSecureStrategyDetail")
    Result<UavSecureStrategyInfoVO> queryUavSecureStrategyDetail(@RequestBody SiteRequestDTO siteRequestDTO);

    @PostMapping("/uav/device/syncUavNestLocationInfo")
    Result<UavNestLocationVO> getUavNestLocationInfo(@RequestBody UavDeviceDTO param);

    /**
     * 批量更新安全策略
     * @param param
     * @return
     */
    @PostMapping("/uav/strategy/batchModifyUavSecureStrategy")
    Result<String> batchModifyUavSecureStrategy(@RequestBody UavSecureStrategyInfoVO param);

    /**
     * 查询无人机详细信息
     * @param param
     * @return
     */
    @PostMapping("/uav/device/queryUavDetail")
    Result<UavDetailVO> queryUavDetail(@RequestBody UavDeviceDTO param);

    /**
     * 查询无人机相机型号信息
     * @param param
     * @return
     */
    @PostMapping("/uav/device/queryUavCameraModelInfo")
    Result<List<DeviceCameraModelVO>> queryUavCameraModelInfo(@RequestBody UavDeviceDTO param);
}
