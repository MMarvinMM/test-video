package com.chinatower.device.api.deprecated;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.factory.VideoDeviceMgmtServiceFactory;
import com.chinatower.platform.entity.device.DeviceLabelReq;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.devicemgmt.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(contextId = "videoDeviceMgmtService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = VideoDeviceMgmtServiceFactory.class)
public interface IVideoDeviceMgmtServiceMigrate {

    @PostMapping("/video/device/queryDeviceInfoPage")
    TableDataInfo queryDeviceInfoPage(@RequestBody DeviceInfoVO deviceInfoVO);

    @PostMapping("/video/device/getChannelInfoForCustomer")
    Result<List<ChannelInfoVO>> listChannelInfoForCustDev(@RequestBody DeviceInfoVO deviceInfoVO);

    @PostMapping("/video/device/queryAreaTree")
    Result<List<DeviceAreaTreeVO>> queryAreaTree(@RequestBody DeviceReqDTO deviceReqDTO);

    @PostMapping("/video/device/getDeviceInfo")
    @Deprecated
    Result<DeviceInfoVO> getDeviceInfo(@RequestBody DeviceInfoVO deviceInfoVO);

    @PostMapping("/video/device/updateDeviceInfo")
    Result<String> updateDeviceInfo(@RequestBody DeviceInfoVO deviceInfoVO);

    @PostMapping("/video/device/getDeviceCustList")
    Result<List<DeviceTenantInfoVO>> getDeviceCustList(@RequestBody DeviceInfoVO deviceInfoVO);

    @PostMapping("/video/device/checkDeviceName")
    Result<Boolean> checkDeviceName(@RequestBody DeviceInfoVO deviceInfoVO);

    @PostMapping("/video/device/queryDeviceLabels")
    Result<List<TenantDeviceLabelVO>> queryDeviceLabels(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO);

    @PostMapping("/video/device/queryTenantLabelList")
    Result<List<TenantDeviceLabelVO>> queryTenantLabelList(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO);

    @PostMapping("/video/device/addDeviceLabel")
    Result<String> addDeviceLabel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO);

    @PostMapping("/video/device/addDeviceLabelRel")
    Result<String> addDeviceLabelRel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO);

    @PostMapping("/video/device/updateDeviceLabel")
    Result<String> updateDeviceLabel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO);

    @PostMapping("/video/device/relieveDeviceLabelRel")
    Result<String> relieveDeviceLabelRel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO);

    @PostMapping("/video/device/deleteDeviceLabel")
    Result<String> deleteDeviceLabel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO);

    @PostMapping("/video/device/queryDeviceLabelInfoByDeviceCode")
    Result<List<TenantDeviceLabelSelectedVO>> queryDeviceLabelInfoByDeviceCode(@RequestBody TenantDeviceLabelRelVO param);

    @PostMapping("/video/device/queryDeviceLabelInfoByCondition")
    Result<List<DeviceLabelInfoVO>> queryDeviceLabelInfoByCondition(@RequestBody DeviceLabelReq param);

    @PostMapping("/video/device/getDeviceImage")
    Result<List<String>> getDeviceImage(@RequestBody DeviceInfoVO deviceInfoVO);

    @PostMapping("/video/device/queryOrderDeviceByDevices")
    Result<List<DeviceInfoVO>> queryOrderDeviceByDevices(@RequestBody DeviceInfoVO deviceInfoVO);
}
