package com.chinatower.device.api;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.deprecated.ISensorDeviceServiceMigrate;
import com.chinatower.device.entity.dto.SenSorMonitorConfigDO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.mongodb.request.Page;
import com.chinatower.platform.entity.device.*;
import com.github.pagehelper.PageInfo;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@Component
public class ISensorDeviceService {


    @Autowired
    private IDeviceCommonService deviceCommonService;

    @Autowired
    private ISensorDeviceServiceMigrate sensorDeviceServiceMigrate;

    private static final int IOT_DEVICE_TYPE = 4;

    private static final int RADAR_DEVICE_TYPE = 3;

    /**
     * 查询物联设备详情
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/iotdevice/queryIotDeviceInfo")
    public Result<List<IotDeviceVO>> queryIotDeviceInfo(@RequestBody IotDeviceVO param) {
        return sensorDeviceServiceMigrate.queryIotDeviceInfo(param);
    }

    /**
     * 物联设备组织树
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/iotdevice/getIotMonitorOrgTree")
    public Result<List<DeviceTreeVO>> getIotMonitorOrgTree(@RequestBody IotDeviceVO param) {
        DeviceReqDTO deviceReqDTO = new DeviceReqDTO();
        BeanUtils.copyProperties(param, deviceReqDTO);
        deviceReqDTO.setQueryType(IOT_DEVICE_TYPE);
        return deviceCommonService.getDeviceOrgTree(deviceReqDTO);
    }

    /**
     * 物联设备区域树
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/iotdevice/getIotMonitorTree")
    public Result<List<DeviceTreeVO>> getIotMonitorTree(@RequestBody IotDeviceVO param) {
        DeviceReqDTO deviceReqDTO = new DeviceReqDTO();
        BeanUtils.copyProperties(param, deviceReqDTO);
        deviceReqDTO.setQueryType(IOT_DEVICE_TYPE);
        return deviceCommonService.getDeviceAreaTree(deviceReqDTO);
    }

    /**
     * 物联打点详情数据
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/iotdevice/queryIotDeviceBySite")
    public Result<List<IotDeviceVO>> queryIotDeviceBySite(@RequestBody IotDeviceVO param) {
        SiteRequestDTO siteRequestDTO = new SiteRequestDTO();
        BeanUtils.copyProperties(param, siteRequestDTO);
        siteRequestDTO.setQueryType(IOT_DEVICE_TYPE);
        List<DeviceSiteVO> deviceSiteVOList = deviceCommonService.getDeviceListBySiteCode(siteRequestDTO).getData();
        List<IotDeviceVO> iotDeviceVOList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(deviceSiteVOList)) {
            deviceSiteVOList.stream().forEach(item -> {
                IotDeviceVO iotDeviceVO = new IotDeviceVO();
                BeanUtils.copyProperties(item, iotDeviceVO);
                if (!StringUtils.isEmpty(item.getDeviceStatus())) {
                    iotDeviceVO.setDeviceStatus(Integer.valueOf(item.getDeviceStatus()));
                }
                if (!CollectionUtils.isEmpty(item.getList())) {
                    List<IotDeviceVO> childList = Lists.newArrayList();
                    item.getList().stream().forEach(child -> {
                        IotDeviceVO childIotDeviceVO = new IotDeviceVO();
                        BeanUtils.copyProperties(child, childIotDeviceVO);
                        if (!StringUtils.isEmpty(child.getDeviceStatus())) {
                            childIotDeviceVO.setDeviceStatus(Integer.valueOf(child.getDeviceStatus()));
                        }
                        childList.add(childIotDeviceVO);
                    });
                    iotDeviceVO.setList(childList);
                }

                iotDeviceVOList.add(iotDeviceVO);
            });
        }
        return Result.data(iotDeviceVOList);
    }

    /**
     * 物联监测数据
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/iotdevice/getSensorData")
    public Result<List<SensorAttrVO>> getSensorData(@RequestBody DeviceReqDTO param) {
        return sensorDeviceServiceMigrate.getSensorData(param);
    }

    /**
     * 物联监测数据
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/iotdevice/getSensorDataBatch")
    public Result<List<SensorAttrVO>> getSensorDataBatch(@RequestBody DeviceReqDTO param) {
        return sensorDeviceServiceMigrate.getSensorDataBatch(param);
    }

    /**
     * 雷达设备组织树
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/radar/getRadarMonitorOrgTree")
    public Result<List<DeviceTreeVO>> getRadarMonitorOrgTree(@RequestBody IotDeviceVO param) {
        DeviceReqDTO deviceReqDTO = new DeviceReqDTO();
        BeanUtils.copyProperties(param, deviceReqDTO);
        deviceReqDTO.setQueryType(RADAR_DEVICE_TYPE);
        return deviceCommonService.getDeviceOrgTree(deviceReqDTO);
    }

    /**
     * 雷达设备区域
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/radar/getRadarMonitorTree")
    public Result<List<DeviceTreeVO>> getRadarMonitorTree(@RequestBody IotDeviceVO param) {
        DeviceReqDTO deviceReqDTO = new DeviceReqDTO();
        BeanUtils.copyProperties(param, deviceReqDTO);
        deviceReqDTO.setQueryType(RADAR_DEVICE_TYPE);
        return deviceCommonService.getDeviceAreaTree(deviceReqDTO);
    }

    /**
     * 雷达打点详情数据
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/radar/queryRadarDeviceBySite")
    public Result<List<IotDeviceVO>> queryRadarDeviceBySite(@RequestBody IotDeviceVO param) {
        SiteRequestDTO siteRequestDTO = new SiteRequestDTO();
        BeanUtils.copyProperties(param, siteRequestDTO);
        siteRequestDTO.setQueryType(RADAR_DEVICE_TYPE);
        List<DeviceSiteVO> deviceSiteVOList = deviceCommonService.getDeviceListBySiteCode(siteRequestDTO).getData();
        List<IotDeviceVO> iotDeviceVOList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(deviceSiteVOList)) {
            deviceSiteVOList.stream().forEach(item -> {
                IotDeviceVO iotDeviceVO = new IotDeviceVO();
                BeanUtils.copyProperties(item, iotDeviceVO);
                if (!StringUtils.isEmpty(item.getDeviceStatus())) {
                    iotDeviceVO.setDeviceStatus(Integer.valueOf(item.getDeviceStatus()));
                }
                if (!CollectionUtils.isEmpty(item.getList())) {
                    List<IotDeviceVO> childList = Lists.newArrayList();
                    item.getList().stream().forEach(child -> {
                        IotDeviceVO childIotDeviceVO = new IotDeviceVO();
                        BeanUtils.copyProperties(child, childIotDeviceVO);
                        if (!StringUtils.isEmpty(child.getDeviceStatus())) {
                            childIotDeviceVO.setDeviceStatus(Integer.valueOf(child.getDeviceStatus()));
                        }
                        childList.add(childIotDeviceVO);
                    });
                    iotDeviceVO.setList(childList);
                }

                iotDeviceVOList.add(iotDeviceVO);
            });
        }
        return Result.data(iotDeviceVOList);
    }

    /**
     * 根据站址查询雷达
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/radar/queryRadarDeviceByArea")
    public Result<List<IotDeviceVO>> queryRadarDeviceByArea(@RequestBody IotDeviceVO param) {
        SiteRequestDTO siteRequestDTO = new SiteRequestDTO();
        BeanUtils.copyProperties(param, siteRequestDTO);
        siteRequestDTO.setQueryType(RADAR_DEVICE_TYPE);
        List<IndustryDeviceTree> industryDeviceTreeList = deviceCommonService.getDeviceSites(siteRequestDTO).getData();
        List<IotDeviceVO> iotDeviceVOList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(industryDeviceTreeList)) {
            industryDeviceTreeList.stream().forEach(item -> {
                IotDeviceVO iotDeviceVO = new IotDeviceVO();
                BeanUtils.copyProperties(item, iotDeviceVO);
                if (!StringUtils.isEmpty(item.getDeviceStatus())) {
                    iotDeviceVO.setDeviceStatus(Integer.valueOf(item.getDeviceStatus()));
                }
                if (!CollectionUtils.isEmpty(item.getList())) {
                    List<IotDeviceVO> childList = Lists.newArrayList();
                    item.getList().stream().forEach(child -> {
                        IotDeviceVO childIotDeviceVO = new IotDeviceVO();
                        BeanUtils.copyProperties(child, childIotDeviceVO);
                        if (!StringUtils.isEmpty(child.getDeviceStatus())) {
                            childIotDeviceVO.setDeviceStatus(Integer.valueOf(child.getDeviceStatus()));
                        }
                        childList.add(childIotDeviceVO);
                    });
                    iotDeviceVO.setList(childList);
                }

                iotDeviceVOList.add(iotDeviceVO);
            });
        }
        return Result.data(iotDeviceVOList);
    }

    /**
     * 查询传感器时间段数据
     *
     * @return
     * @author zheng.yan
     * @date 2022/5/11
     * @version
     */
    @PostMapping("/iotdevice/getSensorPeriodData")
    public Result<List<SensorAttrMapVO>> getSensorPeriodData(@RequestBody DeviceReqDTO deviceReqDTO) {
        return sensorDeviceServiceMigrate.getSensorPeriodData(deviceReqDTO);
    }

    /**
     * 查询传感器时间段数据
     * @param deviceReqDTO
     * @return
     */
    @PostMapping("/iotdevice/pageSensorPeriodData")
    public Result<Page<SensorAttrMapVO>> pageSensorPeriodData(@RequestBody DeviceReqDTO deviceReqDTO) {
        return sensorDeviceServiceMigrate.pageSensorPeriodData(deviceReqDTO);
    }

    /**
     * 提供给林业行业进行物联设备计算数据使用，该方法不对其他行业开放使用
     */
    @PostMapping("/iotdevice/querySensorPeriodData4Calculate")
    public Result<List<IotDeviceSensorCalculateVO>> querySensorPeriodData4Calculate(@RequestBody DeviceDistance4CalculateDTO param) {
        return sensorDeviceServiceMigrate.querySensorPeriodData4Calculate(param);
    }

    /**
     * 查询传感器状态信息
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/iotdevice/querySensorStatusInfo")
    public Result<List<SensorStatusInfoVO>> querySensorStatusInfo(@RequestBody SensorStatusInfoDTO param) {
        return sensorDeviceServiceMigrate.querySensorStatusInfo(param);
    }

    /**
     * 根据物联设备编码集合获取所包含的传感器和物联因子的关联信息
     *
     * @param deviceReqDTO 物联设备编码集合
     * @return 返回一个Result对象
     */
    @PostMapping("/iotdevice/querySensorMonitorRelInfo")
    public Result<List<SensorMonitorRelInfoVo>> querySensorMonitorRelInfo(@RequestBody DeviceReqDTO deviceReqDTO) {
        return sensorDeviceServiceMigrate.querySensorMonitorRelInfo(deviceReqDTO);
    }

    /**
     * 分页查询物联因子列表
     * @apiNote  分页查询物联因子列表
     * @apiVersion 3.60
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "pageSize":10,
     *     "pageNum":"1"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     monitorCode：物联因子编码
     *     monitorName：物联因子名称
     *     monitorUnits：物联因子单位
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1690855107300,
     *     "data":
     * }
     * @param       iotDeviceVO
     * @return
     */
    @PostMapping("/iotdevice/qrySensorMonitor")
    public Result<PageInfo<SenSorMonitorConfigDO>> qrySensorMonitor(@RequestBody IotDeviceVO iotDeviceVO) {
        return sensorDeviceServiceMigrate.qrySensorMonitor(iotDeviceVO);
    }

    /**
     *  查询设备图层打点接口
     * @apiNote  根据类型查询对应设备图层打点接口
     * @apiVersion 3.21
     * @tag  三、设备能力
     * @requestDemo
     * {
     * "cityId": "", //市编码
     * "queryType": 1
     * }
     * @responseDemo
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1715307979841,
     *     "data": [
     *         {
     *             "longitude": "116.4687380000",
     *             "latitude": "39.9578090000",
     *             "siteCode": "35689",
     *             "list": [
     *                 {
     *                     "deviceCode": "11010500991327000888",
     *                     "status": "0"
     *                 }
     *             ]
     *         }
     *     ]
     * }
     * @param   siteRequestDTO
     * @return  Result<List<DeviceTreeVO>>
     */
    @PostMapping("/iotdevice/queryIotRadarDeviceSites")
    public Result<PageInfo<IndustryDeviceTree>> queryIotRadarDeviceSites(@RequestBody SiteRequestDTO siteRequestDTO) {
        return sensorDeviceServiceMigrate.queryIotRadarDeviceSites(siteRequestDTO);
    }
}
