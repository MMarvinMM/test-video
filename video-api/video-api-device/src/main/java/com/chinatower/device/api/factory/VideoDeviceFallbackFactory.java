package com.chinatower.device.api.factory;


import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.deprecated.IVideoDeviceServiceMigrate;
import com.chinatower.device.entity.dto.DeviceComprehensiveReq;
import com.chinatower.device.entity.dto.IotDeviceMsgDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.industry.dto.DeviceIndDTO;
import com.chinatower.platform.entity.device.*;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import feign.hystrix.FallbackFactory;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 设备服务降级处理
 *
 * @author chenmf
 */
@Component
public class VideoDeviceFallbackFactory implements FallbackFactory<IVideoDeviceServiceMigrate> {

    private static final Logger log = LoggerFactory.getLogger(VideoDeviceFallbackFactory.class);

    @Override
    public IVideoDeviceServiceMigrate create(Throwable throwable) {

        return new IVideoDeviceServiceMigrate() {
            @Override
            public Result getMonitorTree(DeviceReqDTO deviceReqDTO) {
                return Result.fail("视频树查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getMonitorList(DeviceReqDTO deviceReqDTO) {
                return Result.fail("摄像机查询列表失败:" + throwable.getMessage());
            }

            @Override
            public Result getMonitorOrgTree(DeviceReqDTO deviceReqDTO) {
                return Result.fail("视频组织树查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<JSONObject> synchDeviceAlgorithms(List<AlgorithmParamBodyVO> param) {
                return Result.fail("设备/通道/预置点/监控区域/算法关系推送失败:" + throwable.getMessage());
            }

            @Override
            public Result batchSaveOrUpdateDevice(List<SaveOrUpdateDeviceDTO> deviceDTOList) {
                return Result.fail("批量保存和更新设备信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> batchSaveOrUpdateIotDevice(@RequestBody List<IotDeviceMsgDTO> iotDevices) {
                return Result.fail("批量保存和更新物联设备信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<TenantIotDeviceConfVO> getTenantIotDevConf(@RequestBody @Valid TenantIotDeviceConfVO qryCond) {
                return Result.fail("查询物联设备联动配置失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<TenantIotDeviceConfVO>> getTenantIotDevConfBatch(TenantIotDeviceConfVO qryCond) {
                return Result.fail("批量查询物联设备联动配置失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<TenantIotDeviceConfVO>> getTenantIotDevConfBatchV2(TenantIotDeviceConfVO qryCond) {
                return Result.fail("批量查询物联设备联动配置V2失败:" + throwable.getMessage());
            }

            @Override
            public Result<PageInfo<WoodProcessingPlantDeivceVO>> getWoodProcessingPlantDeivceInfoPage(WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO) {
                return Result.fail("查询木材加工厂列表失败:" + throwable.getMessage());
            }

            @Override
            public Result<PageInfo<DeviceComprehensiveVO>> queryDeviceComprehensive(DeviceComprehensiveReq deviceComprehensiveReq) {
                return Result.fail("设备综合查询失败:" + throwable.getMessage());
            }

            @Override
            public Result getTenantMonitorLevel(DeviceReqDTO deviceReqDTO) {
                return Result.fail("查询租户管理区域失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo pageDeviceByPlatform(PageDeviceVO pageDeviceVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<List<DeviceTreeVO>> getMonitorLabelTree(DeviceReqDTO deviceReqDTO) {
                return Result.fail("查询设备标签树失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getMonitorLabelTreeNew(DeviceReqDTO deviceReqDTO) {
                return Result.fail("查询设备标签树失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> updateDeviceUserNum(DeviceVideoDTO deviceVideoDTO) {
                return Result.fail("更新设备用户数缓存失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceDistanceVO>> queryUavDeviceDistanceInfo(DeviceDistanceDTO param) {
                return Result.fail("查询无人机设备距离信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceDistanceVO>> queryHornDeviceDistanceInfo(DeviceDistanceDTO param) {
                return Result.fail("查询喇叭设备距离信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<SensorAttrMapVO>> getSensorPeriodData(DeviceReqDTO deviceReqDTO) {
                return Result.fail("查询传感器时间段数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceDistanceVO>> queryRadarDevDistanceInfo(DeviceDistanceDTO param) {
                return Result.fail("查询雷达距离信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceDistanceVO>> queryIotDevDistanceInfo(DeviceDistanceDTO param) {
                return Result.fail("查询物联设备距离信息失败:" + throwable.getMessage());
            }

            @Override
            public  Result<List<DeviceDiyTreeVO>> getList(DeviceDiyTreeVO deviceInfoVO) {
                return Result.fail("查询自定义树下拉框失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceDiyTreeResultVO>> getDiyTree(DeviceDiyTreeResultVO deviceInfoVO) {
                return Result.fail("查询自定义树失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceIndDTO>> getDeviceListByCodes(List<String> deviceCodes) {
                return Result.fail("根据设备code列表查询设备基本信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<TreeSelect>> getDeviceTreeForestry( WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO) {
                return Result.fail("根据用户权限查询摄像机树，该接口为林业提供：" + throwable.getMessage());
            }

            @Override
            public Result<List<WoodProcessingPlantDeivceVO>> getWoodProcessingPlantDeivceInfo( WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO) {
                return Result.fail("根据channelCode获取摄像机列表查询失败，该接口为林业提供：" + throwable.getMessage());
            }

            @Override
            public Result<List<WoodProcessingPlantDeivceVO>> getAlarmNumByDevice(WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO) {
                return Result.fail("根据deivceCode查询设备告警数量查询失败：" + throwable.getMessage());
            }
        };
    }
}
