package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.deprecated.IReorderDeviceServiceMigrate;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.platform.entity.device.*;
import com.github.pagehelper.PageInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class RecorderDeviceFallbackFactory implements FallbackFactory<IReorderDeviceServiceMigrate> {

    @Override
    public IReorderDeviceServiceMigrate create(Throwable throwable) {
        return new IReorderDeviceServiceMigrate() {


            @Override
            public Result<List<RecorderDeviceDTO>> getList(RecorderDeviceVO recorderDeviceVO) {
                return Result.fail("查询执法记录仪列表信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<RecorderDeviceDTO> detail(RecorderDeviceVO recorderDeviceVO) {
                return Result.fail("查询执法记录仪详情信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getRecorderMonitorOrgTree(DeviceReqDTO deviceReqDTO) {
                return Result.fail("查询执法记录仪组织树失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceSiteVO>> getRecorderDevicesBySiteCode(SiteRequestDTO siteRequestDTO) {
                return Result.fail("查询执法记录仪站址设备信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceDistanceVO>> getRecorderDeviceDistanceInfo(DeviceDistanceDTO param) {
                return Result.fail("查询执法记录仪距离信息信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getRecorderMonitorAreaTree(DeviceReqDTO deviceReqDTO) {
                return Result.fail("查询执法记录仪区域树失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getRecorderSites(SiteRequestDTO siteRequestDTO) {
                return Result.fail("查询执法记录仪站址列表失败:" + throwable.getMessage());
            }

            @Override
            public Result<PageInfo<RecorderDeviceDTO>> getRecorderDeviceListPage(RecorderDeviceVO recorderDeviceVO) {
                return Result.fail("分页查询执法记录仪列表失败:" + throwable.getMessage());
            }
        };
    }
}
