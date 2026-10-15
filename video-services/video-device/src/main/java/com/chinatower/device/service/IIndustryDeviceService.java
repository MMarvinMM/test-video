package com.chinatower.device.service;

import com.chinatower.common.entity.Result;
import com.chinatower.industry.dto.*;
import com.chinatower.industry.vo.*;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;

import java.util.List;

/**
 * @author yy
 */
public interface IIndustryDeviceService {

    /**
     * 根据关键字查询设备的基本信息接口
     * 入参：搜索的关键信息（keyword（摄像机名称、物联设备名称））、租户id（tenantId）
     *
     * @param deviceInfoVO name
     * @return IndustryDeviceTree
     */
    List<DeviceAndIotDeviceDTO> queryDeviceList(DeviceInfoVO deviceInfoVO);

    /**
     * 根据设备编码和类型，获取设备详细信息
     * 入参：设备编码(deviceCode)、设备类型（deviceType）、租户id（tenantId）
     *
     * @param deviceInfoVO name
     * @return IndustryDeviceTree
     */
    Object queryDeviceDetail(DeviceQueryVO deviceInfoVO);

    /**
     * 根据设备编码，获取附近指定范围的所有设备"
     * 入参：设备编码(deviceCode)、设备类型（deviceType）、范围（distance）
     *
     * @param deviceQueryVO deviceCode  deviceType distance
     * @return DeviceByDistanceDTO
     */
    List<DeviceByDistanceDTO> queryDeviceByDistance(DeviceQueryVO deviceQueryVO);

    List<DeviceInfoByDistanceDTO> queryDeviceInfoByDistance(DeviceInfoQueryVO param);

    List<DeviceByConditionDTO> queryDevicesByCondition(DevicesByConditionVO devicesByConditionVO);

    Result<DevicesStatisticsResVO> queryDevicesStatistics(DevicesStatisticsReqVO devicesStatisticsReqVO);

    /**
     * 雷达联动接力/取消接力接口
     * @param param 请求参数
     */
    RadarTargetFollowResultVO radarTargetFollowRelay(RadarTargetFollowRelayVO param);

    /**
     * 雷达联动同时随动/取消同时随动接口
     * @param param 请求参数
     */
    RadarTargetFollowResultVO radarTargetFollowLookTo(RadarTargetFollowLookToVO param);

    /**
     * 查询目标设备列表
     * @param radarTargetDTO 请求参数
     * @return Result<List<RadarDeviceStatusVO>>  目标设备列表
     */
    Result<List<RadarDeviceStatusVO>> queryRadarTargetList(RadarTargetDTO radarTargetDTO);

    /**
     * 查询客户行业订购的设备数量
     * @return 客户行业订购设备数量
     */
    IndustryDeviceStatisticsVO queryIndustryDeviceStatistics();

    /**
     * 根据条件查询摄像机信息
     * @param devicesByConditionVO
     * @return
     */
    List<DeviceByConditionDTO> queryDeviceListByCondition(DevicesByConditionVO devicesByConditionVO);
}
