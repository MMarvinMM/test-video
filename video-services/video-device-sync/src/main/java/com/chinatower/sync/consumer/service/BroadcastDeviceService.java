package com.chinatower.sync.consumer.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.device.entity.vo.AIotDeviceCenterRsp;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.platform.enums.AIotPlatformCommonEnum;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.sync.consumer.dao.DeviceBroadcastDAO;
import com.chinatower.sync.consumer.dao.SensorStatusDAO;
import com.chinatower.sync.consumer.item.AIotDeviceStatusSync;
import com.chinatower.sync.consumer.item.AIotDeviceSync;
import com.chinatower.sync.consumer.item.CanalBean;
import com.chinatower.sync.consumer.item.DeviceAppModVO;
import com.chinatower.sync.consumer.item.DeviceBroadcastReport;
import com.chinatower.sync.consumer.item.DeviceReadReq;
import com.chinatower.sync.consumer.item.HornStatusReport;
import com.chinatower.sync.consumer.util.DeviceCommonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 喇叭管理
 */
@Slf4j
@Component
public class BroadcastDeviceService {
    @Autowired
    private DeviceBroadcastDAO deviceBroadcastDAO;
    @Autowired
    private SensorStatusDAO sensorStatusDAO;

    @Autowired
    private SensorStatusService sensorStatusService;

    @Autowired
    private DeviceCommonUtils deviceCommonUtils;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void dealDeviceBroadcastMsgNew(List<String> messages) {
        log.info("喇叭信息消费信息:{}, size:{}", messages, messages.size());
        List<DeviceBroadcastReport> dataList = Lists.newArrayList();
        for (String message : messages) {
            CanalBean canalBeans = JSON.parseObject(message, CanalBean.class);
            List<DeviceBroadcastReport> subDataList = canalBeans.getData().stream().map(x -> {
                DeviceBroadcastReport data = JSON.parseObject(JSON.toJSONString(x), DeviceBroadcastReport.class);
                data.setCanalType(canalBeans.getType());
                data.setTs(canalBeans.getTs());
                return data;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(subDataList)) {
                dataList.addAll(subDataList);
            }
        }
        this.dealDeviceBroadcastInfoNew(dataList);
    }

    private void dealDeviceBroadcastInfoNew(List<DeviceBroadcastReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 根据deviceCode取最新的一条
        Map<String, DeviceBroadcastReport> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        DeviceBroadcastReport::getDeviceCode, // keyMapper: 从Record中提取code作为Map的键
                        Function.identity(), // valueMapper: Record对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getTs() >= oldRecord.getTs()) ? newRecord : oldRecord // mergeFunction: 冲突时选择时间戳最新的Record
                ));
        dataList = Lists.newArrayList(dataMap.values());

        List<DeviceBroadcastReport> insertList = Lists.newArrayList();
        List<DeviceBroadcastReport> updateList = Lists.newArrayList();
        List<DeviceBroadcastReport> updateModList = Lists.newArrayList();

        List<String> deviceCodeList = dataList.stream().map(DeviceBroadcastReport::getDeviceCode).distinct().collect(Collectors.toList());
        List<DeviceAppModVO> deviceAppModList = deviceBroadcastDAO.queryDeviceAppModInfo(deviceCodeList);

        // key -> deviceCode value -> appModFlag
        Map<String, Integer> deviceAppModMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(deviceAppModList)) {
            deviceAppModMap = deviceAppModList.stream().collect(
                    Collectors.toMap(DeviceAppModVO::getDeviceCode, DeviceAppModVO::getAppModFlag, (a, b) -> a));
        }
        String deviceCode;
        for (DeviceBroadcastReport broadcastReport : dataList) {
            deviceCode = broadcastReport.getDeviceCode();
            if (!StringUtils.isEmpty(deviceCode)) {
                if (deviceAppModMap.containsKey(deviceCode)) {
                    if (Objects.equals(deviceAppModMap.get(deviceCode), 1)) {
                        //本地应用侧有修改
//                        deviceBroadcastDAO.updateDeviceMod(broadcastReport);
                        updateModList.add(broadcastReport);
                    }
                    else {
                        //补充自己库表属性值
                        this.setBroadcastReportData(broadcastReport);
//                        deviceBroadcastDAO.updateDevice(broadcastReport);
                        updateList.add(broadcastReport);
                    }
                }
                else {
                    //补充自己库表属性值
                    this.setBroadcastReportData(broadcastReport);
//                    deviceBroadcastDAO.addDevice(broadcastReport);
                    insertList.add(broadcastReport);
                }
            }
        }

        // 批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            deviceBroadcastDAO.insertDeviceBatch(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            deviceBroadcastDAO.updateDeviceBatch(updateList);
        }
        // 批量更新mod
        if (CollectionUtils.isNotEmpty(updateModList)) {
            deviceBroadcastDAO.updateDeviceModBatch(updateModList);
        }
    }

    //根据经纬度 补全 省市区乡镇编码
    private void setBroadcastReportData(DeviceBroadcastReport broadcastReport) {
        if (StringUtils.isEmpty(broadcastReport.getLatitude()) || StringUtils.isEmpty(broadcastReport.getLongitude())) {
            return;
        }
        Region region = sensorStatusService.getGisRegion(broadcastReport.getLongitude(), broadcastReport.getLatitude());
        if (region != null) {
            if (StringUtils.isEmpty(region.getProvinceCode())) {
                broadcastReport.setProvinceCode(DeviceNorthIntfConstants.BEIJING_PROVINCE);
                broadcastReport.setCityCode(DeviceNorthIntfConstants.BEIJING_CITY);
                broadcastReport.setCountyCode(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
            } else {
                broadcastReport.setProvinceCode(region.getProvinceCode());
                if (!StringUtils.isEmpty(region.getCityCode())) {
                    broadcastReport.setCityCode(region.getCityCode());
                }
                if (!StringUtils.isEmpty(region.getCountyCode())) {
                    broadcastReport.setCountyCode(region.getCountyCode());
                }
                if (!StringUtils.isEmpty(region.getTownCode())) {
                    broadcastReport.setTownCode(region.getTownCode());
                }
            }
        } else {
            broadcastReport.setProvinceCode(DeviceNorthIntfConstants.BEIJING_PROVINCE);
            broadcastReport.setCityCode(DeviceNorthIntfConstants.BEIJING_CITY);
            broadcastReport.setCountyCode(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void dealDeviceBroadcastMsgFromAIot(List<String> messages) {
        log.info("AIot喇叭信息消费信息:{}, size:{}", JSON.toJSONString(messages), messages.size());
        List<AIotDeviceSync> dataList = Lists.newArrayList();
        for (String message : messages) {
            AIotDeviceSync temp = JSON.parseObject(message, AIotDeviceSync.class);
            dataList.add(temp);
        }
        this.dealDeviceBroadcastInfoFromAIot(dataList);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void dealDeviceBroadcastStatusMsgFromAIot(List<String> messages) {
        log.info("AIot喇叭状态信息消费信息:{}, size:{}", JSON.toJSONString(messages), messages.size());
        List<AIotDeviceStatusSync> dataList = Lists.newArrayList();
        for (String message : messages) {
            AIotDeviceStatusSync temp = JSON.parseObject(message, AIotDeviceStatusSync.class);
            dataList.add(temp);
        }
        this.dealDeviceBroadcastStatusInfoFromAIot(dataList);
    }

    private void dealDeviceBroadcastStatusInfoFromAIot(List<AIotDeviceStatusSync> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 根据deviceCode取最新的一条
        Map<String, AIotDeviceStatusSync> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        AIotDeviceStatusSync::getDeviceCode, // keyMapper: 从对象中提取deviceCode作为Map的键
                        Function.identity(), // valueMapper: 对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getSourceVersion() >= oldRecord.getSourceVersion()) ? newRecord : oldRecord
                ));
        dataList = Lists.newArrayList(dataMap.values());

        List<HornStatusReport> updateList = Lists.newArrayList();
        List<DeviceBroadcastReport> insertList = Lists.newArrayList();
        List<String> insertDeviceCode = Lists.newArrayList();

        List<String> deviceCodeList = dataList.stream().map(AIotDeviceStatusSync::getDeviceCode).filter(deviceCode -> !StringUtils.isEmpty(deviceCode)).distinct().collect(Collectors.toList());
        List<DeviceAppModVO> deviceAppModList = deviceBroadcastDAO.queryDeviceAppModInfoForAIot(deviceCodeList);

        Map<String, Long> statusVersionMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(deviceAppModList)) {
            statusVersionMap = deviceAppModList.stream().collect(
                    Collectors.toMap(DeviceAppModVO::getDeviceCode, DeviceAppModVO::getStatusVersion, (a, b) -> a));
        }
        String deviceCode;
        for (AIotDeviceStatusSync statusSync : dataList) {
            deviceCode = statusSync.getDeviceCode();
            if (statusVersionMap.containsKey(deviceCode)) {
                if (Objects.isNull(statusSync.getSourceVersion()) || statusSync.getSourceVersion() <= statusVersionMap.get(deviceCode)) {
                    continue;
                }
                HornStatusReport item = deviceCommonUtils.hornStatusDTOConvert(statusSync);
                updateList.add(item);
            }
            else {
                insertDeviceCode.add(deviceCode);
            }
        }

        if (CollectionUtils.isNotEmpty(insertDeviceCode)) {
            try {
                //反查加入新增
                DeviceReadReq qry = new DeviceReadReq(insertDeviceCode, AIotPlatformCommonEnum.Platform.SMARTBC.getPlatformCode());
                AIotDeviceCenterRsp resultVO = deviceCommonUtils.callDeviceCenter(qry, NetConnectUtils.RequestParamter.URLMappingEnum.DEVICE_CENTER_QRY_DEVICE, "");
                if (AIotPlatformCommonEnum.CODE_SUCCESS_CODE.equals(resultVO.getCode())) {
                    List<AIotDeviceSync> devices = JSON.parseArray(JSON.toJSONString(resultVO.getData())).toJavaList(AIotDeviceSync.class);
                    for (AIotDeviceSync device : devices) {
                        DeviceBroadcastReport item = deviceCommonUtils.hornDTOConvert(device);
                        AIotDeviceStatusSync aIotStatusInfo = dataMap.get(device.getDeviceCode());
                        if (aIotStatusInfo != null) {
                            item.setStatusVersion(aIotStatusInfo.getSourceVersion());
                            item.setOnlineStatus(aIotStatusInfo.getDeviceStatus() != null ? String.valueOf(aIotStatusInfo.getDeviceStatus()) : null);
                        }
                        insertList.add(item);
                    }
                }
            } catch (Exception e) {
                log.error("反查设备信息失败, e:", e);
            }
        }

        if (CollectionUtils.isNotEmpty(insertList)) {
            deviceBroadcastDAO.insertDeviceBatchByAIot(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            deviceBroadcastDAO.updateHornDeviceBatchByAIot(updateList);
        }
    }

    public void dealDeviceBroadcastInfoFromAIot(List<AIotDeviceSync> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 根据deviceCode取最新的一条
        Map<String, AIotDeviceSync> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        AIotDeviceSync::getDeviceCode, // keyMapper: 从对象中提取deviceCode作为Map的键
                        Function.identity(), // valueMapper: 对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getSourceVersion() >= oldRecord.getSourceVersion()) ? newRecord : oldRecord
                ));
        dataList = Lists.newArrayList(dataMap.values());

        List<DeviceBroadcastReport> updateList = Lists.newArrayList();
        List<DeviceBroadcastReport> updateModList = Lists.newArrayList();

        List<String> deviceCodeList = dataList.stream().map(AIotDeviceSync::getDeviceCode).filter(deviceCode -> !StringUtils.isEmpty(deviceCode)).distinct().collect(Collectors.toList());
        List<DeviceAppModVO> deviceAppModList = deviceBroadcastDAO.queryDeviceAppModInfoForAIot(deviceCodeList);

        Map<String, Long> deviceVersionMap = Maps.newHashMap();
        Map<String, Integer> deviceAppModMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(deviceAppModList)) {
            deviceAppModMap = deviceAppModList.stream().collect(
                    Collectors.toMap(DeviceAppModVO::getDeviceCode, DeviceAppModVO::getAppModFlag, (a, b) -> a));
            deviceVersionMap = deviceAppModList.stream().collect(
                    Collectors.toMap(DeviceAppModVO::getDeviceCode, DeviceAppModVO::getDeviceVersion, (a, b) -> a));
        }
        String deviceCode;
        for (AIotDeviceSync deviceSyncItem : dataList) {
            deviceCode = deviceSyncItem.getDeviceCode();
            if (deviceAppModMap.containsKey(deviceCode)) {
                if (Objects.isNull(deviceSyncItem.getSourceVersion()) || deviceSyncItem.getSourceVersion() <= deviceVersionMap.get(deviceCode)) {
                    continue;
                }
                DeviceBroadcastReport item = deviceCommonUtils.hornDTOConvert(deviceSyncItem);
                if (Objects.equals(deviceAppModMap.get(deviceCode), 1)) {
                    updateModList.add(item);
                }
                else {
                    //补充自己库表属性值
                    updateList.add(item);
                }
            }
        }

        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            deviceBroadcastDAO.updateDeviceBatchByAIot(updateList);
        }
        // 批量更新mod
        if (CollectionUtils.isNotEmpty(updateModList)) {
            deviceBroadcastDAO.updateDeviceModBatchByAIot(updateModList);
        }
    }

    public void auditDeviceInfo(String param) {
        List<AIotDeviceSync> data = deviceCommonUtils.auditDeviceInfo(param, AIotPlatformCommonEnum.Platform.SMARTBC.getPlatformCode());
        if (!CollectionUtils.isEmpty(data)) {
            this.dealDeviceBroadcastInfoFromAIot(data);
        }
    }

    public void auditDeviceStatusInfo(String param) {
        List<AIotDeviceStatusSync> data = deviceCommonUtils.auditDeviceStatusInfo(param, AIotPlatformCommonEnum.Platform.SMARTBC.getPlatformCode());
        if (!CollectionUtils.isEmpty(data)) {
            this.dealDeviceBroadcastStatusInfoFromAIot(data);
        }
    }
}
