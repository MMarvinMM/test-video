package com.chinatower.analysis.modules.lowalt.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.analysis.api.lowalt.domain.DeviceStatsDTO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceDistributionVO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceOnlineRateVO;
import com.chinatower.analysis.api.lowalt.domain.vo.DeviceStatsVO;
import com.chinatower.analysis.modules.common.Constants;
import com.chinatower.analysis.modules.lowalt.mapper.LowaltDeviceStatisticsMapper;
import com.chinatower.analysis.modules.lowalt.service.LowaltDeviceStatsService;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IHornDeviceService;
import com.chinatower.device.api.IVideoDeviceMgmtService;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 设备统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LowaltDeviceStatsServiceImpl implements LowaltDeviceStatsService {

    private final LowaltDeviceStatisticsMapper deviceStatisticsMapper;
    private final SystemFeignClient systemFeignClient;
    private final IHornDeviceService iHornDeviceService;
    private final IVideoDeviceMgmtService iVideoDeviceMgmtService;

    @Override
    public List<DeviceStatsVO> deviceStatistics(DeviceStatsDTO deviceStatsDTO) {
        try {
            // 查询低空设备类型和订购的物联设备信息
            this.getOrderIotDevice(deviceStatsDTO);
            //查询物联设备统计信息
            log.info("查询设备统计入参，{}", JSONObject.toJSONString(deviceStatsDTO));
            List<DeviceStatsVO> statsList = deviceStatisticsMapper.selectDeviceStatistics(deviceStatsDTO);
            if(!CollectionUtils.isEmpty(statsList)){
                // 计算告警数量
                Map<String,String> dictMap = this.getDictType(Constants.SENSING_DEVICE_TYPE);
                for (DeviceStatsVO stats : statsList) {
                    stats.setWarningCount(0);  //查询设备自身告警信息 默认为0
                    stats.setDeviceTypeName(dictMap.get(stats.getDeviceType()));
                }
            }
            return statsList;
        } catch (Exception e) {
            log.error("查询设备统计失败", e);
            return null;
        }
    }

    @Override
    public List<DeviceDistributionVO> getDeviceDistributionTop5(DeviceStatsDTO deviceStatsDTO) {
        try {
            // 查询低空设备类型和订购的物联设备信息
            this.getOrderIotDevice(deviceStatsDTO);
            //查询设备top5区域信息
            log.info("查询设备top5区域信息入参，{}", JSONObject.toJSONString(deviceStatsDTO));
            List<DeviceDistributionVO> distributionList = deviceStatisticsMapper.selectDeviceDistributionTop5(deviceStatsDTO);

            if(!CollectionUtils.isEmpty(distributionList)){
                //查询top5区域对应的设备和数量
                List<String> areaCodeList = distributionList.stream().map(DeviceDistributionVO::getAreaCode).collect(Collectors.toList());
                deviceStatsDTO.setAreaCodeList(areaCodeList);
                List<Map<String,String>> deviceTypeCountList = deviceStatisticsMapper.selectDeviceTypeCountTop5(deviceStatsDTO);
                Map<String, List<DeviceDistributionVO.DeviceCount>> deviceTypeVo =  this.groupByAreaCode(deviceTypeCountList);
                //查询区域名称并转义
                List<Map<String,String>> areaNameList = deviceStatisticsMapper.getAreaName(areaCodeList);
                Map<String, String> areaCodeMap = areaNameList.stream()
                        .collect(Collectors.toMap(map -> map.get("areaCode"),  map -> map.get("areaName"), (existing, replacement) -> existing  // key 重复时保留已有的
                        ));
                // 计算告警数量
                for (DeviceDistributionVO stats : distributionList) {
                    stats.setAreaName(areaCodeMap.get(stats.getAreaCode()));
                    stats.setDeviceTypeCount(deviceTypeVo.get(stats.getAreaCode()));
                }
            }
            return distributionList;
        } catch (Exception e) {
            log.error("查询设备分布失败",e);
            return null;
        }
    }


    @Override
    public List<DeviceOnlineRateVO> getDeviceOnlineRate(DeviceStatsDTO deviceStatsDTO) {
        try {
            // 查询低空设备类型和订购的物联设备信息
            this.getOrderIotDevice(deviceStatsDTO);
            //查询物联设备在线率
            log.info("查询设备在线率入参，{}", JSONObject.toJSONString(deviceStatsDTO));
            List<DeviceOnlineRateVO> onlineRateList = deviceStatisticsMapper.selectDeviceOnlineRate(deviceStatsDTO);

            // 计算所有设备在线率
            if (!onlineRateList.isEmpty()) {
                Map<String,String> dictMap = this.getDictType(Constants.SENSING_DEVICE_TYPE);
                // 处理空值情况，使用默认值0
                int totalOnline = onlineRateList.stream()
                        .mapToInt(vo -> vo.getOnlineCount() != null ? vo.getOnlineCount() : 0)
                        .sum();
                int totalDevices = onlineRateList.stream()
                        .mapToInt(vo -> vo.getTotalCount() != null ? vo.getTotalCount() : 0)
                        .sum();
                double allDeviceOnlineRate = totalDevices > 0 ? (totalOnline * 100.0 / totalDevices) : 0.0;

                for (DeviceOnlineRateVO onlineRate : onlineRateList) {
                    onlineRate.setDeviceTypeName(dictMap.get(onlineRate.getDeviceType()));
                    onlineRate.setAllDeviceOnlineRate(Math.round(allDeviceOnlineRate * 100.0) / 100.0);
                }
            }

            return onlineRateList;
        } catch (Exception e) {
            log.error("查询设备在线率失败", e);
            return null;
        }
    }

    /**
     * 查询订购设备信息
     * @param deviceStatsDTO
     */
    private void getOrderIotDevice(DeviceStatsDTO deviceStatsDTO) {
        if(Constants.ORDER_FLAG_IOT_DEVICE.equals(deviceStatsDTO.getOrderFlag())|| StringUtils.isEmpty(deviceStatsDTO.getOrderFlag())){
            List<String> deviceList = new ArrayList<>();
            //查询低空物联设备字典值
            Result<List<SysDictDataResponse>> sensingDeviceType = systemFeignClient.dictDataType(Constants.LOW_ALT_IOT_DEVICE_TYPE);
            if (sensingDeviceType != null && sensingDeviceType.getData() != null && !sensingDeviceType.getData().isEmpty()) {
                List<String> iotTypeList = sensingDeviceType.getData().stream().map(SysDictDataResponse::getDictValue).collect(Collectors.toList());
                deviceStatsDTO.setIotDeviceTypeList(iotTypeList);
                // 查询订购物联设备列表
                IotDeviceVO iotDeviceVO = new IotDeviceVO();
                iotDeviceVO.setIotDeviceTypeList(iotTypeList);
                List<String> radarDeviceType = new ArrayList<>();
                radarDeviceType.add(Constants.LOW_ALT_RADAR_DEVICE_TYPE);
                iotDeviceVO.setRadarDeviceTypeList(radarDeviceType);
                log.info("queryIotRadarDeviceList入参：{}",JSONObject.toJSONString(iotDeviceVO));
                Result<List<IotDeviceVO>> listResult = iHornDeviceService.queryIotRadarDeviceList(iotDeviceVO);
                log.info("queryIotRadarDeviceList出参：{}",JSONObject.toJSONString(listResult));
                if(ResultCode.SUCCESS.getCode()==listResult.getCode()&&CollectionUtils.isNotEmpty(listResult.getData())){
                    deviceList =  listResult.getData().stream().map(IotDeviceVO::getDeviceCode).collect(Collectors.toList());
                }
            }
            deviceStatsDTO.setIotDeviceCodeList(deviceList);
        }
        if(Constants.ORDER_FLAG_CAMERA.equals(deviceStatsDTO.getOrderFlag()) || StringUtils.isEmpty(deviceStatsDTO.getOrderFlag())){
            //查询订购摄像机设备列表
            DeviceInfoVO deviceInfoVO = new DeviceInfoVO();
            deviceInfoVO.setExport(true);
            log.info("iVideoDeviceMgmtService.queryDeviceInfoPage入参：{}",JSONObject.toJSONString(deviceInfoVO));
            TableDataInfo tableDataInfo = iVideoDeviceMgmtService.queryDeviceInfoPage(deviceInfoVO);
            log.info("iVideoDeviceMgmtService.queryDeviceInfoPage出参：{}",JSONObject.toJSONString(tableDataInfo));
            List<String> cameraList = new ArrayList<>();
            if(tableDataInfo!=null&&CollectionUtils.isNotEmpty(tableDataInfo.getRows())){
                try {
                    String rowsJson = JSONObject.toJSONString(tableDataInfo.getRows());
                    List<DeviceInfoVO> rows = JSONObject.parseArray(rowsJson, DeviceInfoVO.class);
                    cameraList = rows.stream()
                            .map(DeviceInfoVO::getDeviceCode)
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.toList());

                } catch (Exception e) {
                    log.error("JSON 转换 DeviceInfoVO 失败，尝试其他方式", e);
                }
            }
            deviceStatsDTO.setCameraDeviceCodeList(cameraList);
        }
        log.info("查询的订购设备编码：{}" ,JSONObject.toJSONString(deviceStatsDTO));
    }


    /**
     * 查询翻译字典信息
     * @param type
     */
    public Map<String,String> getDictType(String type) {
        Map<String,String> typeList = new HashMap<>();
        //查询低空物联设备字典值
        Result<List<SysDictDataResponse>> sysDictData = systemFeignClient.dictDataType(type);
        if (sysDictData != null && sysDictData.getData() != null && !sysDictData.getData().isEmpty()) {
            typeList = sysDictData.getData().stream().collect(Collectors.toMap(SysDictDataResponse::getDictValue,SysDictDataResponse::getDictLabel));
        }
        log.info("查询字典信息，{}，{}",type, JSONObject.toJSONString(typeList));
        return typeList;
    }

    /**
     * 将 List<Map<String, String>> 按 areaCode 分组，并转为 Map<String, List<DeviceCount>>
     *
     * @param deviceTypeCountList 输入数据，每个 Map 含 areaCode, deviceType, deviceCount
     * @return Map<String, List<DeviceCount>>，key 是 areaCode，value 是该区域下的设备类型列表
     */
    private Map<String, List<DeviceDistributionVO.DeviceCount>> groupByAreaCode(List<Map<String, String>> deviceTypeCountList) {
        Map<String,String> dictMap = this.getDictType(Constants.SENSING_DEVICE_TYPE);
        return deviceTypeCountList.stream()
            .collect(Collectors.groupingBy(
                map -> map.get("areaCode"),  // 按 areaCode 分组
                Collectors.mapping(
                    map -> {
                        String deviceType = String.valueOf(map.get("deviceType"));
                        String deviceCountStr = String.valueOf(map.get("deviceCount"));
                        // 获取设备类型名称
                        String deviceTypeName = dictMap.get(deviceType);

                        Integer deviceCount = null;
                        try {
                            deviceCount = Integer.parseInt(deviceCountStr);
                        } catch (NumberFormatException e) {
                            deviceCount = 0;
                        }

                        return new DeviceDistributionVO.DeviceCount(deviceTypeName, deviceType, deviceCount);
                    },
                    Collectors.toList()
                )
            ));
    }


}