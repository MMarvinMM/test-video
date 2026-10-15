package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.factory.TowerDeviceFallbackFactory;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.*;
import com.chinatower.platform.entity.admin.SysAreaStreet;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.entity.device.RecorderDeviceVO;
import com.chinatower.platform.entity.device.UavNestDeviceInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceLabelInfoVO;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(contextId = "towerDeviceService", value = ServiceNameConstants.DEVICE_TT_SERVICE, fallbackFactory = TowerDeviceFallbackFactory.class)
public interface ITowerDeviceService {

    @PostMapping("/hornTTDevice/queryHornDevicePage")
    Result<PageInfo<HornDeviceVo>> queryHornDevicePage(@RequestBody HornDeviceDTO hornDeviceDTO);

    @PostMapping("/uav/device/qryUavDeviceListPage")
    TableDataInfo qryUavDeviceListPage(@RequestBody UavTTDeviceDTO uavDeviceDTO);

    @PostMapping("/iotDeviceTT/ttview/device/queryIotDeviceListPage")
    TableDataInfo queryIotDeviceListPage(@RequestBody IotTTDeviceVO deviceInfoVO);

    @PostMapping("/radarTT/queryRadarListPage")
    TableDataInfo queryRadarListPage(@RequestBody IotTTDeviceVO deviceInfoVO);

    @PostMapping("/recorder/device/getRecorderExportList")
    Result<List<RecorderTTExportVO>> getRecorderExportList(@RequestBody RecorderDeviceVO recorderDeviceVO);

    @PostMapping("/recorder/device/getRecorderLocalExportList")
    Result<List<RecorderLocalExportVO>> getRecorderLocalExportList(@RequestBody RecorderDeviceVO recorderDeviceVO);

//    @PostMapping("/ttview/device/getCheckpointAlarmList")
//    TableDataInfo getCheckpointAlarmList(@RequestBody CheckpointAlarmVo vo);

    /**
     * 铁塔视角无人机机场导出
     * @apiNote 铁塔视角无人机机场导出
     * @apiVersion 3.27
     * @tag 三、设备能力
     * @requestDemo {
     * "deviceCode": "a12",
     * "deviceName": "及擦",
     * "nestSn": "12123",
     * "deviceModel": "model",
     * "resourceStatus": "0",
     * "manufacturerList"： ["1", "2"],
     * "accessCodeOrName": "123",
     * "createBy": "12",
     * "updateBy": "232",
     * "updateTimeBegin": "2024-12-24 00:00:00",
     * "updateTimeEnd": "2024-12-25 23:59:59",
     * "createTimeBegin": "2024-12-24 00:00:00",
     * "createTimeEnd": "2024-12-25 23:59:59"
     * "provinceCode": "110000",
     * "cityCode": "110100",
     * "countyCode": "110101",
     * "townCode": "1101001000"
     * }
     * @responseDemo
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1715308032568,
     *     "data": [
     *         {
     *             "locationCode": "10000000001",
     *             "locationName": "中心节点",
     *             "deviceCode": "11010500991327000888",
     *             "deviceName": "三江新区龙头山下",
     *             "deviceStatusName": "在线",
     *             "manufacturerName": null,
     *             "modelCode": null,
     *             "location": "北京市朝阳区七棵树创意园北侧河边",
     *             "custCount": "2",
     *             "provinceDeptName": null,
     *             "cityDeptName": null
     *         },
     *         {
     *             "locationCode": "10000000001",
     *             "locationName": "中心节点",
     *             "deviceCode": "14058100671327000001",
     *             "deviceName": "文物监控-山西晋城市高平清梦观安徽的",
     *             "deviceStatusName": "在线",
     *             "manufacturerName": null,
     *             "modelCode": "hikvision",
     *             "location": "四川省广安市岳池县鱼峰乡大石门",
     *             "custCount": "10",
     *             "provinceDeptName": null,
     *             "cityDeptName": null
     *         }
     *     ]
     * }
     * @param  vo
     * @return Result<List<RecorderTTExportVO>>
     */
    @PostMapping("/nest/uav/pageList")
    Result<PageInfo<UavNestDeviceInfoVO>> pageList(@RequestBody UavNestDeviceInfoVO vo);

    @PostMapping("/ttview/device/getCheckpointAlarmList")
    TableDataInfo getCheckpointAlarmList(@RequestBody CheckpointAlarmReq vo);

    @PostMapping("/ttview/device/queryDeviceLabelBatch")
    Result<List<DeviceLabelInfoVO>> queryDeviceLabelBatch(@RequestBody List<String> deviceCodeList);

    /**
     * 查询街道列表
     * @param areaParentCode
     * @return
     */
    @GetMapping("/video/device/listTownStreet")
    Result<List<SysAreaStreet>> listTownStreet(@RequestParam String areaParentCode);

    /**
     * 查询无人机设备所属的行政区域树形
     * @param uavDeviceDTO
     * @return
     */
    @PostMapping("/uav/device/getUavMonitorTree")
    Result<List<DeviceTreeVO>> getIotMonitorTree(@RequestBody UavDeviceDTO uavDeviceDTO);

    /**
     * 根据设备编码查询无人机设备信息
     * @param deviceCode
     * @return
     */
    @PostMapping("/uav/device/qryUavDeviceInfo/{deviceCode}")
    Result<UavDeviceVO> qryUavDeviceInfo(@PathVariable("deviceCode") String deviceCode);

    /**
     * 获取无人机型号列表
     *
     * @param uavModelVO 包含查询条件的无人机型号对象
     * @return 返回表格数据信息对象
     */
    @PostMapping("/uav/model/list")
    TableDataInfo getList(@RequestBody UavModelVO uavModelVO);

    /**
     * 获取机场型号列表
     *
     * @param uavNestModelVO 包含查询条件的机场型号对象
     * @return 返回表格数据信息对象
     */
    @PostMapping("/uav/nest/model/list")
    TableDataInfo getList(@RequestBody UavNestModelVO uavNestModelVO);

    /**
     * 机场区域树
     * @param uavNestDeviceVO
     * @return
     */
    @PostMapping("/nest/uav/getAreaTree")
    Result<List<DeviceTreeVO>> getAreaTree(@RequestBody UavNestDeviceInfoVO uavNestDeviceVO);

    /**
     * 查询机场详情信息
     * @param uavNestDeviceVO
     * @return
     */
    @PostMapping("/nest/uav/detail")
    Result<UavNestDeviceInfoVO> detail(@RequestBody UavNestDeviceInfoVO uavNestDeviceVO);

    /**
     * 修改机场信息
     * @param uavNestDeviceVO
     * @return
     */
    @PostMapping("/nest/uav/modify")
    Result<String> modify(@RequestBody @Validated UavNestDeviceInfoVO uavNestDeviceVO);

    /**
     * 获取相机型号列表
     */
    @PostMapping("/camera/model/queryCameraModelList")
    TableDataInfo queryCameraModelList(@RequestBody UavCameraModelQryReq qryReq);

    /**
     * 获取相机型号详情
     */
    @GetMapping("/camera/model/detail")
    TableDataInfo getCameraModelDetail(Long id);

    /**
     * 无人机型号详情信息
     *
     * @param uavModelVO 包含需要添加的无人机型号信息的对象
     * @return 无人机型号详情信息
     */
    @PostMapping("/uav/model/getUavModelInfo")
    Result<UavModelDO> getUavModelInfo(@RequestBody UavModelVO uavModelVO);

    /**
     * 查询挂载型号管理列表
     * @param param
     * @return
     */
    @PostMapping("/uav/mount/model/list")
    TableDataInfo list(@RequestBody UavMountModelQueryDTO param);

    @PostMapping("/device/comprehensiveQueryAI")
    Result<ComprehensiveQueryAIResult> comprehensiveQueryAI(@RequestBody DeviceAIVO deviceAIVO);

    @PostMapping("/ttview/device/getDeviceListForYJB")
    Result<List<DeviceYjbRsp>> getDeviceListForYJB(@RequestBody DeviceYjbReq deviceYjbReq);

    /**
     * 创建应急部摄像机抓图任务。
     *
     * @param request 设备通道组合列表
     * @return 任务编码及下发失败明细
     */
    @PostMapping("/ttview/device/createSnapshotTaskForYJB")
    Result<YjbSnapshotTaskRsp> createSnapshotTaskForYJB(@RequestBody YjbSnapshotTaskReq request);

    /**
     * 查询应急部摄像机抓图任务进度和已完成图片。
     *
     * @param request 服务端返回的任务编码
     * @return 任务进度及已完成抓图明细
     */
    @PostMapping("/ttview/device/querySnapshotTaskForYJB")
    Result<YjbSnapshotTaskRsp> querySnapshotTaskForYJB(@RequestBody YjbSnapshotTaskReq request);
}
