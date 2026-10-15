package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IDeviceCommonService;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.vo.DeviceCommonInfoVO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@Component
public class DeviceCommonFallbackFactory implements FallbackFactory<IDeviceCommonService> {


    @Override
    public IDeviceCommonService create(Throwable throwable) {

        return new IDeviceCommonService() {

            @Override
            public Result<List<DeviceTreeVO>> getDeviceAreaTree(DeviceReqDTO deviceReqDTO) {
                return Result.fail("查询设备区域树失败" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getDeviceOrgTree(DeviceReqDTO deviceReqDTO) {
                return Result.fail("查询设备组织树失败" + throwable.getMessage());
            }

            @Override
            public Result<List<IndustryDeviceTree>> getDeviceSites(SiteRequestDTO siteRequestDTO) {
                return Result.fail("查询设备站址打点接口失败" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceSiteVO>> getDeviceListBySiteCode(SiteRequestDTO siteRequestDTO) {
                return Result.fail("查询站址下设备列表失败" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceDistanceVO>> getDeviceDistanceInfo(DeviceDistanceDTO deviceDistanceDTO) {
                return Result.fail("查询周围设备信息失败" + throwable.getMessage());
            }

            @Override
            public Result<DeviceCommonInfoVO> getDeviceInfo(DeviceCommonInfoVO deviceCommonInfoVO) {
                return Result.fail("查询设备详情信息失败" + throwable.getMessage());
            }

            @Override
            public Result<Integer> getFlyingUavDeviceCount(@RequestBody UavDeviceDTO param) {
                return Result.fail("查询正在飞行中的无人机设备数量失败" + throwable.getMessage());
            }
        };
    }
}
