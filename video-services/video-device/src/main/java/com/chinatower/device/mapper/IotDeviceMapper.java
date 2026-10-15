package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dataobject.IotDeviceDO;
import com.chinatower.device.dataobject.SensorDeviceTypeVO;
import com.chinatower.device.entity.dto.DeviceComprehensiveReq;
import com.chinatower.device.entity.vo.DeviceComprehensiveVO;
import com.chinatower.device.entity.dto.SenSorMonitorConfigDO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.industry.dto.DeviceLayerReq;
import com.chinatower.industry.dto.SensorDeviceCountReqDTO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.device.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 物联设备表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-05-07
 */
public interface IotDeviceMapper extends BaseMapper<IotDeviceDO> {
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
    List<DeviceTreeVO> getIotMonitorTree(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     *
     * @param deviceInfoVO
     * @return
     */
    List<DeviceTreeVO> getIotMonitorOrgTree(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     *
     * @param deviceInfoVO
     * @return
     */
    @Deprecated
    List<IotDeviceVO> queryIotDeviceBySite(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     *
     * @param deviceInfoVO
     * @return
     */
    @Deprecated
    List<IotDeviceVO> queryIotDeviceByArea(@Param("param") IotDeviceVO deviceInfoVO);

    List<IndustryDeviceTree> queryIotDeviceSites(@Param("param") SiteRequestDTO siteRequestDTO);

    /**
     *
     * @param deviceInfoVO
     * @return
     */
    List<DeviceTreeVO> getRadarMonitorTree(@Param("param") IotDeviceVO deviceInfoVO);


    /**
     *
     * @param deviceInfoVO
     * @return
     */
    List<DeviceTreeVO> getRadarMonitorOrgTree(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     *
     * @param deviceInfoVO
     * @return
     */
    List<IotDeviceVO> queryRadarDeviceBySite(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     *
     * @param deviceInfoVO
     * @return
     */
    List<IotDeviceVO> queryRadarDeviceByArea(@Param("param") IotDeviceVO deviceInfoVO);

    List<DeviceTreeVO> getDeviceMonitorTree(@Param("param") DeviceReqDTO deviceInfoDTO);

    List<DeviceTreeVO> getDeviceChannelMonitorTree(@Param("param") DeviceReqDTO deviceInfoDTO, @Param("platformCode") String platformCode);

    List<DeviceTreeVO> getDeviceChannelList(@Param("param") DeviceReqDTO deviceInfoDTO, @Param("platformCode") String platformCode);

    /**
     * 条件查询物联设备信息
     *
     * @param: deviceInfoVO
     * @return: List<IotDeviceVO>
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    List<IotDeviceVO> queryIotDeviceList(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     * 条件查询物联设备信息--不关联订购
     *
     * @param deviceInfoVO
     * @return List<IotDeviceVO>
     */
    List<IotDeviceVO> queryIotDeviceInfo(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     * 条件查询雷达设备信息--不关联订购
     *
     * @param deviceInfoVO
     * @return List<IotDeviceVO>
     */
    List<IotDeviceVO> queryRadarDeviceInfo(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     * 条件查询物联设备信息
     *
     * @param: deviceInfoVO
     * @return: List<IotDeviceVO>
     * @author: zheng.yan
     * @date: 2022/5/13
     * @version:
     */
    List<IotDeviceVO> queryIotDeviceListSimple(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     * 查询物联设备信息
     * @param deviceInfoVO 请求参数
     * @return 物联设备信息
     */
    List<IotDeviceVO> querySensorList(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     * 根据ID查询雷达设备信息
     *
     * @param: deviceInfoVO
     * @return: List<IotDeviceVO>
     * @author: li.shihao
     * @date: 2023/3/22
     * @version:
     */
    List<IotDeviceVO> queryRadarList(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     *
     * 查询物联/雷达设备区域树
     * @param deviceInfoVO
     * @return
     */
    @Deprecated
    List<DeviceTreeVO> getIotOrRadarDeviceTree(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     * 查询物联设备数量
     * @param request 请求参数
     * @return 物联设备数量信息
     */
    SensorDeviceCountVO getSensorDeviceCount(@Param("param") SensorDeviceCountReqDTO request);

    /**
     * 条件查询行业物联设备信息
     *
     * @param: deviceInfoVO
     * @return: List<IotDeviceVO>
     * @author: zheng.yan
     * @date: 2022/5/21
     * @version:
     */
    List<IotDeviceVO> queryIndIotDeviceListSimple(@Param("param") IotDeviceVO deviceInfoVO);

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
     * 查询周边物联设备
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
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 条件查询雷达设备信息
     *
     * @param: deviceInfoVO
     * @return: List<IotDeviceVO>
     * @author: zheng.yan
     * @date: 2022/5/8
     * @version:
     */
    List<IotDeviceVO> queryRadarListPage(@Param("param") IotDeviceVO deviceInfoVO);

    List<IotDeviceVO> queryRadarOrIotDeviceList(@Param("param") IotDeviceVO deviceInfoVO,Integer categoryCode);

    IotDeviceVO queryIotDeviceByCode(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     * 查询订购雷达的客户信息
     * @param deviceCodeList 雷达编码列表
     * @return 订购雷达的客户信息
     */
    List<RadarDeviceVO> getRadarDeviceConfigInfoNew(@Param("deviceCodeList") List<String> deviceCodeList);

    /**
     * 根据组织机构id查询物联设备
     */
    List<IotDeviceEntity> getIotDeviceByDept(IotDeviceEntity iotDeviceEntity);

    /**
     * 根据地域或传感器类型查询传感器设备列表（以类型分组返回）
     */
    List<IotDeviceTypeData> getIotGroupByDeviceType(IotDeviceAreaEntity iotDeviceAreaEntity);

    /**
     * 根据设备类型查询物联设备信息
     */
    List<PageIotDeviceVO> getIotInfoByDeviceType(@Param("param") PageIotDeviceDTO pageIotDeviceDTO);

    /**
     * 查询物联设备所对应的告警类型编码
     */
    List<PageAlarmTypeVO> getAlarmTypeList(@Param("param") PageIotDeviceDTO pageIotDeviceDTO);


    List<String> pageIotMonitorDeviceCode(@Param("param") PageIotMonitorDTO param);

    /**
     * 分页查询物联因子信息
     * @param deviceCodeList 参数
     * @return 返回值
     */
    List<PageIotMonitorVO> pageIotMonitorInfo(@Param("deviceCodeList") List<String> deviceCodeList);


    /**
     * 周边分析-周边物联设备信息搜索
     * @param param
     * @return
     */
    List<DeviceDistanceVO> queryIotDevDistanceInfo(@Param("param")DeviceDistanceDTO param);

    List<DeviceDistanceVO> queryIotDevDistanceInfo4Calculate(@Param("param")DeviceDistance4CalculateDTO param);

    /**
     * 周边分析-周边物雷达信息搜索
     * @param param
     * @return
     */
    List<DeviceDistanceVO> queryRadarDevDistanceInfo(@Param("param")DeviceDistanceDTO param);

    IotDeviceVO queryRadarDeviceByCode(@Param("param") IotDeviceVO iotDeviceVO);

    /**
     * 根据关键字查询物联设备列表
     *
     * @param deviceComprehensiveReq
     * @return List<DeviceComprehensiveVO>
     */
    List<DeviceComprehensiveVO> queryDeviceListByKeyWord(@Param("param") DeviceComprehensiveReq deviceComprehensiveReq);

    List<IotDeviceVO> queryIotList(IotDeviceVO iotDeviceVO);

    List<IotDeviceVO> queryUavList(IotDeviceVO iotDeviceVO);

    List<IotDeviceVO> queryHornList(IotDeviceVO iotDeviceVO);

    List<IotDeviceVO> queryRecorderList(IotDeviceVO iotDeviceVO);

    int updateIotDeviceBatch(@Param("list") List<IotDeviceVO> list);

    int updateUavDeviceBatch(@Param("list") List<IotDeviceVO> list);

    int updateHornDeviceBatch(@Param("list") List<IotDeviceVO> list);

    int updateRecorderDeviceBatch(@Param("list") List<IotDeviceVO> list);

    List<SensorStatusInfoVO> querySensorStatusInfo(@Param("param") SensorStatusInfoDTO param);


    /**
     * 根据类型编码列表获取传感器设备类型VO列表
     *
     * @param typeCodeList 类型编码列表
     * @return 传感器设备类型VO列表
     */
    List<SensorDeviceTypeVO> getSensorDeviceTypeByCode(@Param("list")List<String> typeCodeList);

    /**
     * 根据物联设备编码集合去重查询传感器和物联因子关系
     * @param deviceInfoDTO 物联设备编码集合
     * @return
     */
    List<SensorMonitorRelInfoVo> queryMonitorInfoByDeviceCodes(DeviceReqDTO deviceInfoDTO);

    List<SenSorMonitorConfigDO> qrySensorMonitor();


    /**
     * 根据条件查询物联/雷达设备
     * @param deviceInfoVO
     * @return
     */
    List<IotDeviceVO> queryIotRadarList(@Param("param") IotDeviceVO deviceInfoVO);

    List<IotDeviceVO> queryIotRadarDeviceList(@Param("param") IotDeviceVO deviceInfoVO);

    /**
     * 查询物联/雷达设备图层打点节点
     * @param siteRequestDTO
     * @return
     */
    List<IndustryDeviceTree> queryIotRadarSiteList(@Param("param") SiteRequestDTO siteRequestDTO);

    //查询物联/雷达设备图层打点节点
    List<IndustryDeviceTree> queryIotRadarDeviceSites(@Param("param") SiteRequestDTO siteRequestDTO);
}
