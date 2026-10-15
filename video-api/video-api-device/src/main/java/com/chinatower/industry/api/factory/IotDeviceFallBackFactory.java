package com.chinatower.industry.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.deprecated.IIotDeviceServiceMigrate;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.industry.api.IIotDeviceService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class IotDeviceFallBackFactory implements FallbackFactory<IIotDeviceServiceMigrate> {


    @Override
    public IIotDeviceServiceMigrate create(Throwable throwable) {
        return new IIotDeviceServiceMigrate() {
            @Override
            public Result<List<IotDeviceVO>> queryIotDeviceByArea(IotDeviceVO iotDeviceVO) {
                return Result.fail("根据区域编码查询物联设备列表:" + throwable.getMessage());
            }

            @Override
            public Result<List<IotDeviceVO>> queryIotDeviceBySite(IotDeviceVO iotDeviceVO) {
                return Result.fail("根据站址编码查询物联设备列表:" + throwable.getMessage());
            }

            @Override
            public Result<List<IotDeviceVO>> getIotMonitorTree(IotDeviceVO iotDeviceVO) {
                return Result.fail("查询设备所属的行政区域树形:" + throwable.getMessage());
            }
        };
    }
}
