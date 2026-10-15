package com.chinatower.device.api.deprecated;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.industry.api.factory.IotDeviceFallBackFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "iotDeviceService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = IotDeviceFallBackFactory.class)
public interface IIotDeviceServiceMigrate {



    @ApiOperation(value = "根据区域编码查询物联设备列表")
    @PostMapping("/iotdevice/queryIotDeviceByArea")
    Result<List<IotDeviceVO>> queryIotDeviceByArea(@RequestBody IotDeviceVO iotDeviceVO);


    @ApiOperation(value = "根据站址编码查询物联设备列表")
    @PostMapping("/iotdevice/queryIotDeviceBySite")
    Result<List<IotDeviceVO>> queryIotDeviceBySite(@RequestBody IotDeviceVO iotDeviceVO);


    @ApiOperation(value = "查询设备所属的行政区域树形")
    @PostMapping("/iotdevice/getIotMonitorTree")
    Result<List<IotDeviceVO>> getIotMonitorTree(@RequestBody IotDeviceVO iotDeviceVO);

}
