package com.chinatower.device.api.factory;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.ITowerDeviceService;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.*;
import com.chinatower.platform.entity.admin.SysAreaStreet;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.entity.device.RecorderDeviceVO;
import com.chinatower.platform.entity.device.UavNestDeviceInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceLabelInfoVO;
import com.github.pagehelper.PageInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
public class TowerDeviceFallbackFactory implements FallbackFactory<ITowerDeviceService> {

    @Override
    public ITowerDeviceService create(Throwable throwable) {
        return new ITowerDeviceService() {

            @Override
            public Result<PageInfo<HornDeviceVo>> queryHornDevicePage(HornDeviceDTO hornDeviceDTO) {
                return Result.fail("查询喇叭列表失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo qryUavDeviceListPage(UavTTDeviceDTO uavDeviceDTO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo queryIotDeviceListPage(IotTTDeviceVO deviceInfoVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo queryRadarListPage(IotTTDeviceVO deviceInfoVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<List<RecorderTTExportVO>> getRecorderExportList(RecorderDeviceVO recorderDeviceVO) {
                return Result.fail("查询执法记录仪列表失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<RecorderLocalExportVO>> getRecorderLocalExportList(RecorderDeviceVO recorderDeviceVO) {
                return Result.fail("查询执法记录仪列表失败:" + throwable.getMessage());
            }

//            @Override
//            public TableDataInfo getCheckpointAlarmList(CheckpointAlarmVo vo) {
//                return null;
//            }

            @Override
            public Result<PageInfo<UavNestDeviceInfoVO>> pageList(UavNestDeviceInfoVO vo) {
                return Result.fail("查询无人机机场列表失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getCheckpointAlarmList(CheckpointAlarmReq vo) {
                return null;
            }

            @Override
            public Result<List<DeviceLabelInfoVO>> queryDeviceLabelBatch(@RequestBody List<String> deviceCodeList){
                return Result.fail("根据设备编码列表批量查询设备标签信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<SysAreaStreet>> listTownStreet(String areaParentCode) {
                return Result.fail("查询街道列表失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getIotMonitorTree(UavDeviceDTO uavDeviceDTO) {
                return Result.fail("查询无人机设备所属的行政区域树失败:" + throwable.getMessage());
            }

            @Override
            public Result<UavDeviceVO> qryUavDeviceInfo(String deviceCode) {
                return Result.fail("根据设备编码查询无人机设备信息失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo getList(UavModelVO uavModelVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getList(UavNestModelVO uavNestModelVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<List<DeviceTreeVO>> getAreaTree(UavNestDeviceInfoVO uavNestDeviceVO) {
                return Result.fail("获取机场区域树失败:" + throwable.getMessage());
            }

            @Override
            public Result<UavNestDeviceInfoVO> detail(UavNestDeviceInfoVO uavNestDeviceVO) {
                return Result.fail("查询机场详情信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> modify(UavNestDeviceInfoVO uavNestDeviceVO) {
                return Result.fail("修改机场信息失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryCameraModelList(UavCameraModelQryReq qryReq) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo getCameraModelDetail(Long id) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<UavModelDO> getUavModelInfo(UavModelVO uavModelVO) {
                return Result.fail("获取无人机型号详情信息失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo list(UavMountModelQueryDTO param) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<ComprehensiveQueryAIResult> comprehensiveQueryAI(DeviceAIVO deviceAIVO){
                return Result.fail("综合查询摄像机列表失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceYjbRsp>> getDeviceListForYJB(DeviceYjbReq deviceYjbReq){
                return Result.fail("应急部摄像机列表失败:" + throwable.getMessage());
            }

            @Override
            public Result<YjbSnapshotTaskRsp> createSnapshotTaskForYJB(YjbSnapshotTaskReq request) {
                return Result.fail("创建应急部抓图任务失败:" + throwable.getMessage());
            }

            @Override
            public Result<YjbSnapshotTaskRsp> querySnapshotTaskForYJB(YjbSnapshotTaskReq request) {
                return Result.fail("查询应急部抓图任务失败:" + throwable.getMessage());
            }
        };
    }
}
