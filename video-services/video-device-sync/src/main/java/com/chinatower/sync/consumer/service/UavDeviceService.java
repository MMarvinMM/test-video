package com.chinatower.sync.consumer.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.vo.UavNestLocationVO;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.sync.consumer.dao.SensorStatusDAO;
import com.chinatower.sync.consumer.dao.UavCameraLensMapper;
import com.chinatower.sync.consumer.dao.UavCameraModelMapper;
import com.chinatower.sync.consumer.dao.UavDeviceDAO;
import com.chinatower.sync.consumer.dao.UavMountModelMapper;
import com.chinatower.sync.consumer.dao.UavNestDAO;
import com.chinatower.sync.consumer.entity.ChannelStatusReq;
import com.chinatower.sync.consumer.item.CanalBean;
import com.chinatower.sync.consumer.item.CommonYgReport;
import com.chinatower.sync.consumer.item.Constants;
import com.chinatower.sync.consumer.item.DeviceAppModVO;
import com.chinatower.sync.consumer.item.UavCameraModelReport;
import com.chinatower.sync.consumer.item.UavCameraParamReport;
import com.chinatower.sync.consumer.item.UavDeviceReport;
import com.chinatower.sync.consumer.item.UavDeviceStatusReport;
import com.chinatower.sync.consumer.item.UavModelReport;
import com.chinatower.sync.consumer.item.UavMountModelRelReport;
import com.chinatower.sync.consumer.item.UavMountModelReport;
import com.chinatower.sync.consumer.item.UavMountModelVO;
import com.chinatower.sync.consumer.item.UavNestChannelReport;
import com.chinatower.sync.consumer.item.UavNestDeviceReport;
import com.chinatower.sync.consumer.item.UavNestModelReport;
import com.chinatower.sync.consumer.util.DeviceCommonUtils;
import com.chinatower.video.api.ICasCadeInfoService;
import com.chinatower.video.api.entity.VideoConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UavDeviceService {

    @Resource
    private UavDeviceDAO uavDeviceDAO;

    @Resource
    private UavNestDAO uavNestDAO;

    @Autowired
    private SensorStatusService sensorStatusService;

    @Resource
    private NetConnectUtils netConnectUtils;

    @Autowired
    private UavMountModelMapper uavMountModelMapper;

    @Autowired
    private UavCameraModelMapper uavCameraModelMapper;

    @Autowired
    private UavCameraLensMapper uavCameraLensMapper;

    @Autowired
    private SensorStatusDAO sensorStatusDAO;

    @Autowired
    private ICasCadeInfoService iCasCadeInfoService;

    @Autowired
    private DeviceCommonUtils deviceCommonUtils;

    @Transactional(propagation = Propagation.REQUIRED)
    public void dealUavDeviceMsgNew(List<String> messages) {
        log.info("无人机设备消费信息:{}, size:{}", messages, messages.size());
        List<UavDeviceReport> dataList = Lists.newArrayList();
        for (String message : messages) {
            CanalBean canalBeans = JSON.parseObject(message, CanalBean.class);
            List<UavDeviceReport> subDataList = canalBeans.getData().stream().map(x -> {
                UavDeviceReport data = JSON.parseObject(JSON.toJSONString(x), UavDeviceReport.class);
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
    public void dealUavNestDeviceMsg(List<String> messages) {
        log.info("无人机机巢设备消费信息:{}, size:{}", messages, messages.size());
        List<UavNestDeviceReport> dataList = Lists.newArrayList();
        for (String message : messages) {
            CanalBean canalBeans = JSON.parseObject(message, CanalBean.class);
            List<UavNestDeviceReport> subDataList = canalBeans.getData().stream().map(x -> {
                UavNestDeviceReport data = JSON.parseObject(JSON.toJSONString(x), UavNestDeviceReport.class);
                data.setCanalType(canalBeans.getType());
                data.setTs(canalBeans.getTs());
                return data;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(subDataList)) {
                dataList.addAll(subDataList);
            }
        }
        this.dealUavNestDeviceDataInfo(dataList);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void dealUavMountModelMsg(List<String> messages) {
        log.info("无人机挂载型号消费信息:{}, size:{}", messages, messages.size());
        List<UavMountModelReport> dataList = commonAssembleDataList(messages, UavMountModelReport.class);
        this.dealUavMountModelDataInfo(dataList);
    }

    /**
     * 无人机挂载型号关联表
     * @param messages
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void dealUavMountModelRelMsg(List<String> messages) {
        log.info("无人机关联挂载型号关系表消费信息:{}, size:{}", messages, messages.size());
        List<UavMountModelRelReport> dataList = commonAssembleDataList(messages, UavMountModelRelReport.class);
        this.dealUavMountModelRelDataInfo(dataList);


    }

    public void dealUavMountModelRelDataInfo(List<UavMountModelRelReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 根据id取最新的一条
        Map<Long, UavMountModelRelReport> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        UavMountModelRelReport::getId, // keyMapper: 从Record中提取code作为Map的键
                        Function.identity(), // valueMapper: Record对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getTs() >= oldRecord.getTs()) ? newRecord : oldRecord // mergeFunction: 冲突时选择时间戳最新的Record
                ));
        dataList = Lists.newArrayList(dataMap.values());
        // 挂载型号新增列表
        List<UavMountModelRelReport> insertList = Lists.newArrayList();
        // 挂载型号更新列表
        List<UavMountModelRelReport> updateList = Lists.newArrayList();

        List<Long> ygIdList = dataList.stream().map(UavMountModelRelReport::getId).distinct().collect(Collectors.toList());
        List<Long> existYgIdList = uavDeviceDAO.queryUavMountRelYgIdList(ygIdList);

        Long ygId;
        for (UavMountModelRelReport uavMountModelReport : dataList) {
            ygId = uavMountModelReport.getId();
            if (ygId != null) {
                //运管删除该设备，应用平台同步更改状态为不可用
                if (Constants.DELETE.equals(uavMountModelReport.getCanalType())) {
                    //补充自己库表属性值
                    uavMountModelReport.setDelFlag(Constants.DELETEFLAG_1);
                    updateList.add(uavMountModelReport);
                }
                else {
                    uavMountModelReport.setDelFlag(Constants.DELETEFLAG_0);
                    if (existYgIdList.contains(ygId)) {
                        updateList.add(uavMountModelReport);
                    }
                    else {
                        insertList.add(uavMountModelReport);
                    }
                }
            }
        }
        // 批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            uavDeviceDAO.insertMountModelRelBatch(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            uavDeviceDAO.updateMountModelRelBatch(updateList);
        }
    }



    public void dealUavMountModelDataInfo(List<UavMountModelReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 根据deviceCode取最新的一条
        Map<Long, UavMountModelReport> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        UavMountModelReport::getId, // keyMapper: 从Record中提取code作为Map的键
                        Function.identity(), // valueMapper: Record对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getTs() >= oldRecord.getTs()) ? newRecord : oldRecord // mergeFunction: 冲突时选择时间戳最新的Record
                ));
        dataList = Lists.newArrayList(dataMap.values());

        // 挂载型号新增列表
        List<UavMountModelReport> insertList = Lists.newArrayList();
        // 挂载型号更新列表
        List<UavMountModelReport> updateList = Lists.newArrayList();

        List<Long> ygIdList = dataList.stream().map(UavMountModelReport::getId).distinct().collect(Collectors.toList());
        List<Long> existYgIdList = uavDeviceDAO.queryUavMountYgIdList(ygIdList);

        Long ygId;
        for (UavMountModelReport uavMountModelReport : dataList) {
            ygId = uavMountModelReport.getId();
            if (ygId != null) {
                //运管删除该设备，应用平台同步更改状态为不可用
                if (Constants.DELETE.equals(uavMountModelReport.getCanalType())) {
                    //补充自己库表属性值
                    uavMountModelReport.setDelFlag(Constants.DELETEFLAG_1);
                    updateList.add(uavMountModelReport);
                }
                else {
                    if (existYgIdList.contains(ygId)) {
                        updateList.add(uavMountModelReport);
                    }
                    else {
                        insertList.add(uavMountModelReport);
                    }
                }
            }
        }

        // 批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            uavDeviceDAO.insertMountModelBatch(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            uavDeviceDAO.updateMountModelBatch(updateList);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void dealUavNestChannelMsg(List<String> messages) {
        log.info("无人机机巢通道消费信息:{}, size:{}", messages, messages.size());
        List<UavNestChannelReport> dataList = Lists.newArrayList();
        for (String message : messages) {
            CanalBean canalBeans = JSON.parseObject(message, CanalBean.class);
            List<UavNestChannelReport> subDataList = canalBeans.getData().stream().map(x -> {
                UavNestChannelReport data = JSON.parseObject(JSON.toJSONString(x), UavNestChannelReport.class);
                data.setCanalType(canalBeans.getType());
                data.setTs(canalBeans.getTs());
                return data;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(subDataList)) {
                dataList.addAll(subDataList);
            }
        }
        this.dealUavNestChannelDataInfo(dataList);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void dealUavNestModelMsg(List<String> messages) {
        log.info("无人机机巢型号消费信息:{}, size:{}", messages, messages.size());
        List<UavNestModelReport> dataList = this.commonAssembleDataList(messages, UavNestModelReport.class);
        this.dealUavNestModelDataInfo(dataList);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void dealUavModelMsg(List<String> messages) {
        log.info("无人机型号消费信息:{}, size:{}", messages, messages.size());
        List<UavModelReport> dataList = this.commonAssembleDataList(messages, UavModelReport.class);
        this.dealUavModelDataInfo(dataList);
    }

    private <T extends CommonYgReport> List<T> commonAssembleDataList(List<String> messages, Class<T> tType) {
        List<T> dataList = Lists.newArrayList();
        for (String message : messages) {
            CanalBean canalBeans = JSON.parseObject(message, CanalBean.class);
            if (Objects.nonNull(canalBeans.getData())) {
                List<T> subDataList = canalBeans.getData().stream().map(x -> {
                    T data = JSON.parseObject(JSON.toJSONString(x), tType);
                    data.setCanalType(canalBeans.getType());
                    data.setTs(canalBeans.getTs());
                    return data;
                }).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(subDataList)) {
                    dataList.addAll(subDataList);
                }
            }
        }
        return dataList;
    }

    public void dealDeviceDataInfoNew(List<UavDeviceReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 根据deviceCode取最新的一条
        Map<String, UavDeviceReport> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        UavDeviceReport::getDeviceCode, // keyMapper: 从Record中提取code作为Map的键
                        Function.identity(), // valueMapper: Record对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getTs() >= oldRecord.getTs()) ? newRecord : oldRecord // mergeFunction: 冲突时选择时间戳最新的Record
                ));
        dataList = Lists.newArrayList(dataMap.values());

        List<UavDeviceReport> insertList = Lists.newArrayList();
        List<UavDeviceReport> updateList = Lists.newArrayList();

        List<String> deviceCodeList = dataList.stream().map(UavDeviceReport::getDeviceCode).distinct().collect(Collectors.toList());
        List<DeviceAppModVO> deviceAppModList = uavDeviceDAO.queryDeviceAppModInfo(deviceCodeList);
        // key -> deviceCode value -> appModFlag
        Map<String, Integer> deviceAppModMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(deviceAppModList)) {
            deviceAppModMap = deviceAppModList.stream().collect(
                    Collectors.toMap(DeviceAppModVO::getDeviceCode, DeviceAppModVO::getAppModFlag, (a, b) -> a));
        }

        String deviceCode;
        for (UavDeviceReport uavDeviceReport : dataList) {
            deviceCode = uavDeviceReport.getDeviceCode();
            if (!StringUtils.isEmpty(deviceCode)) {
                //运管删除该设备，应用平台同步更改状态为不可用
                if (Constants.DELETE.equals(uavDeviceReport.getCanalType())) {
                    //补充自己库表属性值
                    this.setUavDeviceReportData(uavDeviceReport);
                    uavDeviceReport.setDelFlag(Constants.DELETEFLAG_1);
                    updateList.add(uavDeviceReport);
                }
                else {
                    if (deviceAppModMap.containsKey(deviceCode)) {
                        // 获取无人机和机巢的绑定关系，如果绑定信息和缓存不一致，更新缓存
                        String uavNestDeviceCode = uavDeviceReport.getBindNestDeviceCode();
                        if (StringUtils.isNotBlank(uavNestDeviceCode)) {
                            String cacheKey = String.format("UAV_NEST_BIND_%s", uavNestDeviceCode);
                            String uavDeviceCode = RedisUtil.get(cacheKey, String.class, RedisDBType.PRIMARY);
                            if (!StringUtils.equals(uavDeviceCode, deviceCode)) {
                                RedisUtil.set(cacheKey, deviceCode, 10, TimeUnit.MINUTES, RedisDBType.PRIMARY);
                            }
                        }

                        //补充自己库表属性值
                        this.setUavDeviceReportData(uavDeviceReport);
                        updateList.add(uavDeviceReport);
                    }
                    else {
                        //补充自己库表属性值
                        this.setUavDeviceReportData(uavDeviceReport);
                        insertList.add(uavDeviceReport);
                    }
                }
            }
        }

        // 批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            uavDeviceDAO.insertDeviceBatch(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            uavDeviceDAO.updateDeviceBatch(updateList);
        }
        // 批量更新mod
        /*if (CollectionUtils.isNotEmpty(updateModList)) {
            uavDeviceDAO.updateDeviceModBatch(updateModList);
        }*/
    }

    public void dealUavNestDeviceDataInfo(List<UavNestDeviceReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 根据deviceCode取最新的一条
        Map<String, UavNestDeviceReport> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        UavNestDeviceReport::getDeviceCode, // keyMapper: 从Record中提取code作为Map的键
                        Function.identity(), // valueMapper: Record对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getTs() >= oldRecord.getTs()) ? newRecord : oldRecord // mergeFunction: 冲突时选择时间戳最新的Record
                ));
        dataList = Lists.newArrayList(dataMap.values());

        List<UavNestDeviceReport> insertList = Lists.newArrayList();
        List<UavNestDeviceReport> updateList = Lists.newArrayList();

        List<String> deviceCodeList = dataList.stream().map(UavNestDeviceReport::getDeviceCode).distinct().collect(Collectors.toList());
        List<DeviceAppModVO> deviceAppModList = uavNestDAO.queryDeviceAppModInfo(deviceCodeList);
        // key -> deviceCode value -> data
        Map<String, DeviceAppModVO> deviceAppModMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(deviceAppModList)) {
            deviceAppModMap = deviceAppModList.stream().collect(
                    Collectors.toMap(DeviceAppModVO::getDeviceCode, (p) -> p, (a, b) -> a));
        }

        Date now = new Date();

        String deviceCode;
        for (UavNestDeviceReport uavDeviceReport : dataList) {
            deviceCode = uavDeviceReport.getDeviceCode();
            if (!StringUtils.isEmpty(deviceCode)) {
                //运管删除该设备，应用平台同步更改状态为不可用
                if (Constants.DELETE.equals(uavDeviceReport.getCanalType())) {
                    this.setUavNestDeviceReportData(uavDeviceReport);
                    //补充自己库表属性值
                    uavDeviceReport.setDelFlag(Constants.DELETEFLAG_1);
                    updateList.add(uavDeviceReport);
                }
                else {
                    DeviceAppModVO existInfo = deviceAppModMap.get(deviceCode);
                    if (existInfo != null) {
                        this.setUavNestDeviceReportData(uavDeviceReport);
                        // 更新经纬度时间
                        if (Objects.equals(uavDeviceReport.getPositionFix(), 0)
                                && (!StringUtils.equals(uavDeviceReport.getLongitude(), existInfo.getLongitude()) || !StringUtils.equals(uavDeviceReport.getLatitude(), existInfo.getLatitude()))) {
                            uavDeviceReport.setLatLonUpdateTime(now);
                        }
                        updateList.add(uavDeviceReport);
                    }
                    else {
                        // 初始化经纬度时间
                        if (Objects.equals(uavDeviceReport.getPositionFix(), 0)) {
                            uavDeviceReport.setLatLonUpdateTime(now);
                        }
                        this.setUavNestDeviceReportData(uavDeviceReport);
                        //补充自己库表属性值
                        insertList.add(uavDeviceReport);
                    }
                }
            }
        }

        // 批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            uavNestDAO.insertDeviceBatch(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            uavNestDAO.updateDeviceBatch(updateList);
        }
        // 批量更新mod
        /*if (CollectionUtils.isNotEmpty(updateModList)) {
            uavNestDAO.updateDeviceModBatch(updateModList);
        }*/
    }

    public void dealUavNestChannelDataInfo(List<UavNestChannelReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 根据channelCode取最新的一条
        Map<String, UavNestChannelReport> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        UavNestChannelReport::getChannelCode, // keyMapper: 从Record中提取code作为Map的键
                        Function.identity(), // valueMapper: Record对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getTs() >= oldRecord.getTs()) ? newRecord : oldRecord // mergeFunction: 冲突时选择时间戳最新的Record
                ));
        dataList = Lists.newArrayList(dataMap.values());

        List<UavNestChannelReport> insertList = Lists.newArrayList();
        List<UavNestChannelReport> updateList = Lists.newArrayList();

        List<String> channelCodeList = dataList.stream().map(UavNestChannelReport::getChannelCode).distinct().collect(Collectors.toList());
        List<String> existChannelCodeList = uavNestDAO.queryExistChannelCodeList(channelCodeList);

        log.info("dealUavNestChannelDataInfo dataList:{}, existChannelCodeList:{}", JSON.toJSONString(dataList), JSON.toJSONString(existChannelCodeList));
        for (UavNestChannelReport channelReport : dataList) {
            if (!StringUtils.isEmpty(channelReport.getDeviceCode()) && !StringUtils.isEmpty(channelReport.getChannelCode())) {
                //运管删除该设备，应用平台同步更改状态为不可用
                if (Constants.DELETE.equals(channelReport.getCanalType())) {
                    channelReport.setDelFlag(Constants.DELETEFLAG_1);
                    updateList.add(channelReport);
                }
                else {
                    if (existChannelCodeList.contains(channelReport.getChannelCode())) {
                        updateList.add(channelReport);
                    }
                    else {
                        insertList.add(channelReport);
                    }
                }
            }
        }
        // 批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            uavNestDAO.insertDeviceChannelBatch(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            uavNestDAO.updateDeviceChannelBatch(updateList);
        }
    }

    public void dealUavNestModelDataInfo(List<UavNestModelReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 根据channelCode取最新的一条
        Map<String, UavNestModelReport> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        UavNestModelReport::getDictCode, // keyMapper: 从Record中提取code作为Map的键
                        Function.identity(), // valueMapper: Record对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getTs() >= oldRecord.getTs()) ? newRecord : oldRecord // mergeFunction: 冲突时选择时间戳最新的Record
                ));
        dataList = Lists.newArrayList(dataMap.values());

        List<UavNestModelReport> insertList = Lists.newArrayList();
        List<UavNestModelReport> updateList = Lists.newArrayList();
        List<String> deleteList = Lists.newArrayList();

        List<String> ygIdList = dataList.stream().map(UavNestModelReport::getId).distinct().collect(Collectors.toList());
        List<String> existYgIdList = uavNestDAO.queryExistUavNestYgIdList(ygIdList);
        log.info("dealUavNestModelDataInfo dataList:{}, queryExistUavNestYgIdList:{}", JSON.toJSONString(dataList), JSON.toJSONString(existYgIdList));

        for (UavNestModelReport uavNestModelReport : dataList) {
            if (Constants.DELETE.equals(uavNestModelReport.getCanalType())) {
                deleteList.add(uavNestModelReport.getId());
            }
            else {
                if (existYgIdList.contains(uavNestModelReport.getId())) {
                    updateList.add(uavNestModelReport);
                }
                else {
                    insertList.add(uavNestModelReport);
                }
            }
        }
        // 批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            uavNestDAO.insertUavNestModelBatch(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            uavNestDAO.updateUavNestModelBatch(updateList);
        }
        // 批量删除
        if (CollectionUtils.isNotEmpty(deleteList)) {
            log.info("需要删除的机巢型号编码集合：{}", JSON.toJSONString(deleteList));
            uavNestDAO.deleteUavNestModelBatch(deleteList);
        }
    }

    public void dealUavModelDataInfo(List<UavModelReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 根据channelCode取最新的一条
        Map<String, UavModelReport> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        UavModelReport::getDictCode, // keyMapper: 从Record中提取code作为Map的键
                        Function.identity(), // valueMapper: Record对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getTs() >= oldRecord.getTs()) ? newRecord : oldRecord // mergeFunction: 冲突时选择时间戳最新的Record
                ));
        dataList = Lists.newArrayList(dataMap.values());

        List<UavModelReport> insertList = Lists.newArrayList();
        List<UavModelReport> updateList = Lists.newArrayList();
        List<String> deleteList = Lists.newArrayList();

        //型号编码筛选
        List<String> ygIdList = dataList.stream().map(UavModelReport::getId).distinct().collect(Collectors.toList());
        List<String> existYgIdList = uavDeviceDAO.queryExistUavYgIdList(ygIdList);
        log.info("dealUavModelDataInfo dataList:{}, queryExistUavYgIdList:{}", JSON.toJSONString(dataList), JSON.toJSONString(existYgIdList));

        for (UavModelReport uavModelReport : dataList) {
            if (Constants.DELETE.equals(uavModelReport.getCanalType())) {
                deleteList.add(uavModelReport.getId());
            }
            else {
                if (existYgIdList.contains(uavModelReport.getId())) {
                    updateList.add(uavModelReport);
                }
                else {
                    insertList.add(uavModelReport);
                }
            }
        }
        // 批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            uavDeviceDAO.insertUavModelBatch(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            uavDeviceDAO.updateUavModelBatch(updateList);
        }
        // 批量删除
        if (CollectionUtils.isNotEmpty(deleteList)) {
            log.info("需要删除的无人机型号编码集合：{}", JSON.toJSONString(deleteList));
            uavDeviceDAO.deleteUavModelBatch(deleteList);
        }
    }

    //根据经纬度 补全 省市区乡镇编码
    private void setUavDeviceReportData(UavDeviceReport uavDeviceReport) {
        if (StringUtils.isEmpty(uavDeviceReport.getLatitude()) || StringUtils.isEmpty(uavDeviceReport.getLongitude())) {
            return;
        }
        PoiContraryGeocodingQueryRes regionInfo = deviceCommonUtils.getRegionInfoByLonAndLat(uavDeviceReport.getLongitude(), uavDeviceReport.getLatitude());
        if (regionInfo != null && regionInfo.getRegion() != null) {
            if (StringUtils.isEmpty(regionInfo.getRegion().getProvinceCode())) {
                uavDeviceReport.setProvinceCode(DeviceNorthIntfConstants.BEIJING_PROVINCE);
                uavDeviceReport.setCityCode(DeviceNorthIntfConstants.BEIJING_CITY);
                uavDeviceReport.setCountyCode(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
            } else {
                uavDeviceReport.setProvinceCode(regionInfo.getRegion().getProvinceCode());
                if (!StringUtils.isEmpty(regionInfo.getRegion().getCityCode())) {
                    uavDeviceReport.setCityCode(regionInfo.getRegion().getCityCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getRegion().getCountyCode())) {
                    uavDeviceReport.setCountyCode(regionInfo.getRegion().getCountyCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getRegion().getTownCode())) {
                    uavDeviceReport.setTownCode(regionInfo.getRegion().getTownCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getAddress())) {
                    uavDeviceReport.setAddress(regionInfo.getAddress());
                }
            }
        } else {
            uavDeviceReport.setProvinceCode(DeviceNorthIntfConstants.BEIJING_PROVINCE);
            uavDeviceReport.setCityCode(DeviceNorthIntfConstants.BEIJING_CITY);
            uavDeviceReport.setCountyCode(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void dealUavCameraModelMsg(List<String> messages) {
        log.info("无人机相机型号消费信息:{}, size:{}", messages, messages.size());
        List<com.chinatower.sync.consumer.item.UavCameraModelReport> dataList = commonAssembleDataList(messages, com.chinatower.sync.consumer.item.UavCameraModelReport.class);
        this.dealUavCameraModelDataInfo(dataList);

    }

    public void dealUavCameraModelDataInfo(List<com.chinatower.sync.consumer.item.UavCameraModelReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 根据id取最新的一条
        Map<Long, com.chinatower.sync.consumer.item.UavCameraModelReport> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        com.chinatower.sync.consumer.item.UavCameraModelReport::getId, // keyMapper: 从Record中提取id作为Map的键
                        Function.identity(), // valueMapper: Record对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getTs() >= oldRecord.getTs()) ? newRecord : oldRecord // mergeFunction: 冲突时选择时间戳最新的Record
                ));
        dataList = Lists.newArrayList(dataMap.values());
        // 相机型号新增列表
        List<com.chinatower.sync.consumer.item.UavCameraModelReport> insertList = Lists.newArrayList();
        // 相机型号更新列表
        List<com.chinatower.sync.consumer.item.UavCameraModelReport> updateList = Lists.newArrayList();

        List<Long> modelIdList = dataList.stream().map(com.chinatower.sync.consumer.item.UavCameraModelReport::getId).distinct().collect(Collectors.toList());
        List<Long> existModelYgIdList = uavDeviceDAO.queryUavCameraModelYgIdList(modelIdList);
        Long ygId;
        for (com.chinatower.sync.consumer.item.UavCameraModelReport uavCameraModelReport : dataList) {
            ygId = uavCameraModelReport.getId();
            if (ygId != 0) {
                //运管删除该设备，应用平台同步更改状态为不可用
                if (Constants.DELETE.equals(uavCameraModelReport.getCanalType())) {
                    //补充自己库表属性值
                    uavCameraModelReport.setDelFlag(Constants.DELETEFLAG_1);
                    updateList.add(uavCameraModelReport);
                }
                else {
                    if (existModelYgIdList.contains(ygId)) {
                        updateList.add(uavCameraModelReport);
                    }
                    else {
                        insertList.add(uavCameraModelReport);
                    }
                }
            }
        }

        // 批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            uavDeviceDAO.insertCameraModelBatch(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            uavDeviceDAO.updateCameraModelBatch(updateList);
        }


    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void dealUavCameraParamMsg(List<String> messages) {
        log.info("无人机相机参数消费信息:{}, size:{}", messages, messages.size());
        List<UavCameraParamReport> dataList = commonAssembleDataList(messages, UavCameraParamReport.class);
        this.dealUavCameraParamDataInfo(dataList);
    }


    private void dealUavCameraParamDataInfo(List<UavCameraParamReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 根据id取最新的一条
        Map<Long, UavCameraParamReport> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        UavCameraParamReport::getId, // keyMapper: 从Record中提取id作为Map的键
                        Function.identity() // valueMapper: Record对象本身作为值
                ));
        dataList = Lists.newArrayList(dataMap.values());
        // 相机型号参数新增列表
        List<UavCameraParamReport> insertList = Lists.newArrayList();
        List<UavCameraParamReport> updateList = Lists.newArrayList();
        List<Long> deleteIdList = Lists.newArrayList();
        List<Long> allIdList = dataList.stream().map(UavCameraParamReport::getId).collect(Collectors.toList());
        List<Long> existIdList = uavDeviceDAO.queryUavCameraParamYgIdList(allIdList);

        Long ygId;
        for (com.chinatower.sync.consumer.item.UavCameraParamReport uavCameraModelReport : dataList) {
            ygId = uavCameraModelReport.getId();
            if (ygId != 0) {
                //运管删除该设备，应用平台同步更改状态为不可用
                if (Constants.DELETE.equals(uavCameraModelReport.getCanalType())) {
                    deleteIdList.add(ygId);
                }
                else {
                    if (existIdList.contains(ygId)) {
                        updateList.add(uavCameraModelReport);
                    }
                    else {
                        insertList.add(uavCameraModelReport);
                    }
                }
            }
        }

        // 批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            uavDeviceDAO.insertCameraParamBatch(insertList);
        }

        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            uavDeviceDAO.updateCameraParamBatch(updateList);
        }

        // 批量删除原来的镜头参数
        if (CollectionUtils.isNotEmpty(deleteIdList)) {
            uavDeviceDAO.deleteUavCameraParamById(deleteIdList);
        }

    }

    /**
     * TODO 用于测试的测试数据，上线删掉
     *
     * @return
     */
    public List<UavMountModelReport> getTestData() {
        List<String> jsonStrings = new ArrayList<>(Arrays.asList(
                "{\"id\":\"1\",\"dictCode\":\"modelCode1\",\"dictLabel\":\"modelName_71ab542ccfa8\",\"manufacturer\":\"manufacturerName_a665ec1d8a9e\",\"manufacturerCode\":\"manufacturerCode_24d09b4aea6b\",\"mountType\":\"dd\",\"supportFunction\":\"supportFunction_bd3ca254d8ab\",\"createBy\":\"\",\"createTime\":\"\",\"updateBy\":\"\",\"updateTime\":\"\",\"delFlag\":\"0\"}",
                "{\"id\":\"2\",\"dictCode\":\"modelCode2\",\"dictLabel\":\"modelName_71ab542ccfa8\",\"manufacturer\":\"manufacturerName_a665ec1d8a9e\",\"manufacturerCode\":\"manufacturerCode_24d09b4aea6b\",\"mountType\":\"dd\",\"supportFunction\":\"supportFunction_bd3ca254d8ab\",\"createBy\":\"\",\"createTime\":\"\",\"updateBy\":\"\",\"updateTime\":\"\",\"delFlag\":\"0\"}",
                "{\"id\":\"3\",\"dictCode\":\"modelCode3\",\"dictLabel\":\"modelName_71ab542ccfa8\",\"manufacturer\":\"manufacturerName_a665ec1d8a9e\",\"manufacturerCode\":\"manufacturerCode_24d09b4aea6b\",\"mountType\":\"dd\",\"supportFunction\":\"supportFunction_bd3ca254d8ab\",\"createBy\":\"\",\"createTime\":\"\",\"updateBy\":\"\",\"updateTime\":\"\",\"delFlag\":\"0\"}",
                "{\"id\":\"4\",\"dictCode\":\"modelCode4\",\"dictLabel\":\"modelName_71ab542ccfa8\",\"manufacturer\":\"manufacturerName_a665ec1d8a9e\",\"manufacturerCode\":\"manufacturerCode_24d09b4aea6b\",\"mountType\":\"dd\",\"supportFunction\":\"supportFunction_bd3ca254d8ab\",\"createBy\":\"\",\"createTime\":\"\",\"updateBy\":\"\",\"updateTime\":\"\",\"delFlag\":\"0\"}",
                "{\"id\":\"7\",\"dictCode\":\"modelCode7\",\"dictLabel\":\"modelName_71ab542ccfa8\",\"manufacturer\":\"manufacturerName_a665ec1d8a9e\",\"manufacturerCode\":\"manufacturerCode_24d09b4aea6b\",\"mountType\":\"dd\",\"supportFunction\":\"supportFunction_bd3ca254d8ab\",\"createBy\":\"\",\"createTime\":\"\",\"updateBy\":\"\",\"updateTime\":\"\",\"delFlag\":\"0\"}",
                "{\"id\":\"8\",\"dictCode\":\"modelCode8\",\"dictLabel\":\"modelName_71ab542ccfa8\",\"manufacturer\":\"manufacturerName_a665ec1d8a9e\",\"manufacturerCode\":\"manufacturerCode_24d09b4aea6b\",\"mountType\":\"dd\",\"supportFunction\":\"supportFunction_bd3ca254d8ab\",\"createBy\":\"\",\"createTime\":\"\",\"updateBy\":\"\",\"updateTime\":\"\",\"delFlag\":\"0\"}",
                "{\"id\":\"28\",\"dictCode\":\"modelCode9\",\"dictLabel\":\"modelName_71ab542ccfa8\",\"manufacturer\":\"manufacturerName_a665ec1d8a9e\",\"manufacturerCode\":\"manufacturerCode_24d09b4aea6b\",\"mountType\":\"dd\",\"supportFunction\":\"supportFunction_bd3ca254d8ab\",\"createBy\":\"\",\"createTime\":\"\",\"updateBy\":\"\",\"updateTime\":\"\",\"delFlag\":\"0\"}",
                "{\"id\":\"29\",\"dictCode\":\"modelCode10\",\"dictLabel\":\"modelName_71ab542ccfa8\",\"manufacturer\":\"manufacturerName_a665ec1d8a9e\",\"manufacturerCode\":\"manufacturerCode_24d09b4aea6b\",\"mountType\":\"dd\",\"supportFunction\":\"supportFunction_bd3ca254d8ab\",\"createBy\":\"\",\"createTime\":\"\",\"updateBy\":\"\",\"updateTime\":\"\",\"delFlag\":\"0\"}",
                "{\"id\":\"30\",\"dictCode\":\"modelCode11\",\"dictLabel\":\"modelName_71ab542ccfa8\",\"manufacturer\":\"manufacturerName_a665ec1d8a9e\",\"manufacturerCode\":\"manufacturerCode_24d09b4aea6b\",\"mountType\":\"dd\",\"supportFunction\":\"supportFunction_bd3ca254d8ab\",\"createBy\":\"\",\"createTime\":\"\",\"updateBy\":\"\",\"updateTime\":\"\",\"delFlag\":\"0\"}",
                "{\"id\":\"31\",\"dictCode\":\"modelCode12\",\"dictLabel\":\"modelName_71ab542ccfa8\",\"manufacturer\":\"manufacturerName_a665ec1d8a9e\",\"manufacturerCode\":\"manufacturerCode_24d09b4aea6b\",\"mountType\":\"dd\",\"supportFunction\":\"supportFunction_bd3ca254d8ab\",\"createBy\":\"\",\"createTime\":\"\",\"updateBy\":\"\",\"updateTime\":\"\",\"delFlag\":\"0\"}"
        ));

        ObjectMapper objectMapper = new ObjectMapper();
        List<UavMountModelReport> result = new ArrayList<>();

        try {
            for (String json : jsonStrings) {
                UavMountModelReport vo = objectMapper.readValue(json, UavMountModelReport.class);
                result.add(vo);
            }
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void syncAllUavMountModel() {

        // 其他地方同步过来的数据
        //List<UavMountModelReport> uavMountModelReportList = getTestData();
        List<UavMountModelReport> uavMountModelReportList = getAllModelData(NetConnectUtils.RequestParamter.URLMappingEnum.ALL_UAV_MOUNT_MODEL, "无人机挂载型号", UavMountModelReport.class);

        // 获取数据库中所有的设备数据
        List<UavMountModelVO> uavMountModelList = uavMountModelMapper.getAllUavMountModel();

        // 将两个列表转换为以ygId为key的Map，便于比对
        Map<Long, UavMountModelReport> reportMap = uavMountModelReportList.stream()
                .collect(Collectors.toMap(UavMountModelReport::getId, Function.identity()));
        Map<Long, UavMountModelVO> dbMap = uavMountModelList.stream()
                .collect(Collectors.toMap(UavMountModelVO::getYgId, Function.identity()));

        // 需要新增的数据,
        List<UavMountModelReport> toInsertList = uavMountModelReportList.stream()
                .filter(report -> !dbMap.containsKey(report.getId()))
                .collect(Collectors.toList());

        // 需要删除的数据
        List<Long> toDeleteList = uavMountModelList.stream()
                .filter(db -> !reportMap.containsKey(db.getYgId()))
                .map(UavMountModelVO::getYgId)
                .collect(Collectors.toList());

        // 需要更新的数据
        List<UavMountModelReport> toUpdateList = uavMountModelReportList.stream()
                .filter(report -> dbMap.containsKey(report.getId()))
                .collect(Collectors.toList());

        if (!toDeleteList.isEmpty()) {
            batchDeleteUavMountModel(toDeleteList);
        }

        // 批量处理数据
        if (!toInsertList.isEmpty()) {
//            batchInsertUavMountModel(toInsertList);
            toInsertList.forEach(list -> list.setCreateTime(new Date()));
            toInsertList.forEach(list -> list.setDelFlag("0"));
            uavDeviceDAO.insertMountModelBatch(toInsertList);
        }

        if (!toUpdateList.isEmpty()) {
//            batchUpdateUavMountModel(toUpdateList);
            toUpdateList.forEach(list -> list.setUpdateTime(new Date()));
            toUpdateList.forEach(list -> list.setDelFlag("0"));
            uavDeviceDAO.updateMountModelBatch(toUpdateList);
        }
    }

    // 批量插入方法
    private void batchInsertUavMountModel(List<UavMountModelVO> insertList) {
        if (CollectionUtils.isEmpty(insertList)) {
            return;
        }
        uavMountModelMapper.batchInsert(insertList);
    }

    // 批量删除方法
    private void batchDeleteUavMountModel(List<Long> deleteList) {
        if (CollectionUtils.isEmpty(deleteList)) {
            return;
        }
        uavMountModelMapper.batchDelete(deleteList);
    }

    // 批量更新方法
    private void batchUpdateUavMountModel(List<UavMountModelVO> updateList) {
        if (CollectionUtils.isEmpty(updateList)) {
            return;
        }
        uavMountModelMapper.batchUpdate(updateList);
    }

    @Transactional
    public void syncAllUavCameraModel() {
        List<UavCameraModelReport> dataList = getAllModelData(NetConnectUtils.RequestParamter.URLMappingEnum.ALL_UAV_CAMERA_MODEL, "无人机相机型号", UavCameraModelReport.class);
        // List<UavCameraModelReport> dataList = getDataList();
        List<Long> modelIdList = dataList.stream().map(UavCameraModelReport::getId).collect(Collectors.toList());
        List<Long> existCameraYgIdList = uavCameraModelMapper.getAllUavCameraModelYgId();
        // 运管有通用没有
        List<Long> insertIdList = modelIdList.stream().filter(x -> !existCameraYgIdList.contains(x)).collect(Collectors.toList());
        List<UavCameraModelReport> insertList = dataList.stream().filter(x -> insertIdList.contains(x.getId())).collect(Collectors.toList());
        // 运管没有通用有
        List<Long> deleteIdList = existCameraYgIdList.stream().filter(x -> !modelIdList.contains(x)).collect(Collectors.toList());
        // 运管通用都有
        List<Long> updateIdList = modelIdList.stream().filter(existCameraYgIdList::contains).collect(Collectors.toList());
        List<UavCameraModelReport> updateList = dataList.stream().filter(x -> updateIdList.contains(x.getId())).collect(Collectors.toList());
        log.info("modelIdList:{}；existCameraYgIdList:{}", modelIdList, existCameraYgIdList);
        if (CollectionUtils.isNotEmpty(insertList)) {
            log.info("新增的相机型号列表为：{}", insertList);
            // 批量新增相机型号
            uavCameraModelMapper.insertUavCameraModelBatch(insertList);
        }
        if (CollectionUtils.isNotEmpty(deleteIdList)) {
            log.info("删除的相机型号Id列表为：{}", deleteIdList);
            // 根据运管id批量删除相机型号
            uavCameraModelMapper.deleteUavCameraModelBatch(deleteIdList);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            // 批量更新相机型号
            log.info("更新的相机型号列表为：{}", updateList);
            uavCameraModelMapper.updateUavCameraModelBatch(updateList);
        }
        // 批量删除原来的镜头参数
        uavCameraLensMapper.deleteUavCameraLensBatch();
        // 批量插入新的镜头参数
        try {
            List<UavCameraParamReport> lenList = dataList.stream()
                    .flatMap(report -> report.getDeviceUavCameraLensDictDataList().stream())
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(lenList)) {
                log.info("新的镜头参数列表为：{}", lenList);
                uavCameraLensMapper.insertUavCameraLensBatch(lenList);
            }
        } catch (Exception e) {
            // 记录错误信息
            log.error("插入镜头参数失败: {}", e.getMessage(), e);
            // 重新抛出异常以确保事务回滚
            throw new RuntimeException("插入镜头参数失败", e);
        }

    }

    public List<UavCameraModelReport> getDataList() {
        List<String> jsonStrings = Arrays.asList(
                "{\"dictCode\": \"dictCode1\", \"dictLabel\": \"dictLabel_71ab542ccfa8\", \"manufacturer\": \"大疆\", \"manufacturerCode\": \"dj\", \"domain\": \"0\", \"type\": \"0\", \"subType\": \"0\", \"rangeOfPan\": \"-90~30\", \"rangeOfYaw\": \"0~10\", \"supportFunction\": null, \"delFlag\": 0, \"lenList\": [{\"deviceUavCameraDictCode\": \"dictCode1\", \"lensName\": \"可见光\", \"spectralType\": \"可见光\", \"sensorSize\": \"13.2x8.8\", \"pixelSize\": \"2.71x2.41\", \"focalLengthType\": \"定焦\", \"zoomConversionCoefficient\": \"2.73\", \"actualFocalLengthRange\": \"0~8.8\", \"equivalentFocalLengthRange\": \"0~24\", \"opticalZoomRange\": null, \"digitalZoomRange\": null, \"colorPaletteStyle\": \"白热,熔岩,铁红,热铁,医疗,北极,彩虹1,彩虹2,描红,黑热\", \"supportingFeature\": \"红外测温,红外分屏\"}]}",
                "{\"dictCode\": \"dictCode2\", \"dictLabel\": \"dictLabel_71ab542ccfa8\", \"manufacturer\": \"大疆\", \"manufacturerCode\": \"dj\", \"domain\": \"1\", \"type\": \"1\", \"subType\": \"0\", \"rangeOfPan\": \"-60~60\", \"rangeOfYaw\": \"0~5\", \"supportFunction\": null, \"delFlag\": 0, \"lenList\": [{\"deviceUavCameraDictCode\": \"dictCode2\", \"lensName\": \"红外光\", \"spectralType\": \"红外光\", \"sensorSize\": \"12.6x4.7\", \"pixelSize\": \"1.21x2.24\", \"focalLengthType\": \"变焦\", \"zoomConversionCoefficient\": \"3.12\", \"actualFocalLengthRange\": \"5.0~6.6\", \"equivalentFocalLengthRange\": \"10~15\", \"opticalZoomRange\": null, \"digitalZoomRange\": null, \"colorPaletteStyle\": \"白热,熔岩,铁红,热铁\", \"supportingFeature\": \"红外分屏\"}]}",
                "{\"dictCode\": \"dictCode3\", \"dictLabel\": \"dictLabel_71ab542ccfa8\", \"manufacturer\": \"大疆\", \"manufacturerCode\": \"dj\", \"domain\": \"2\", \"type\": \"2\", \"subType\": \"0\", \"rangeOfPan\": \"-40~20\", \"rangeOfYaw\": \"0~8\", \"supportFunction\": null, \"delFlag\": 0, \"lenList\": [{\"deviceUavCameraDictCode\": \"dictCode3\", \"lensName\": \"夜视\", \"spectralType\": \"夜视\", \"sensorSize\": \"2.9x5.5\", \"pixelSize\": \"2.11x3.35\", \"focalLengthType\": \"定焦\", \"zoomConversionCoefficient\": \"1.21\", \"actualFocalLengthRange\": \"7.1~9.3\", \"equivalentFocalLengthRange\": \"1~10\", \"opticalZoomRange\": null, \"digitalZoomRange\": null, \"colorPaletteStyle\": \"医疗,北极,彩虹1,彩虹2,描红,黑热\", \"supportingFeature\": \"红外测温\"}]}"
        );
        ObjectMapper objectMapper = new ObjectMapper();
        List<UavCameraModelReport> result = new ArrayList<>();

        try {
            for (String json : jsonStrings) {
                UavCameraModelReport vo = objectMapper.readValue(json, UavCameraModelReport.class);
                result.add(vo);
            }
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void syncAllUavModel(String param) {
        List<UavModelReport> dataList = getAllModelData(NetConnectUtils.RequestParamter.URLMappingEnum.ALL_UAV_MODEL, "无人机型号", UavModelReport.class);
        List<String> ygIdList = dataList.stream().map(UavModelReport::getId).collect(Collectors.toList());
        List<String> exsistYgIdList = uavDeviceDAO.getAllUavModelYgId();
        //运管有通用没有
        List<String> insertCodeList = ygIdList.stream().filter(x -> !exsistYgIdList.contains(x)).collect(Collectors.toList());
        List<UavModelReport> insertList = dataList.stream().filter(x -> insertCodeList.contains(x.getId())).collect(Collectors.toList());
        //运管没有通用有
        List<String> deleteCodeList = exsistYgIdList.stream().filter(x -> !ygIdList.contains(x)).collect(Collectors.toList());
        //运管通用都有
        List<String> updateCodeList = ygIdList.stream().filter(exsistYgIdList::contains).collect(Collectors.toList());
        List<UavModelReport> updateList = dataList.stream().filter(x -> updateCodeList.contains(x.getId())).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(insertList)) {
            uavDeviceDAO.insertUavModelBatch(insertList);
        }
        if (CollectionUtils.isNotEmpty(deleteCodeList)) {
            uavDeviceDAO.deleteUavModelBatch(deleteCodeList);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            uavDeviceDAO.updateUavModelBatch(updateList);
        }
    }

    public void syncAllUavNestModel(String param) {
        List<UavNestModelReport> dataList = getAllModelData(NetConnectUtils.RequestParamter.URLMappingEnum.ALL_UAV_NEST_MODEL, "无人机机巢型号", UavNestModelReport.class);
        List<String> ygIdList = dataList.stream().map(UavNestModelReport::getId).collect(Collectors.toList());
        List<String> exsistYgIdList = uavNestDAO.getAllUavNestModelYgId();
        //运管有通用没有
        List<String> insertCodeList = ygIdList.stream().filter(x -> !exsistYgIdList.contains(x)).collect(Collectors.toList());
        List<UavNestModelReport> insertList = dataList.stream().filter(x -> insertCodeList.contains(x.getId())).collect(Collectors.toList());
        //运管没有通用有
        List<String> deleteCodeList = exsistYgIdList.stream().filter(x -> !ygIdList.contains(x)).collect(Collectors.toList());
        //运管通用都有
        List<String> updateCodeList = ygIdList.stream().filter(exsistYgIdList::contains).collect(Collectors.toList());
        List<UavNestModelReport> updateList = dataList.stream().filter(x -> updateCodeList.contains(x.getId())).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(insertList)) {
            uavNestDAO.insertUavNestModelBatch(insertList);
        }
        if (CollectionUtils.isNotEmpty(deleteCodeList)) {
            uavNestDAO.deleteUavNestModelBatch(deleteCodeList);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            uavNestDAO.updateUavNestModelBatch(updateList);
        }
    }

    private <T> List<T> getAllModelData(NetConnectUtils.RequestParamter.URLMappingEnum urlMappingEnum, String modelDataType, Class<T> respType) {
        NetConnectUtils.RequestParamter<Object> body = new NetConnectUtils.RequestParamter<>();
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        path.setUrl(urlMappingEnum);
        body.setPath(path);
        body.setBody(new JSONObject());
        try {
            Result<String> platformCodeResult = iCasCadeInfoService.getCurrentPlatformCode();
            String platformCode = platformCodeResult.getData();

            ResultEntity response = netConnectUtils.post(body, ResultEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_MsgId.of("msgId"),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(platformCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(Constants.CENTER_LOCATION_CODE),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_YG),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(netConnectUtils.getRealUrl(body))
            );
            log.info("获取{}，返回报文:{}", modelDataType, response);
            if (!response.getReturnCode().equals(Constants.SUCCESS_CODE)) {
                throw new CustomException("获取" + modelDataType + "失败:" + response.getErrorMsg(), HttpStatus.ERROR_4001);
            }
            List<JSONObject> data = (List<JSONObject>) response.getData();
            List<T> respData = new ArrayList<>(data.size());
            for (JSONObject object : data) {
                T temp = JSONObject.parseObject(object.toJSONString(), respType);
                respData.add(temp);
            }
            return respData;
        }
        catch (Exception e) {
            log.info("getAllModelData 获取全量型号失败：", e);
            throw new CustomException(e.getMessage(), HttpStatus.ERROR_4001);
        }

    }

    private void setUavNestDeviceReportData(UavNestDeviceReport reportData) {
        if (StringUtils.isEmpty(reportData.getLatitude()) || StringUtils.isEmpty(reportData.getLongitude())) {
            return;
        }
        PoiContraryGeocodingQueryRes regionInfo = deviceCommonUtils.getRegionInfoByLonAndLat(reportData.getLongitude(), reportData.getLatitude());
        if (regionInfo != null) {
            if (StringUtils.isEmpty(regionInfo.getRegion().getProvinceCode())) {
                reportData.setProvinceCode(DeviceNorthIntfConstants.BEIJING_PROVINCE);
                reportData.setCityCode(DeviceNorthIntfConstants.BEIJING_CITY);
                reportData.setCountyCode(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
            } else {
                reportData.setProvinceCode(regionInfo.getRegion().getProvinceCode());
                if (!StringUtils.isEmpty(regionInfo.getRegion().getCityCode())) {
                    reportData.setCityCode(regionInfo.getRegion().getCityCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getRegion().getCountyCode())) {
                    reportData.setCountyCode(regionInfo.getRegion().getCountyCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getRegion().getTownCode())) {
                    reportData.setTownCode(regionInfo.getRegion().getTownCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getAddress())) {
                    reportData.setAddress(regionInfo.getAddress());
                }
            }
        } else {
            reportData.setProvinceCode(DeviceNorthIntfConstants.BEIJING_PROVINCE);
            reportData.setCityCode(DeviceNorthIntfConstants.BEIJING_CITY);
            reportData.setCountyCode(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void uavDeviceStatusMsg(List<String> messages) {
        SysConfig sysConfig = sensorStatusDAO.getSysConfig(Constants.DEPENDENCE_MANAGE);
        if (sysConfig != null && (Constants.ZERO.equals(sysConfig.getConfigValue())
                || Constants.TWO.equals(sysConfig.getConfigValue()))) {
            log.info("无人机上报设备状态消费信息:{}, size:{}", messages, messages.size());
            List<UavDeviceStatusReport> dataList = Lists.newArrayList();
            for (String message : messages) {
                log.info("无人机上报设备状态消费信息上报：{}  ", message);
                try {
                    UavDeviceStatusReport uavDeviceStatusReport = JSON.parseObject(message, UavDeviceStatusReport.class);
                    dataList.add(uavDeviceStatusReport);
                } catch (JSONException e) {
                    log.error("解析消息失败：{}， 错误原因：{}", message, e);
                }
            }
            this.dealLocationDeviceStatusDataInfo(dataList);
        }
    }

    public void dealLocationDeviceStatusDataInfo(List<UavDeviceStatusReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // 筛选出无人机设备
        List<UavDeviceStatusReport> uavDeviceList = dataList.stream().filter(x -> StringUtils.equals(Constants.UAV, x.getDeviceType())).collect(Collectors.toList());
        // 筛选出机巢设备
        List<UavDeviceStatusReport> uavNetDeviceList = dataList.stream().filter(x -> StringUtils.equals(Constants.NEST, x.getDeviceType())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(uavDeviceList)) {
            uavDeviceDAO.updateDeviceStatusBatch(uavDeviceList);
        }
        if (CollectionUtils.isNotEmpty(uavNetDeviceList)) {
            uavNestDAO.updateDeviceStatusBatch(uavNetDeviceList);
            // 获取筛选设备通道
            List<ChannelStatusReq> channelStatusList = uavNetDeviceList.stream()
                    .map(report -> report.getChannelInfos())
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(channelStatusList)) {
                uavNestDAO.updateDeviceChannelStatusBatch(channelStatusList);
            }
        }
        // 1.7.25如果上报状态报文中UAV的经纬度及海拔字段不为空，需要更新无人机及其机巢的位置信息
        List<UavDeviceStatusReport> uavDeviceUpdateLocationList = uavDeviceList.stream().filter(x -> StringUtils.isNotEmpty(x.getLatitude())
                && StringUtils.isNotEmpty(x.getLongitude()) && StringUtils.isNotEmpty(x.getAltitude())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(uavDeviceUpdateLocationList)) {
            List<UavNestLocationVO> locationInfoList = Lists.newArrayList();
            for (UavDeviceStatusReport one : uavDeviceUpdateLocationList) {
                UavNestLocationVO locationInfo = new UavNestLocationVO();
                PoiContraryGeocodingQueryRes regionInfo = deviceCommonUtils.getRegionInfoByLonAndLat(one.getLongitude(), one.getLatitude());
                if (Objects.isNull(regionInfo)) {
                    continue;
                }
                locationInfo.setDeviceCode(one.getDeviceCode());
                locationInfo.setAddress(regionInfo.getAddress());
                locationInfo.setProvinceCode(regionInfo.getRegion().getProvinceCode());
                locationInfo.setCityCode(regionInfo.getRegion().getCityCode());
                locationInfo.setCountyCode(regionInfo.getRegion().getCountyCode());
                locationInfo.setTownCode(regionInfo.getRegion().getTownCode());
                locationInfo.setLatitude(one.getLatitude());
                locationInfo.setLongitude(one.getLongitude());
                locationInfo.setAltitude(one.getAltitude());
                locationInfoList.add(locationInfo);
            }
            //更新无人机位置信息
            if (CollectionUtils.isNotEmpty(locationInfoList)) {
                uavDeviceDAO.updateUavDeviceLocationInfo(locationInfoList);
            }
            //更新机巢位置信息
            List<String> deviceCodes = uavDeviceUpdateLocationList.stream().map(UavDeviceStatusReport::getDeviceCode).collect(Collectors.toList());
            List<UavDeviceReport> uavDeviceReports = uavDeviceDAO.queryBindNestCodeByUavDeviceCode(deviceCodes);
            if (CollectionUtils.isEmpty(uavDeviceReports)) {
                return;
            }
            Map<String, String> deviceCodeToUavNestCodeMap = uavDeviceReports.stream()
                    .collect(Collectors.toMap(UavDeviceReport::getDeviceCode, UavDeviceReport::getBindNestDeviceCode));
            Iterator<UavNestLocationVO> iterator = locationInfoList.iterator();
            while (iterator.hasNext()) {
                UavNestLocationVO item = iterator.next();
                String uavNestDeviceCode = MapUtils.getString(deviceCodeToUavNestCodeMap, item.getDeviceCode());
                if (StringUtils.isEmpty(uavNestDeviceCode)) {
                    iterator.remove();
                }
                else {
                    item.setDeviceCode(uavNestDeviceCode);
                }
            }
            if (CollectionUtils.isNotEmpty(locationInfoList)) {
                uavNestDAO.updateUavNestDeviceLocationInfo(locationInfoList);
            }
        }
    }
}

