package com.chinatower.sync.consumer.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.sync.consumer.dao.RecorderDeviceDAO;
import com.chinatower.sync.consumer.item.*;
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

@Slf4j
@Component
public class RecorderDeviceService {

    @Autowired
    private RecorderDeviceDAO recorderDeviceDAO;

    @Autowired
    private SensorStatusService sensorStatusService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void dealRecorderDeviceMsgNew(List<String> messages) {
        log.info("执法记录仪设备消费信息:{}, size:{}", messages, messages.size());
        List<RecorderDeviceReport> dataList = Lists.newArrayList();
        for (String message : messages) {
            CanalBean canalBeans = JSON.parseObject(message, CanalBean.class);
            List<RecorderDeviceReport> subDataList = canalBeans.getData().stream().map(x -> {
                RecorderDeviceReport data = JSON.parseObject(JSON.toJSONString(x), RecorderDeviceReport.class);
                data.setCanalType(canalBeans.getType());
                data.setTs(canalBeans.getTs());
                return data;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(subDataList)) {
                dataList.addAll(subDataList);
            }
        }
        this.dealDeviceDataInfoNew(dataList);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void dealRecorderDeviceChannelMsgNew(List<String> messages) {
        log.info("执法记录仪设备通道消费信息:{}, size:{}", messages, messages.size());
        List<RecorderChannelReport> dataList = Lists.newArrayList();
        for (String message : messages) {
            CanalBean canalBeans = JSON.parseObject(message, CanalBean.class);
            List<RecorderChannelReport> subDataList = canalBeans.getData().stream().map(x -> {
                RecorderChannelReport data = JSON.parseObject(JSON.toJSONString(x), RecorderChannelReport.class);
                data.setCanalType(canalBeans.getType());
                data.setTs(canalBeans.getTs());
                return data;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(subDataList)) {
                dataList.addAll(subDataList);
            }
        }
        this.dealDeviceChannelDataInfoNew(dataList);
    }

    public void dealDeviceDataInfoNew(List<RecorderDeviceReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 根据deviceCode取最新的一条
        Map<String, RecorderDeviceReport> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        RecorderDeviceReport::getDeviceCode, // keyMapper: 从Record中提取code作为Map的键
                        Function.identity(), // valueMapper: Record对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getTs() >= oldRecord.getTs()) ? newRecord : oldRecord // mergeFunction: 冲突时选择时间戳最新的Record
                ));
        dataList = Lists.newArrayList(dataMap.values());

        List<RecorderDeviceReport> insertList = Lists.newArrayList();
        List<RecorderDeviceReport> updateList = Lists.newArrayList();
        List<RecorderDeviceReport> updateModList = Lists.newArrayList();

        List<String> deviceCodeList = dataList.stream().map(RecorderDeviceReport::getDeviceCode).distinct().collect(Collectors.toList());
        List<DeviceAppModVO> deviceAppModList = recorderDeviceDAO.queryDeviceAppModInfo(deviceCodeList);
        // key -> deviceCode value -> appModFlag
        Map<String, Integer> deviceAppModMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(deviceAppModList)) {
            deviceAppModMap = deviceAppModList.stream().collect(
                    Collectors.toMap(DeviceAppModVO::getDeviceCode, DeviceAppModVO::getAppModFlag, (a, b) -> a));
        }

        String deviceCode;
        for (RecorderDeviceReport recorderDeviceReport : dataList) {
            deviceCode = recorderDeviceReport.getDeviceCode();
            if (!StringUtils.isEmpty(recorderDeviceReport.getDeviceCode())) {
                //运管删除该设备，应用平台同步更改状态为不可用
                if (Constants.TWO.equals(recorderDeviceReport.getDelFlag())) {
                    //补充自己库表属性值
                    this.setRecorderDeviceReportData(recorderDeviceReport);
                    //运管测删除标识为2，要改为通用这边的1
                    recorderDeviceReport.setDelFlag(Constants.DELETEFLAG_1);
//                    recorderDeviceDAO.updateRecorderDevice(recorderDeviceReport);
                    updateList.add(recorderDeviceReport);
                } else {
                    if (deviceAppModMap.containsKey(deviceCode)) {
                        if (Objects.equals(deviceAppModMap.get(deviceCode), 1)) {
                            //本地应用侧有修改
//                            recorderDeviceDAO.updateRecorderDeviceMod(recorderDeviceReport);
                            updateModList.add(recorderDeviceReport);
                        } else {
                            //补充自己库表属性值
                            this.setRecorderDeviceReportData(recorderDeviceReport);
//                            recorderDeviceDAO.updateRecorderDevice(recorderDeviceReport);
                            updateList.add(recorderDeviceReport);
                        }
                    } else {
                        //补充自己库表属性值
                        this.setRecorderDeviceReportData(recorderDeviceReport);
//                        recorderDeviceDAO.addRecorderDevice(recorderDeviceReport);
                        insertList.add(recorderDeviceReport);
                    }

                }
            }
        }
        // 批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            recorderDeviceDAO.insertDeviceBatch(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            recorderDeviceDAO.updateDeviceBatch(updateList);
        }
        // 批量更新mod
        if (CollectionUtils.isNotEmpty(updateModList)) {
            recorderDeviceDAO.updateDeviceModBatch(updateModList);
        }
    }

    public void dealDeviceChannelDataInfoNew(List<RecorderChannelReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 根据channelCode取最新的一条
        Map<String, RecorderChannelReport> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        RecorderChannelReport::getChannelCode, // keyMapper: 从Record中提取code作为Map的键
                        Function.identity(), // valueMapper: Record对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getTs() >= oldRecord.getTs()) ? newRecord : oldRecord // mergeFunction: 冲突时选择时间戳最新的Record
                ));
        dataList = Lists.newArrayList(dataMap.values());

        List<RecorderChannelReport> insertList = Lists.newArrayList();
        List<RecorderChannelReport> updateList = Lists.newArrayList();

        List<String> channelCodeList = dataList.stream().map(RecorderChannelReport::getChannelCode).distinct().collect(Collectors.toList());
        List<String> existChannelCodeList = recorderDeviceDAO.queryExistChannelCodeList(channelCodeList);

        for (RecorderChannelReport recorderChannelReport : dataList) {
            if (!StringUtils.isEmpty(recorderChannelReport.getDeviceCode()) && !StringUtils.isEmpty(recorderChannelReport.getChannelCode())) {
                //运管删除该设备，应用平台同步更改状态为不可用
                if (Constants.DELETE.equals(recorderChannelReport.getCanalType())) {
                    recorderChannelReport.setDelFlag(Constants.DELETEFLAG_1);
//                    recorderDeviceDAO.updateRecorderDeviceChannel(recorderChannelReport);
                    updateList.add(recorderChannelReport);
                } else {
                    if (existChannelCodeList.contains(recorderChannelReport.getChannelCode())) {
//                        recorderDeviceDAO.updateRecorderDeviceChannel(recorderChannelReport);
                        if (recorderChannelReport.getDelFlag().equals(Constants.DELETEFLAG_2)){
                            recorderChannelReport.setDelFlag(Constants.DELETEFLAG_1);
                        }
                        updateList.add(recorderChannelReport);
                    } else {
//                        recorderDeviceDAO.addRecorderDeviceChannel(recorderChannelReport);
                        insertList.add(recorderChannelReport);
                    }
                }
            }
        }
        // 批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            recorderDeviceDAO.insertDeviceChannelBatch(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            recorderDeviceDAO.updateDeviceChannelBatch(updateList);
        }
    }

    //根据经纬度 补全 省市区乡镇编码
    private void setRecorderDeviceReportData(RecorderDeviceReport recorderDeviceReport) {
        if (StringUtils.isEmpty(recorderDeviceReport.getLatitude()) || StringUtils.isEmpty(recorderDeviceReport.getLongitude())) {
            return;
        }
        Region region = sensorStatusService.getGisRegion(recorderDeviceReport.getLongitude(), recorderDeviceReport.getLatitude());
        if (region != null) {
            if (StringUtils.isEmpty(region.getProvinceCode())) {
                recorderDeviceReport.setProvinceCode(DeviceNorthIntfConstants.BEIJING_PROVINCE);
                recorderDeviceReport.setCityCode(DeviceNorthIntfConstants.BEIJING_CITY);
                recorderDeviceReport.setCountyCode(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
            } else {
                recorderDeviceReport.setProvinceCode(region.getProvinceCode());
                if (!StringUtils.isEmpty(region.getCityCode())) {
                    recorderDeviceReport.setCityCode(region.getCityCode());
                }
                if (!StringUtils.isEmpty(region.getCountyCode())) {
                    recorderDeviceReport.setCountyCode(region.getCountyCode());
                }
                if (!StringUtils.isEmpty(region.getTownCode())) {
                    recorderDeviceReport.setTownCode(region.getTownCode());
                }
            }
        } else {
            recorderDeviceReport.setProvinceCode(DeviceNorthIntfConstants.BEIJING_PROVINCE);
            recorderDeviceReport.setCityCode(DeviceNorthIntfConstants.BEIJING_CITY);
            recorderDeviceReport.setCountyCode(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
        }
    }

}
