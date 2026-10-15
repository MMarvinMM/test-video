package com.chinatower.device.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.device.dataobject.IotDeviceDO;
import com.chinatower.device.dataobject.SensorAttrResultVO;
import com.chinatower.device.dataobject.SensorPeriodPageResultVO;
import com.chinatower.device.entity.dto.DeviceComprehensiveReq;
import com.chinatower.device.entity.vo.DeviceComprehensiveVO;
import com.chinatower.device.dto.RadarDeviceRelDTO;
import com.chinatower.device.dto.SensorEventInfoVO;
import com.chinatower.device.entity.dto.IotDeviceMsgDTO;
import com.chinatower.device.entity.dto.SenSorMonitorConfigDO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.industry.dto.DeviceLayerReq;
import com.chinatower.industry.dto.RadarDeviceReqDTO;
import com.chinatower.industry.dto.SensorDeviceCountReqDTO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.device.*;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 物联设备表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
public interface IIotDeviceRepository extends IService<IotDeviceDO> {


    /**
     *
     * 查询行政组织物联设备树
     *
     * @param: deviceInfoDTO
     * @return: List<DeviceTreeVO>
     */
    List<DeviceTreeVO> getIotMonitorOrgTree(IotDeviceVO deviceInfoVO);


    /**
     * 查询物联设备详情--不关联订购
     * @param iotDeviceVO
     * @return
     */
    List<IotDeviceVO> queryIotDeviceInfo(IotDeviceVO iotDeviceVO);

    /**
     * 查询雷达设备详情--不关联订购
     * @param iotDeviceVO
     * @return
     */
    List<IotDeviceVO> queryRadarDeviceInfo(IotDeviceVO iotDeviceVO);

    /**
     *
     * 根据站址编码查询物联设备列表
     * @param iotDeviceVO
     * @return
     */
    List<IotDeviceVO> queryIotDeviceBySite(IotDeviceVO iotDeviceVO);

    /**
     *
     * 根据区域编码查询物联设备列表
     * @param iotDeviceVO
     * @return
     */
    @Deprecated
    List<IotDeviceVO> queryIotDeviceByArea(IotDeviceVO iotDeviceVO);

    /**
     * 物联设备图层打点接口
     * @param siteRequestDTO
     * @return
     */
    List<IndustryDeviceTree> queryIotDeviceSites(SiteRequestDTO siteRequestDTO);

    /**
     *
     * 查询行政区域物联设备树
     *
     * @param: deviceInfoDTO
     * @return: List<DeviceTreeVO>
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    List<DeviceTreeVO> getIotMonitorTree(@RequestBody IotDeviceVO deviceInfoVO);

    /**
     * 查询行政区域雷达设备树
     *
     * @param: deviceInfoDTO
     * @return: List<DeviceTreeVO>
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    List<DeviceTreeVO> getRadarMonitorTree(IotDeviceVO deviceInfoVO);


    /**
     *
     * 查询行政组织雷达设备树
     *
     * @param: deviceInfoDTO
     * @return: List<DeviceTreeVO>
     */
    List<DeviceTreeVO> getRadarMonitorOrgTree(IotDeviceVO deviceInfoVO);

    /**
     *
     * 根据站址编码查询雷达列表
     * @param iotDeviceVO
     * @return
     */
    List<IotDeviceVO> queryRadarDeviceBySite(IotDeviceVO iotDeviceVO);

    /**
     *
     * 根据区域编码查询雷达列表
     * @param iotDeviceVO
     * @return
     */
    List<IotDeviceVO> queryRadarDeviceByArea(IotDeviceVO iotDeviceVO);

    /**
     * 条件查询 物联设备/雷达 信息
     *
     * @param: deviceInfoVO
     * @return: List<IotDeviceVO>
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    List<IotDeviceVO> queryIotDeviceListNew(IotDeviceVO deviceInfoVO);

    /**
     * 条件查询物联设备信息-废弃
     *
     * @param: deviceInfoVO
     * @return: List<IotDeviceVO>
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    List<IotDeviceVO> queryIotDeviceList(IotDeviceVO deviceInfoVO);


    /**
     * 条件查询物联设备信息
     *
     * @param: deviceInfoVO
     * @return: List<IotDeviceVO>
     * @author: zheng.yan
     * @date: 2022/5/14
     * @version:
     */
    List<IotDeviceVO> queryIotDeviceListSimple(IotDeviceVO deviceInfoVO);

    /**
     * 查询物联设备列表信息
     * @param deviceInfoVO 请求参数
     * @return 物联设备列表信息
     */
    List<IotDeviceVO> querySensorList(IotDeviceVO deviceInfoVO);

    /**
     * 根据设备类型查询物联设备信息
     * @param pageIotDeviceDTO 请求参数
     * @return List<PageIotDeviceVO>
     */
    List<PageIotDeviceVO> queryIotDeviceInfo(PageIotDeviceDTO pageIotDeviceDTO);

    /**
     * 查询物联设备所对应的告警类型编码
     * @param pageIotDeviceDTO 请求参数
     * @return List<PageAlarmTypeVo>
     */
    List<PageAlarmTypeVO> queryAlarmType(PageIotDeviceDTO pageIotDeviceDTO);

    /**
     * 根据租户ID查询雷达设备信息
     *
     * @param: deviceInfoVO
     * @return: List<IotDeviceVO>
     * @author: li.shihao
     * @date: 2023/3/22
     * @version:
     */
    List<IotDeviceVO> queryRadarList(IotDeviceVO deviceInfoVO);

    /**
     *
     * 查询物联/雷达设备区域树
     *
     * @param: deviceVO
     * @return: List<DeviceTreeVO>
     * @author: li.shihao
     * @date: 2023/3/23
     * @version:
     */
    @Deprecated
    List<DeviceTreeVO> getIotOrRadarDeviceTree(IotDeviceVO deviceVO);

    /**
     * 查询物联设备数量
     * @param request 请求参数
     * @return 物联设备数量信息
     */
    SensorDeviceCountVO getSensorDeviceCount(SensorDeviceCountReqDTO request);

    /**
     * 条件查询物联设备信息
     *
     * @param: deviceInfoVO
     * @return: List<IotDeviceVO>
     * @author: zheng.yan
     * @date: 2022/5/14
     * @version:
     */
    List<IotDeviceVO> queryIndIotDeviceListSimple(IotDeviceVO deviceInfoVO);

    /**
     * 更新物联设备信息
     *
     * @param: iotDevice
     * @return: void
     * @author: zheng.yan
     * @date: 2022/5/9
     * @version:
     */
    void updateIotDevice(IotDeviceVO iotDevice);

    /**
     * 获取传感器监测数据
     *
     * @param: qryCond
     * @return: List<com.chinatower.device.mongodb.dto.SensorAttrValueDTO>
     * @author: zheng.yan
     * @date: 2022/5/10
     * @version:
     */
    List<SensorAttrMapVO> listSensorAttrValue(DeviceReqDTO deviceReqDTO);

    /**
     * 新增传感器监测数据
     * @param sensorAttrInfoVO
     * @return
     */
    Result<String> insertSensorData(SensorAttrInfoVO sensorAttrInfoVO);

    /**
     * 查询传感器监控因子
     *
     * @param: deviceInfoDTO
     * @return: List<SensorAttrVO>
     * @author: zheng.yan
     * @date: 2022/5/10
     * @version:
     */
    List<SensorAttrVO> querySensorAttr(DeviceReqDTO deviceInfoDTO);

    /**
     * 根据设备编码获取设备
     *
     * @param deviceCodes
     * @return List<IotDeviceDO>
     * @author zheng.yan
     * @date 2022/5/10
     * @version:
     */
    List<IotDeviceDO> listIotDeviceByCodes(List<String> deviceCodes);

    /**
     * 批量保存和更新物联设备信息
     *
     * @param: iotDevices
     * @return:
     * @author: zheng.yan
     * @date: 2022/5/10
     * @version:
     */
    void batchSaveOrUpdateIotDevice(List<IotDeviceMsgDTO> iotDevices);

    /**
     * 构建物联设备信息
     *
     * @param: iotDeviceMsg
     * @param: sameDeviceInfoList
     * @return: IotDeviceDO
     * @author: zheng.yan
     * @date: 2022/5/10
     * @version:
     */
    IotDeviceDO buildIotDeviceDo(IotDeviceMsgDTO iotDeviceMsg, List<IotDeviceDO> sameDeviceInfoList);

    /**
     * 查询周边传感器设备
     *
     * @param: deviceLayerReq
     * @return: List<IndustryDeviceTree>
     * @author: zheng.yan
     * @date: 2022/5/20
     * @version:
     */
    List<IndustryDeviceTree> querySensorLayer(DeviceLayerReq deviceLayerReq);

    /**
     * 查询部门管理数据
     *
     * @param dept
     * @return
     */
    List<SysDept> selectDeptTree(SysDept dept);

    /**
     * 条件查询雷达设备信息
     *
     * @param: deviceInfoVO
     * @return: List<IotDeviceVO>
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    @Deprecated
    List<IotDeviceVO> queryRadarListPage(IotDeviceVO deviceInfoVO);

    /**
     * 根据条件分页查询设备信息--APP使用
     *
     * @param deviceInfoVO
     * @return TableDataInfo
     */
    Result<PageInfo<IotDeviceVO>> queryRadarOrIotDeviceList(IotDeviceVO deviceInfoVO,Integer categoryCode);

    @Deprecated
    Result<IotDeviceVO> queryIotDeviceByCode(IotDeviceVO deviceInfoVO);

    Result getDeviceListByRadar(RadarDeviceRelDTO radarDeviceRelDTO);

    /**
     * 雷达与摄像机关联关系
     *
     * @param radarDeviceRelDTO
     * @return
     */
    Result saveOrUpdateRel(RadarDeviceRelDTO radarDeviceRelDTO);


    List<DeviceTreeVO> getDeviceMonitorTree(DeviceReqDTO deviceInfoDTO);

    List<DeviceTreeVO> getDeviceChannelMonitorTree(DeviceReqDTO deviceInfoDTO, String platformCode);

    List<DeviceTreeVO> getDeviceChannelList(DeviceReqDTO deviceInfoDTO, String platformCode);

    Result<List<RadarDeviceVO>> getRadarDeviceConfigInfo(RadarDeviceReqDTO radarDeviceReqDTO);

    /**
     * 根据组织机构id查询物联设备
     *
     * @param: iotDeviceEntity
     * @return: List<IotDeviceEntity>
     * @author: zhujun
     * @date: 2022/11/9
     * @version:
     */
    Result<List<IotDeviceEntity>> getIotDeviceByDept(IotDeviceEntity iotDeviceEntity);

    /**
     * 根据地域或传感器类型查询传感器设备列表（以类型分组返回）
     *
     * @param: iotDeviceAreaEntity
     * @return: List<IotDeviceTypeData>
     * @author: zhujun
     * @date: 2022/11/9
     * @version:
     */
    Result<List<IotDeviceTypeData>> getIotGroupByDeviceType(IotDeviceAreaEntity iotDeviceAreaEntity);


    /**
     * 分页查询物联因子信息
     * @param param 参数
     * @return 返回值
     */
    Result<PageInfo<PageIotMonitorVO>> pageIotMonitorInfo(PageIotMonitorDTO param);

    /**
     * 周边分析-周边物联设备信息搜索
     * @param param
     * @return
     */
    List<DeviceDistanceVO> queryIotDevDistanceInfo(DeviceDistanceDTO param);

    List<IotDeviceSensorCalculateVO> querySensorPeriodData4Calculate(DeviceDistance4CalculateDTO param);

    /**
     *
     * 周边分析-周边物联设备信息搜索
     * @param param
     * @return
     */
    @Deprecated
    List<DeviceDistanceVO> queryIotDevDistanceInfo4App(DeviceDistanceDTO param);

    /**
     * 周边分析-周边雷达信息搜索
     * @param param
     * @return
     */
    List<DeviceDistanceVO> queryRadarDevDistanceInfo(DeviceDistanceDTO param);

    /**
     *
     * 周边分析-周边雷达信息搜索
     * @param param
     * @return
     */
    @Deprecated
    List<DeviceDistanceVO> queryRadarDevDistanceInfo4App(DeviceDistanceDTO param);

    /**
     * 根据雷达编码查询雷达详情--APP端用
     * @param iotDeviceVO 入参-设备编码
     * @return IotDeviceVO 喇叭详情
     */
    Result<IotDeviceVO> queryRadarDeviceByCode(IotDeviceVO iotDeviceVO);

    /**
     * 根据关键字查询物联设备列表
     * @param deviceComprehensiveRe
     * @return
     */
    Result<PageInfo<DeviceComprehensiveVO>> queryDeviceListByKeyWord(DeviceComprehensiveReq deviceComprehensiveRe);

    /**
     * 批量获取传感器最新数据
     * @param deviceReqDTO
     * @return
     */
    List<SensorAttrMapVO> getSensorAttrValueBatch(DeviceReqDTO deviceReqDTO);

    /**
     * 查询传感器监测事件数据
     * @param deviceReqDTO
     * @return
     */
    SensorEventInfoVO getSensorEventList(DeviceReqDTO deviceReqDTO);
    /**
     * 查询传感器监测文件数据
     * @param deviceReqDTO
     * @return
     */
    List<SensorEventFileVO> getSensorEventFileList(DeviceReqDTO deviceReqDTO, List<String> eventIdList);

    /**
     * 查询传感器状态信息
     * @param param 参数
     * @return 返回值
     */
    List<SensorStatusInfoVO> querySensorStatusInfo(SensorStatusInfoDTO param);

    /**
     * 查询传感器最新数据
     *
     * @param deviceReqDTO 设备请求DTO对象
     * @return 返回传感器属性结果VO
     */
    Result<SensorAttrResultVO> getSensorDataV2(DeviceReqDTO deviceReqDTO);

    /**
     * 获取监测数据列表
     *
     * @param deviceReqDTO 设备请求DTO
     * @return 监测数据分页VO
     */
    Result<SensorPeriodPageResultVO> pageSensorPeriodDataV2(DeviceReqDTO deviceReqDTO);

    /**
     * 获取监测数据列表
     *
     * @param deviceReqDTO 设备请求DTO
     * @return 监测数据分页VO
     */
    Result<List<SensorMonitorRelInfoVo>> querySensorMonitorRelInfo(DeviceReqDTO deviceReqDTO);


    /**
     * 批量查询传感器最新数据
     */
    Result<List<SensorAttrVO>> getSensorDataBatch(DeviceReqDTO deviceReqDTO);

    /**
     * 查询物联因子列表
     *
     * @return 物联因子列表
     */
    Result<PageInfo<SenSorMonitorConfigDO>> qrySensorMonitor(IotDeviceVO iotDeviceVO);

    /**
     * 查询物联/雷达设备基本信息
     * @param deviceInfoVO
     * @return
     */
    List<IotDeviceVO> queryIotRadarList(IotDeviceVO deviceInfoVO);

    /**
     * 查询物联和雷达设备列表
     * @param iotDevice
     * @return
     */
    List<IotDeviceVO> queryIotRadarDeviceList(IotDeviceVO iotDevice);

    PageInfo<IndustryDeviceTree> queryIotRadarDeviceSites(SiteRequestDTO siteRequestDTO);
}
