package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IUavDeviceService;
import com.chinatower.device.api.deprecated.IUavDeviceServiceMigrate;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.dto.UavSecureStrategyInfoVO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 无人机设备信息查询
 */
@Component
public class UavDeviceFallbackFactory implements FallbackFactory<IUavDeviceServiceMigrate> {


    @Override
    public IUavDeviceServiceMigrate create(Throwable throwable) {

        return new IUavDeviceServiceMigrate() {

            /**
             * 查询无人机详情
             * @param deviceCode
             * @return
             */
            @Override
            public Result<UavDeviceQueryVO> qryUavDeviceInfo(String deviceCode) {
                return Result.fail("查询无人机设备详情失败" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getUavMonitorAreaTree(DeviceReqDTO deviceReqDTO) {
                return Result.fail("查询无人机区域设备树失败" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getUavMonitorOrgTree(DeviceReqDTO deviceReqDTO) {
                return Result.fail("查询无人机组织设备树失败" + throwable.getMessage());
            }

            @Override
            public Result<List<SiteResultVO>> queryUavSitesForWE(SiteRequestDTO siteRequestDTO) {
                return Result.fail("查询无人机分布情况失败" + throwable.getMessage());
            }

            @Override
            public Result<List<SiteResultVO>> queryUavSites(SiteRequestDTO siteRequestDTO) {
                return Result.fail("查询无人机站址分布情况失败" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceSiteVO>> queryUavDevicesBySiteCode(SiteRequestDTO siteRequestDTO) {
                return Result.fail("查询列表失败" + throwable.getMessage());
            }

            @Override
            public Result<UavSecureStrategyInfoVO> queryUavSecureStrategyDetail(SiteRequestDTO siteRequestDTO) {
                return Result.fail("查询无人机安全策略失败" + throwable.getMessage());
            }

            @Override
            public Result<UavNestLocationVO> getUavNestLocationInfo(UavDeviceDTO param) {
                return Result.fail("同步同步更新当前无人机巢的信息失败" + throwable.getMessage());
            }

            @Override
            public Result<String> batchModifyUavSecureStrategy(UavSecureStrategyInfoVO param) {
                return Result.fail("批量更新安全策略失败" + throwable.getMessage());
            }

            @Override
            public Result<UavDetailVO> queryUavDetail(UavDeviceDTO param) {
                return Result.fail("查询无人机详细信息失败" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceCameraModelVO>> queryUavCameraModelInfo(UavDeviceDTO param) {
                return Result.fail("查询无人机相机型号信息失败" + throwable.getMessage());
            }
        };
    }
}
