package com.chinatower.device.service;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.device.dataobject.DeviceInfoDO;
import com.chinatower.device.dto.AnalysisDTO;
import com.chinatower.device.dto.ComprehensiveDeviceStatisticsVO;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.DeviceComprehensiveVO;
import com.chinatower.industry.dto.DeviceIndDTO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.QueryStorageDayVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;


public interface IVideoDeviceInfoService {

    /**
     * 根据行业版本编码查询设备信息
     * @author chenmf
     * @date 2022/1/27
     * @param pageDeviceVO 平台编码
     * @return 设备数据和分页结果
     */
    List<DeviceByPlatformVO> pageDeviceByPlatform(PageDeviceVO pageDeviceVO);

    /**
     * 视频树查询
     *
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getMonitorTree(DeviceReqDTO deviceReqDTO);

    /**
     * 视频组织树查询
     *
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getMonitorOrgTree(DeviceReqDTO deviceReqDTO);


    /**
     * 设备/通道/预置点/监控区域/算法关系推送
     *
     * @return
     * @author chenmf
     * @date 2022/2/9
     * @version
     */
    Result<JSONObject> synchDeviceAlgorithms(List<AlgorithmParamBodyVO> param);

    /**
     * 批量保存和更新设备信息
     *
     * @return
     * @author chenmf
     * @date 2022/2/11
     * @version
     */
    void batchSaveOrUpdateDevice(List<SaveOrUpdateDeviceDTO> deviceDTOList);

    /**
     * 功能描述: 查询租户管理区域
     *
     * @param: deviceReqDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/17
     * @version: 1.0
     */
    List<DeviceTreeVO> getTenantMonitorLevel(DeviceReqDTO deviceReqDTO);

    /**
     * 获取视频存储时长
     * @return
     * @author chenmf
     * @date 2022/3/21
     * @version
     */
    String queryRecordPlanStorageDay(QueryStorageDayVO queryStorageDayVO);

    /**
     * 获取设备标签树
     */
    List<DeviceTreeVO> getMonitorLabelTree(DeviceReqDTO deviceReqDTO);
    List<DeviceTreeVO> getMonitorLabelTreeNew(DeviceReqDTO deviceReqDTO);

    List<DeviceInfoVO> linkageDevice(DeviceReqDTO deviceReqDTO);

    DeviceDetailVO queryVideoDetail(DeviceTreeVO deviceTreeVO, LoginUser loginUser);

    /**
     * 获取大屏设备统计结果
     * @param analysisDTO 参数
     * @return 设备统计结果
     */
    Map<String, Object> queryDeviceStatistics(AnalysisDTO analysisDTO);

    DeviceInfoDO queryDeviceInfo(DeviceReqDTO deviceReqDTO);

    /**
     * 设备综合查询
     * @param deviceComprehensiveReq
     * @return
     */
    Result<PageInfo<DeviceComprehensiveVO>> queryDeviceComprehensive(DeviceComprehensiveReq deviceComprehensiveReq);

    /**
     *
     * 查询范围内距离最近的设备，以树的形式返回
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> linkageDeviceTree(DeviceReqDTO deviceReqDTO);

    List<DeviceIndDTO> getDeviceListByCodes(List<String> deviceCodes);

    List<DeviceTreeVO> getMonitorList(DeviceReqDTO deviceReqDTO);

    Result<List<TreeSelect>> getDeviceTreeForestry(WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO);

    List<WoodProcessingPlantDeivceVO> getWoodProcessingPlantDeivceInfo(WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO);

    List<WoodProcessingPlantDeivceVO> getAlarmNumByDevice(WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO);

    PageInfo<WoodProcessingPlantDeivceVO> getWoodProcessingPlantDeivceInfoPage(WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO);

    List<String> queryDeviceCodeList(DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO>  getMonitorTreeStepOne(DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> queryDevicesByAreaParam(DeviceTreeVO deviceTreeVO);

    List<DeviceTreeVO> getLabelTree(DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> queryDevicesByLabelParam(DeviceTreeVO deviceTreeVO);

    List<IndustryDeviceTree> queryDevicesForGlobal(DevicesForGlobalReq devicesForGlobalReq);

    List<DevicesForGlobalPoiRes> queryDevicesForGlobalPoi(DevicesForGlobalPoiReq devicesForGlobalPoiReq);

    List<IndustryDeviceTree> queryDevicesForGlobalSurround(DevicesForGlobalSurroundReq devicesForGlobalSurroundReq);

    List<IndustryDeviceTree> queryDevicesForGlobalSurroundList(DevicesForGlobalSurroundReq devicesForGlobalSurroundReq);

    List<DevicesForGlobalPoiRes> queryHisForGlobalPoi();

    String addHisForGlobalPoi(DevicesForGlobalPoiRes devicesForGlobalPoiRes);

    String delHisForGlobalPoi(DevicesForGlobalPoiRes devicesForGlobalPoiRes);

    List<IndustryDeviceTree> queryDevicesForGlobalUav(DevicesForGlobalSurroundReq devicesForGlobalSurroundReq);

    /**
     * 综合设备统计
     * @return
     */
    ComprehensiveDeviceStatisticsVO queryComprehensiveDeviceStatistics();

    List<DeviceTreeVO> getMonitorDeptTreeStepOne(DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getMonitorDeptTreeStepTwo(DeviceTreeVO deviceTreeVO);

    List<DeviceTreeVO> getMonitorTreeStepTwoPage(DeviceTreeVO deviceTreeVO);
}
