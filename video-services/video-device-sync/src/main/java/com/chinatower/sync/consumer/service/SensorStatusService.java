package com.chinatower.sync.consumer.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.entity.vo.AIotDeviceCenterRsp;
import com.chinatower.event.util.DateUtils;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceNorthIntfConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.enums.AIotPlatformCommonEnum;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.sync.consumer.constants.IotDeviceEnum;
import com.chinatower.sync.consumer.dao.MongoUtilDAO;
import com.chinatower.sync.consumer.dao.SensorAttributeMongoDAO;
import com.chinatower.sync.consumer.dao.SensorStatusDAO;
import com.chinatower.sync.consumer.item.*;
import com.chinatower.sync.consumer.util.DeviceCommonUtils;
import com.chinatower.sync.consumer.util.MongoHashUtil;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 传感器状态上报
 * Created by zhujun on 2022/3/30.
 */
@Slf4j
@Component
public class SensorStatusService {

    private static final List<String> HORN_DEVICE_TYPE_LIST = Lists.newArrayList("4001", "4002");

    @Autowired
    private SensorAttributeMongoDAO sensorAttributeMongoDAO;

    @Autowired
    private SensorStatusDAO sensorStatusDAO;

    @Autowired
    private MongoUtilDAO mongoUtilDAO;

    @Autowired
    private GisMapApi gisMapApi;

    @Autowired
    private DeviceCommonUtils deviceCommonUtils;

    @Autowired
    private BroadcastDeviceService broadcastDeviceService;

    @Autowired
    private MongoHashUtil mongoHashUtil;

    private static final String YYYYMM_FORMAT = "yyyyMM";

    public static final String TENANT_ID = "tenantId";
    public static final String INDUSTRY_CODE = "industryCode";
    public static final String ALARM_TIME = "alarmTime";

    public void dealAIotDeviceStatusLogic(List<String> messages) {
        log.info("AIot物联设备状态信息消费信息:{}, size:{}", JSON.toJSONString(messages), messages.size());
        List<AIotDeviceStatusSync> dataList = Lists.newArrayList();
        for (String message : messages) {
            AIotDeviceStatusSync temp = JSON.parseObject(message, AIotDeviceStatusSync.class);
            dataList.add(temp);
        }
        this.dealAIotDeviceStatusData(dataList);
    }

    private void dealAIotDeviceStatusData(List<AIotDeviceStatusSync> dataList) {

        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        // Dtu设备不做同步，去除Dtu设备
        dataList = dataList.stream().filter(x -> !StringUtils.isEmpty(x.getDeviceCode()) && !x.getDeviceCode().contains("TTE")).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        /**
         * 当设备中心同步状态时，如果有设备则更新设备状态；如果没有设备，则通过接口反查获取设备信息，插入设备和状态。
         */
        // 根据deviceCode取最新的一条
        Map<String, AIotDeviceStatusSync> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        AIotDeviceStatusSync::getDeviceCode, // keyMapper: 从对象中提取deviceCode作为Map的键
                        Function.identity(), // valueMapper: 对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getSourceVersion() >= oldRecord.getSourceVersion()) ? newRecord : oldRecord
                ));
        dataList = Lists.newArrayList(dataMap.values());

        List<LocalizationSensorStatusReport> sensorStatusReportList = dataList.stream()
                .filter(x -> StringUtils.isNotEmpty(x.getExt()))
                .flatMap(x -> {
                    List<LocalizationSensorStatusReport> sensorStatusReportSubList = JSON.parseArray(x.getExt(), LocalizationSensorStatusReport.class);
                    return CollectionUtils.isNotEmpty(sensorStatusReportSubList) ? sensorStatusReportSubList.stream() : Stream.empty();
                }).collect(Collectors.toList());

        List<SensorStatusReport> updateList = Lists.newArrayList();
        List<SensorStatusReport> insertList = Lists.newArrayList();
        List<String> insertDeviceCode = Lists.newArrayList();

        List<String> deviceCodeList = dataList.stream().map(AIotDeviceStatusSync::getDeviceCode).filter(deviceCode -> !StringUtils.isEmpty(deviceCode))
                .distinct().collect(Collectors.toList());
        List<DeviceAppModVO> deviceAppModList = sensorStatusDAO.queryDeviceAppModAndVersionInfo(deviceCodeList);

        Map<String, Long> statusVersionMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(deviceAppModList)) {
            statusVersionMap = deviceAppModList.stream().collect(
                    Collectors.toMap(DeviceAppModVO::getDeviceCode, DeviceAppModVO::getStatusVersion, (a, b) -> a));
        }

        log.info("dealAIotDeviceStatusData deviceCodeList:{}, deviceAppModList:{}", JSON.toJSONString(deviceCodeList), JSON.toJSONString(deviceAppModList));

        String deviceCode;
        for (AIotDeviceStatusSync statusSync : dataList) {
            deviceCode = statusSync.getDeviceCode();
            if (statusVersionMap.containsKey(deviceCode)) {
                if (Objects.isNull(statusSync.getSourceVersion()) || statusSync.getSourceVersion() <= statusVersionMap.get(deviceCode)) {
                    continue;
                }
                SensorStatusReport item = deviceCommonUtils.sensorStatusDTOConvert(statusSync);
                updateList.add(item);
            }
            else {
                insertDeviceCode.add(deviceCode);
            }
        }

        if (CollectionUtils.isNotEmpty(insertDeviceCode)) {
            try {
                //反查加入新增
                DeviceReadReq qry = new DeviceReadReq(insertDeviceCode, AIotPlatformCommonEnum.Platform.AIOT.getPlatformCode());
                AIotDeviceCenterRsp resultVO  = deviceCommonUtils.callDeviceCenter(qry, NetConnectUtils.RequestParamter.URLMappingEnum.DEVICE_CENTER_QRY_DEVICE ,"");
                if (AIotPlatformCommonEnum.CODE_SUCCESS_CODE.equals(resultVO.getCode())) {
                    List<AIotDeviceSync> devices = JSON.parseArray(JSON.toJSONString(resultVO.getData())).toJavaList(AIotDeviceSync.class);
                    for (AIotDeviceSync device : devices) {
                        SensorStatusReport item = deviceCommonUtils.sensorDTOConvert(device);
                        AIotDeviceStatusSync aiotStatusInfo = dataMap.get(device.getDeviceCode());
                        if (aiotStatusInfo != null) {
                            item.setStatusVersion(aiotStatusInfo.getSourceVersion());
                            item.setOnlineStatus(aiotStatusInfo.getOnlineStatus());
                        }

                        // 只有指定设备类型，新增物联才会插入wZone字段
                        IotDeviceEnum.IotDeviceTypeZoneEnum iotDeviceTypeZoneEnum = IotDeviceEnum.IotDeviceTypeZoneEnum.getByDeviceType(item.getCatalogType());
                        if (iotDeviceTypeZoneEnum != null) {
                            item.setWZone(iotDeviceTypeZoneEnum.getWZone());
                        }
                        insertList.add(item);
                    }
                }
            } catch (Exception e) {
                log.error("反查设备信息失败, e:", e);
            }

        }

        if (CollectionUtils.isNotEmpty(insertList)) {
            sensorStatusDAO.insertDeviceBatch4Aiot(insertList);

            // 同步到mongodb
            insertMongoInfo(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            sensorStatusDAO.updateDeviceStatusBatch4Aiot(updateList);
        }
        // 处理物联因子状态的信息 ext
        if (CollectionUtils.isNotEmpty(sensorStatusReportList)) {
            log.info("AIot物联因子状态信息:{}", JSON.toJSONString(sensorStatusReportList));
            dealLocationDeviceDataInfo(sensorStatusReportList, true);
        }
    }

    private void insertMongoInfo(List<SensorStatusReport> insertMongoList) {
        try {
            if (CollectionUtils.isEmpty(insertMongoList)) {
                return;
            }

            String tableTime = DateUtil.format(new Date(), YYYYMM_FORMAT);
            String collectionName = Constants.TABLE_NAME_STATUS + tableTime;

            sensorAttributeMongoDAO.insertAll(insertMongoList, collectionName, MongoDBType.PRIMARY);
            //先检查索引是否已存在
            boolean listIndexs = checkListIndexs(collectionName, MongoDBType.PRIMARY);
            log.info("Aiot状态上报查询索引结果：{}",listIndexs);
            if (!listIndexs) {
                try {
                    List<IndexModel> indexModels = getIndexModels();
                    mongoUtilDAO.mongoCreateIndexs(collectionName, MongoDBType.PRIMARY, indexModels);
                } catch (Exception e) {
                    log.error("Aiot创建索引异常", e);
                }
            }

            SysConfig sysConfig = sensorStatusDAO.getSysConfig(Constants.DEPENDENCE_MANAGE);
            if (sysConfig != null && Constants.ONE.equals(sysConfig.getConfigValue())) {
                // 创建hash索引
                mongoHashUtil.createHashedIndexIfNeed(collectionName, Constants.DEVICECODE, "idx_deviceCode_hash");
                // 处理分片
                mongoHashUtil.shardCollectionIfNeed(collectionName, Constants.DEVICECODE);
            }
        } catch (Exception e) {
            log.error("SensorStatusService insertMongoInfo error:", e);
        }
    }

    public void dealAiotDeviceLogic(List<String> messages) {
        log.info("AIot物联设备信息消费信息:{}, size:{}", JSON.toJSONString(messages), messages.size());
        List<AIotDeviceSync> sensorDataList = Lists.newArrayList();
        List<AIotDeviceSync> hornDataList = Lists.newArrayList();
        for (String message : messages) {
            AIotDeviceSync temp = JSON.parseObject(message, AIotDeviceSync.class);
            // aIot设备同步有可能有广播设备，3.2版本之前广播设备是在aIot平台管理的
            if (HORN_DEVICE_TYPE_LIST.contains(temp.getSubDevType())) {
                hornDataList.add(temp);
            } else {
                sensorDataList.add(temp);
            }
        }
        // 处理物联设备
        this.dealAiotDeviceData(sensorDataList);

        // 处理广播设备
        broadcastDeviceService.dealDeviceBroadcastInfoFromAIot(hornDataList);
    }

    private void dealAiotDeviceData(List<AIotDeviceSync> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        // Dtu设备不做同步，去除Dtu设备
        dataList = dataList.stream().filter(x -> !StringUtils.isEmpty(x.getDeviceCode()) && !x.getDeviceCode().contains("TTE")).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        /**
         * 设备中心推送设备时，如果当前设备表没有，则抛弃当前信息；
         * 如果当前设备表有设备，则需要区分是否人工修改过数据，修改后按之前逻辑处理（更新部分字段）。
         */
        // 根据deviceCode取最新的一条
        Map<String, AIotDeviceSync> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        AIotDeviceSync::getDeviceCode, // keyMapper: 从对象中提取deviceCode作为Map的键
                        Function.identity(), // valueMapper: 对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getSourceVersion() >= oldRecord.getSourceVersion()) ? newRecord : oldRecord
                ));
        dataList = Lists.newArrayList(dataMap.values());

        List<SensorStatusReport> updateList = Lists.newArrayList();
        List<SensorStatusReport> updateModList = Lists.newArrayList();

        List<String> deviceCodeList = dataList.stream().map(AIotDeviceSync::getDeviceCode).filter(deviceCode -> !StringUtils.isEmpty(deviceCode))
                .distinct().collect(Collectors.toList());
        List<DeviceAppModVO> deviceAppModList = sensorStatusDAO.queryDeviceAppModAndVersionInfo(deviceCodeList);


        Map<String, Integer> deviceAppModMap = Maps.newHashMap();
        Map<String, Long> deviceVersionMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(deviceAppModList)) {
            deviceAppModMap = deviceAppModList.stream().collect(
                    Collectors.toMap(DeviceAppModVO::getDeviceCode, DeviceAppModVO::getAppModFlag, (a, b) -> a));
            deviceVersionMap = deviceAppModList.stream().collect(
                    Collectors.toMap(DeviceAppModVO::getDeviceCode, DeviceAppModVO::getDeviceVersion, (a, b) -> a));
        }
        String deviceCode;
        List<SensorStatusReport> insertMongoList = Lists.newArrayList();
        for (AIotDeviceSync deviceSyncItem : dataList) {
            deviceCode = deviceSyncItem.getDeviceCode();
            if (deviceAppModMap.containsKey(deviceCode)) {
                if (Objects.isNull(deviceSyncItem.getSourceVersion()) || deviceSyncItem.getSourceVersion() <= deviceVersionMap.get(deviceCode)) {
                    continue;
                }
                SensorStatusReport item = deviceCommonUtils.sensorDTOConvert(deviceSyncItem);
                if (Objects.equals(deviceAppModMap.get(deviceCode), 1)) {
                    updateModList.add(item);
                } else {
                    //补充自己库表属性值
                    updateList.add(item);
                }
                insertMongoList.add(item);
            }
        }

        if (CollectionUtils.isNotEmpty(insertMongoList)) {
            // 同步到mongodb
            insertMongoInfo(insertMongoList);
        }

        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            sensorStatusDAO.updateDeviceBatch4Aiot(updateList);
        }
        // 批量更新mod
        if (CollectionUtils.isNotEmpty(updateModList)) {
            sensorStatusDAO.updateDeviceModBatch4Aiot(updateModList);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void dealDeviceMsgNew(List<String> messages, boolean isAIot) {
        log.info("传感器设备消费信息size:{}", messages.size());
        List<SensorStatusReport> dataList = Lists.newArrayList();
        for (String sensorValue : messages) {
            CanalBean canalBeans = JSON.parseObject(sensorValue, CanalBean.class);
            List<SensorStatusReport> subDataList = canalBeans.getData().stream().map(x -> {
                SensorStatusReport data = JSON.parseObject(JSON.toJSONString(x), SensorStatusReport.class);
                data.setTs(canalBeans.getTs());
                data.setCanalType(canalBeans.getType());
                return data;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(subDataList)) {
                dataList.addAll(subDataList);
            }
        }
        // 3.4版本以上不处理物联设备
        if (isAIot) {
            dataList = dataList.stream().filter(x -> !Objects.equals(x.getDeviceType(), DeviceConstants.IOT_DEVICE_SENSOR)).collect(Collectors.toList());
        }
        this.dealDeviceDataInfoNew(dataList);
    }

    public void dealSensorMsgNew(List<String> messages) {
        log.info("传感器状态消费信息:{}, size:{}", messages, messages.size());
        List<MonitorStatusReprot> dataList = Lists.newArrayList();
        for (String sensorValue : messages) {
            CanalBean canalBeans = JSON.parseObject(sensorValue, CanalBean.class);
            List<MonitorStatusReprot> subDataList = canalBeans.getData().stream().map(x -> {
                MonitorStatusReprot data = JSON.parseObject(JSON.toJSONString(x), MonitorStatusReprot.class);
                data.setCanalType(canalBeans.getType());
                return data;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(subDataList)) {
                dataList.addAll(subDataList);
            }
        }
        this.dealSensorDataInfoNew(dataList);
    }

    public void dealLocationSensorDeviceMsg(List<String> sensorValues) {
        SysConfig sysConfig = sensorStatusDAO.getSysConfig(Constants.DEPENDENCE_MANAGE);
        if (sysConfig != null && (Constants.ZERO.equals(sysConfig.getConfigValue())
                || Constants.TWO.equals(sysConfig.getConfigValue()))) {
            log.info("属地化物联传感器状态消费信息:{},size:{}", sensorValues,sensorValues.size());
            List<LocalizationSensorStatusReport> sensorStatusReportList = new ArrayList<>();
            for (String sensorValue : sensorValues){
                List<LocalizationSensorStatusReport> sensorReportList = JSON.parseArray(sensorValue, LocalizationSensorStatusReport.class);
                sensorStatusReportList.addAll(sensorReportList);
            }
            this.dealLocationDeviceDataInfo(sensorStatusReportList, false);
        }
    }


    public void dealRadarDeviceMsg(List<String> radarValues) {
        SysConfig sysConfig = sensorStatusDAO.getSysConfig(Constants.DEPENDENCE_MANAGE);
        if (sysConfig != null && (Constants.ZERO.equals(sysConfig.getConfigValue())
                || Constants.TWO.equals(sysConfig.getConfigValue()))) {
            log.info("雷达状态消费信息:{}", radarValues);
            this.dealRadarDeviceInfo(radarValues);
        }
    }

    public void dealHornDeviceMsg(String radarValue) {
        SysConfig sysConfig = sensorStatusDAO.getSysConfig(Constants.DEPENDENCE_MANAGE);
        if (sysConfig != null && (Constants.ZERO.equals(sysConfig.getConfigValue())
                || Constants.TWO.equals(sysConfig.getConfigValue()))) {
            log.info("喇叭状态消费信息:{}", radarValue);
            this.dealHornDeviceInfo(radarValue);
        }
    }

    public void dealHornDeviceMsgV2(List<String> messages) {
        SysConfig sysConfig = sensorStatusDAO.getSysConfig(Constants.DEPENDENCE_MANAGE);
        if (sysConfig != null && (Constants.ZERO.equals(sysConfig.getConfigValue())
                || Constants.TWO.equals(sysConfig.getConfigValue()))) {
            log.info("喇叭状态消费信息V2:{}", JSON.toJSONString(messages));
            this.dealHornDeviceInfoV2(messages);
        }
    }

    /**
     * 雷达设备上报
     *
     * @param radarValues 上报信息
     */
    public void dealRadarDeviceInfo(List<String> radarValues) {
        List<RadarStatusReport> radarStatusReportList = new ArrayList<>();
        for (String radarValue : radarValues){
            List<RadarStatusReport> radarReportList = JSON.parseArray(radarValue, RadarStatusReport.class);
            radarStatusReportList.addAll(radarReportList);
        }
        if (!CollectionUtils.isEmpty(radarStatusReportList)){
            List<RadarStatusReport> statusReportList = radarStatusReportList.stream().filter(radarStatusReport -> !StringUtils.isEmpty(radarStatusReport.getDeviceCode())).collect(Collectors.toList());
            //根据上报的雷达编码更改状态
            sensorStatusDAO.updateRadarDeviceBatch(statusReportList);
        }

    }

    /**
     * 喇叭设备上报
     *
     * @param radarValue 上报信息
     */
    public void dealHornDeviceInfo(String radarValue) {
        HornStatusReport hornStatusReport = JSON.parseObject(radarValue, HornStatusReport.class);
        if (!StringUtils.isEmpty(hornStatusReport.getDeviceCode())) {
            //根据上报的雷达编码更改状态
            sensorStatusDAO.updateHornDevice(hornStatusReport);
        }
    }

    public void dealHornDeviceInfoV2(List<String> messages) {
        List<HornStatusReport> paramList = Lists.newArrayList();
        for (String message : messages) {
            HornStatusReportV2 hornStatusReport = JSON.parseObject(message, HornStatusReportV2.class);
            if (!StringUtils.isEmpty(hornStatusReport.getDeviceCode())) {
                //根据上报的雷达编码更改状态
                HornStatusReport param = new HornStatusReport();
                param.setDeviceCode(hornStatusReport.getDeviceCode());
                param.setOnlineStatus(hornStatusReport.getDevStatus());
                paramList.add(param);
            }
        }
        sensorStatusDAO.updateHornDeviceBatch(paramList);
    }

    /**
     * 属地化物联设备上报处理
     */
    public void dealLocationDeviceDataInfo(List<LocalizationSensorStatusReport> sensorStatusReportList, boolean isAIot) {
        //上报中设备编码不为空的编码集合
        List<String> existDeviceCode = sensorStatusReportList.stream().map(LocalizationSensorStatusReport::getDeviceCode).filter(deviceCode -> !StringUtils.isEmpty(deviceCode)).collect(Collectors.toList());
        //库中存在的设备编码集合
        List<String> deviceCodeList = sensorStatusDAO.queryDevice(existDeviceCode);

        String tableTime = DateUtil.format(new Date(), YYYYMM_FORMAT);
        //需要处理状态的设备信息
        List<LocalizationSensorStatusReport> updateSensorStatusReports = new ArrayList<>();
        //全量因子信息
        List<MonitorStatusReprot> allMonitorStatusList = new ArrayList<>();
        //需要修改的因子信息
        List<MonitorStatusReprot> updateMonitorStatusList = new ArrayList<>();
        //需要新增的因子信息
        List<MonitorStatusReprot> insertMonitorStatusList = new ArrayList<>();

        //取上报设备中  在库中存在的设备
        for (LocalizationSensorStatusReport sensorStatusReport : sensorStatusReportList){
            if (deviceCodeList.contains(sensorStatusReport.getDeviceCode())){
                //同步状态
                if (StringUtils.isEmpty(sensorStatusReport.getLastTime())) {
                    sensorStatusReport.setLastTime(DateUtils.currentDateToString());
                }
                updateSensorStatusReports.add(sensorStatusReport);

                //物联因子信息
                if (CollectionUtils.isNotEmpty(sensorStatusReport.getMonitorStatusList())){
                    List<MonitorStatusReprot> monitorStatusList = sensorStatusReport.getMonitorStatusList();
                    monitorStatusList.forEach(monitor -> {
                        monitor.setDeviceCode(sensorStatusReport.getDeviceCode());
                        // 物联平台同步状态是 0-有效 1-无效，和数据库相反，需要转换一下
                        monitor.setStatus(1 == monitor.getStatus() ? 0 : 1);
                        if (StringUtils.isEmpty(monitor.getMonitorDiscription())) {
                            monitor.setMonitorDiscription(monitor.getMonitorName());
                        }
                        monitor.setDeleteFlag(Constants.DELETEFLAG_0);
                    });
                    allMonitorStatusList.addAll(monitorStatusList);
                }
            }
        }

        //处理设备 aiot平台上报不处理设备信息
        if (!isAIot && !CollectionUtils.isEmpty(updateSensorStatusReports)){
            //批量修改状态
            sensorStatusDAO.updateLocationDeviceBatch(updateSensorStatusReports);
            //插入MongoDB库
            sensorAttributeMongoDAO.insertAll(updateSensorStatusReports, Constants.TABLE_NAME_STATUS + tableTime, MongoDBType.PRIMARY);

            //先检查索引是否已存在
            boolean listIndexs = checkListIndexs(Constants.TABLE_NAME_STATUS + tableTime, MongoDBType.PRIMARY);
            log.info("状态上报查询索引结果：{}",listIndexs);
            if (!listIndexs) {
                try {
                    List<IndexModel> indexModels = getIndexModels();
                    mongoUtilDAO.mongoCreateIndexs(Constants.TABLE_NAME_STATUS + tableTime, MongoDBType.PRIMARY, indexModels);
                } catch (Exception e) {
                    log.error("创建索引异常", e);
                }
            }
        }
        //处理因子
        if (!CollectionUtils.isEmpty(allMonitorStatusList)){
            // 取最新的一条
            Map<String, MonitorStatusReprot> dataMap = allMonitorStatusList.stream()
                    .collect(Collectors.toMap(
                            monitorStatusReprot -> assembleSensorMapKey(
                                    monitorStatusReprot.getDeviceCode(),
                                    monitorStatusReprot.getSensorCode(),
                                    monitorStatusReprot.getMonitorName()
                            ),
                            Function.identity(), // valueMapper: Record对象本身作为值
                            (oldRecord, newRecord) -> (DateUtils.compareStrTime(oldRecord.getMonitorTime(), newRecord.getMonitorTime(), "yyyy-MM-dd HH:mm:ss")) ? oldRecord : newRecord // mergeFunction: 冲突时选择时间戳最新的Record
                    ));
            allMonitorStatusList = Lists.newArrayList(dataMap.values());

            List<String> deviceCodes = allMonitorStatusList.stream().map(MonitorStatusReprot::getDeviceCode).collect(Collectors.toList());
            List<String> monitorNames = assembleMonitorNameList(allMonitorStatusList);
            //库中存在的因子信息
            List<MonitorStatusReprot> monitorList = sensorStatusDAO.queryMonitorList(deviceCodes, monitorNames);
            Map<String, MonitorStatusReprot> dbDataMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(monitorList)) {
                dbDataMap = monitorList.stream().collect(Collectors.toMap(x -> assembleSensorMapKey(x.getDeviceCode(), x.getSensorCode(), x.getMonitorName()), p -> (p), (a, b) -> a));
            }
            for (MonitorStatusReprot monitorStatusReprot : allMonitorStatusList){
                String key = assembleSensorMapKey(monitorStatusReprot.getDeviceCode(), monitorStatusReprot.getSensorCode(), monitorStatusReprot.getMonitorName());
                if (dbDataMap.get(key) != null){
                    //库中存在-修改
                    updateMonitorStatusList.add(monitorStatusReprot);
                }else {
                    //库中不存在-新增
                    insertMonitorStatusList.add(monitorStatusReprot);
                }
            }
            if (!CollectionUtils.isEmpty(updateMonitorStatusList)){
                sensorStatusDAO.modifyMonitorBatch(updateMonitorStatusList);
            }
            if (!CollectionUtils.isEmpty(insertMonitorStatusList)){
                sensorStatusDAO.insertMonitorBatch(insertMonitorStatusList);
            }
            sensorAttributeMongoDAO.insertAll(allMonitorStatusList,Constants.TABLE_NAME_SENSOR_STATUS + tableTime,MongoDBType.PRIMARY);
        }
    }

    private List<String> assembleMonitorNameList(List<MonitorStatusReprot> allMonitorStatusList) {
        List<String> monitorNames = Lists.newArrayList();
        for (MonitorStatusReprot report : allMonitorStatusList) {
            try {
                String monitorName = report.getMonitorName();
                if (monitorName.contains("#")) {
                    String[] parts = monitorName.split("#");
                    monitorNames.add(parts[1]);
                    report.setMonitorName(parts[1]);
                    report.setSensorCode(parts[0]);
                } else {
                    monitorNames.add(monitorName);
                    report.setSensorCode("");
                }
            } catch (Exception e) {
                log.error("处理传感器物联因子字段异常", e);
            }
        }
        return monitorNames;
    }

    public void dealDeviceDataInfoNew(List<SensorStatusReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        // Dtu设备不做同步，去除Dtu设备
        dataList = dataList.stream().filter(x -> !StringUtils.isEmpty(x.getDeviceCode()) && !x.getDeviceCode().contains("TTE")).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        Stopwatch sw = Stopwatch.createStarted();
        String uuid = UUID.randomUUID().toString();
        int preSize = dataList.size();
        // 根据deviceCode取最新的一条
        Map<String, SensorStatusReport> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        SensorStatusReport::getDeviceCode, // keyMapper: 从Record中提取code作为Map的键
                        Function.identity(), // valueMapper: Record对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getTs() >= oldRecord.getTs()) ? newRecord : oldRecord // mergeFunction: 冲突时选择时间戳最新的Record
                ));
        dataList = Lists.newArrayList(dataMap.values());
        int afterSize = dataList.size();
        log.info("dealDeviceDataInfoNew preSize:{} afterSize:{} diffSize:{} uuid:{}", preSize, afterSize, (preSize - afterSize), uuid);

        List<SensorStatusReport> insertMongoList = Lists.newArrayList();
        List<SensorStatusReport> insertList = Lists.newArrayList();
        List<SensorStatusReport> updateList = Lists.newArrayList();
        List<SensorStatusReport> updateModList = Lists.newArrayList();

        List<String> deviceCodeList = dataList.stream().map(SensorStatusReport::getDeviceCode).distinct().collect(Collectors.toList());
        List<DeviceAppModVO> deviceAppModList = sensorStatusDAO.queryDeviceAppModInfo(deviceCodeList);
        // key -> deviceCode value -> appModFlag
        Map<String, Integer> deviceAppModMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(deviceAppModList)) {
            log.info("dealDeviceDataInfoNew deviceAppModList:{}", JSON.toJSONString(deviceAppModList));
            deviceAppModMap = deviceAppModList.stream().collect(
                    Collectors.toMap(DeviceAppModVO::getDeviceCode, DeviceAppModVO::getAppModFlag, (a, b) -> a));
        }

        List<SensorStatusReport> paramDeviceModelList = dataList.stream()
                .filter(x -> !StringUtils.isEmpty(x.getDeviceCode()) && !StringUtils.isEmpty(x.getDeviceModel())
                        && !StringUtils.isEmpty(x.getManufacturer())).collect(Collectors.toList());

        String deviceCode;
        for (SensorStatusReport sensorStatusReport : dataList) {
            deviceCode = sensorStatusReport.getDeviceCode();
            if (!StringUtils.isEmpty(deviceCode)) {
                //补充自己库表属性值
                this.setSensorStatusReportData(sensorStatusReport);
                if (StringUtils.isEmpty(sensorStatusReport.getDeviceModel())){
                    sensorStatusReport.setDeviceModel("");
                }
                //运管删除该设备，应用平台同步更改状态为不可用
                if (Constants.DELETE.equals(sensorStatusReport.getCanalType())) {
                    sensorStatusReport.setDeleteFlag(Constants.DELETEFLAG_1);
                    updateList.add(sensorStatusReport);
                } else {
                    if (Constants.DELETEFLAG_1.equals(sensorStatusReport.getDelFlag())) {
                        sensorStatusReport.setDeleteFlag(Constants.DELETEFLAG_1);
                    } else {
                        sensorStatusReport.setDeleteFlag(Constants.DELETEFLAG_0);
                    }
                    if (sensorStatusReport.getCatalogType() == null) {
                        sensorStatusReport.setCatalogType("");
                    }
                    // 数据库是否存在，存在则更新，不存在则新增
                    if (deviceAppModMap.containsKey(deviceCode)) {
                        if (Objects.equals(deviceAppModMap.get(deviceCode), 1)) {
                            //本地应用侧有修改
                            updateModList.add(sensorStatusReport);
                        } else {
                            updateList.add(sensorStatusReport);
                        }
                    } else {
                        insertList.add(sensorStatusReport);
                    }
                    // 记录插入mongodb的数据
                    insertMongoList.add(sensorStatusReport);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(paramDeviceModelList)) {
            handleDeviceModelLogic(paramDeviceModelList);
        }

        // 批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            sensorStatusDAO.insertDeviceBatch(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            sensorStatusDAO.updateDeviceBatch(updateList);
        }
        // 批量更新mod
        if (CollectionUtils.isNotEmpty(updateModList)) {
            sensorStatusDAO.updateDeviceModBatch(updateModList);
        }

        if (CollectionUtils.isNotEmpty(insertMongoList)) {
            String tableTime = DateUtil.format(new Date(), YYYYMM_FORMAT);
            sensorAttributeMongoDAO.insertAll(insertMongoList, Constants.TABLE_NAME_STATUS + tableTime, MongoDBType.PRIMARY);
            //先检查索引是否已存在
            boolean listIndexs = checkListIndexs(Constants.TABLE_NAME_STATUS + tableTime, MongoDBType.PRIMARY);
            log.info("状态上报查询索引结果：{}",listIndexs);
            if (!listIndexs) {
                try {
                    List<IndexModel> indexModels = getIndexModels();
                    mongoUtilDAO.mongoCreateIndexs(Constants.TABLE_NAME_STATUS + tableTime, MongoDBType.PRIMARY, indexModels);
                } catch (Exception e) {
                    log.error("创建索引异常", e);
                }
            }
        }
        log.info("SensorStatusService dealDeviceDataInfo total sw:{}, uuid:{}", sw.elapsed(TimeUnit.MILLISECONDS), uuid);
    }

    private void handleDeviceModelLogic(List<SensorStatusReport> paramDeviceModelList) {
        try {
            List<String> modelCodeList = paramDeviceModelList.stream().map(SensorStatusReport::getManufacturer)
                    .distinct().collect(Collectors.toList());
            List<String> deviceModelList = paramDeviceModelList.stream().map(SensorStatusReport::getDeviceModel)
                    .distinct().collect(Collectors.toList());

            List<SensorStatusReport> deviceModelInfoList = sensorStatusDAO.queryIotDeviceModel(modelCodeList, deviceModelList);
            List<String> deviceModelKeyList = deviceModelInfoList.stream().map(x -> assembleModelKey(x.getManufacturer()
                    , x.getDeviceModel())).distinct().collect(Collectors.toList());

            Date now = new Date();
            List<SensorStatusReport> insertDeviceModelList = Lists.newArrayList();
            for (SensorStatusReport paramDeviceModel : paramDeviceModelList) {
                String paramModelKey = assembleModelKey(paramDeviceModel.getManufacturer(), paramDeviceModel.getDeviceModel());
                if (!deviceModelKeyList.contains(paramModelKey)) {
                    Integer deviceType = DeviceConstants.IOT_DEVICE_RADAR_3.equals(paramDeviceModel.getDeviceType()) ? 1 : 2;
                    SensorStatusReport insertDeviceModel = new SensorStatusReport();
                    insertDeviceModel.setDeviceType(deviceType);
                    insertDeviceModel.setDeviceModel(paramDeviceModel.getDeviceModel());
                    insertDeviceModel.setManufacturer(paramDeviceModel.getManufacturer());
                    insertDeviceModel.setCreateTime(now);
                    insertDeviceModelList.add(insertDeviceModel);
                }
            }
            if (CollectionUtils.isNotEmpty(insertDeviceModelList)) {
                sensorStatusDAO.addIotDeviceModelBatch(insertDeviceModelList);
            }
        } catch (Exception e) {
            log.error("SensorStatusService handleDeviceModelLogic error:", e);
        }
    }

    private String assembleModelKey(String manufacturer, String deviceModel) {
        return manufacturer + "#" + deviceModel;
    }

    /**
     * 查询索引列
     * @param tableName
     * @param primary
     * @return
     */
    private boolean checkListIndexs(String tableName, MongoDBType primary) {
        boolean success = false;
        try {
            ListIndexesIterable<Document> indexList = mongoUtilDAO.mongoListIndexs(tableName, primary);
            for (Document document : indexList) {
                Object key = document.get("key");
                if (null != key && key instanceof Document) {
                    Document keyDocument = (Document) key;
                    log.info("vm_iot_device_t_表对应的index:{}",keyDocument.toString());
                    if (keyDocument.containsKey("updateTime") && keyDocument.containsKey(Constants.INDEXFIELD_DEVICECODE)) {
                        success = true;
                    }
                }
            }
        } catch (Exception e) {
            success = false;
        }
        return success;
    }

    private List<IndexModel> getIndexModels() {
        List<IndexModel> indexModels = new ArrayList<>();
        BasicDBObject indexAlarmCode = new BasicDBObject();
        indexAlarmCode.put(Constants.INDEXFIELD_UPDATETIME, 1);
        IndexOptions indexOptions1 = new IndexOptions().background(true).name("index_updateTime");
        indexModels.add(new IndexModel(indexAlarmCode, indexOptions1));
        //增加设备编码、监测时间索引
        BasicDBObject index = new BasicDBObject();
        index.put(Constants.INDEXFIELD_DEVICECODE, 1);
        index.put(Constants.INDEXFIELD_UPDATETIME, -1);
        IndexOptions indexOptions = new IndexOptions().background(true).name("index_deviceCode_monitorTime");
        indexModels.add(new IndexModel(index,indexOptions));
        return indexModels;
    }

    private void handleIotDeviceModelLogic(SensorStatusReport sensorStatusReport) {
        if (sensorStatusReport == null) {
            return;
        }
        // 厂商编码
        String modelCode = sensorStatusReport.getManufacturer();
        // 设备型号编码
        String deviceModel = sensorStatusReport.getDeviceModel();
        // 设备类型 暂时先定死 雷达 1 、物联设备 2
        String deviceType = DeviceConstants.IOT_DEVICE_RADAR_3.equals(sensorStatusReport.getDeviceType()) ? "1" : "2";
        try {
            if (StringUtils.isEmpty(modelCode) || StringUtils.isEmpty(deviceModel)) {
                return;
            }
            if (sensorStatusDAO.countIotDeviceModel(modelCode, deviceModel) == 0) {
                sensorStatusDAO.addIotDeviceModel(modelCode, deviceModel, deviceType);
            }
        } catch (Exception e) {
            log.error("handleIotDeviceModelLogic error modelCode:{}, deviceModel:{}", modelCode, deviceModel);
        }
    }

    private String assembleMapKey(String deviceCode, String monitorName) {
        return deviceCode + "#" + monitorName;
    }

    /**
     * 1.7.20新需求判断唯一标准为deviceCode+sensorTypeCod+monitorName
     * @param deviceCode
     * @param sensorTypeCode
     * @param monitorName
     * @return
     */
    private String assembleSensorMapKey(String deviceCode, String sensorTypeCode, String monitorName) {
        return deviceCode + "#" + sensorTypeCode + "#" + monitorName;
    }

    public void dealSensorDataInfoNew(List<MonitorStatusReprot> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        String tableTime = DateUtil.format(new Date(), YYYYMM_FORMAT);
        List<MonitorStatusReprot> insertList = Lists.newArrayList();
        List<MonitorStatusReprot> updateList = Lists.newArrayList();
        List<MonitorStatusReprot> insertMongoList = Lists.newArrayList();
        // 参数中所有设备编码去重
        List<String> deviceCodeList = dataList.stream().map(MonitorStatusReprot::getDeviceCode).distinct().collect(Collectors.toList());
        // 参数中所有物联因子编码去重
        List<String> monitorNameList = assembleMonitorNameList(dataList);
        // 根据上报的物联设备编码和物联因子编码查询物联因子数据库信息
        List<MonitorStatusReprot> dbDataList = sensorStatusDAO.queryMonitorList(deviceCodeList, monitorNameList);
        // 拼装物联因子数据字典，key -> deviceCode#monitorName  value -> data
        Map<String, MonitorStatusReprot> dbDataMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(dbDataList)) {
            dbDataMap = dbDataList.stream().collect(Collectors.toMap(x -> assembleSensorMapKey(x.getDeviceCode(), x.getSensorCode(), x.getMonitorName()), p -> (p), (a, b) -> a));
        }

        for (MonitorStatusReprot monitorStatusReprot : dataList) {
            if (StringUtils.isEmpty(monitorStatusReprot.getMonitorDiscription())) {
                monitorStatusReprot.setMonitorDiscription(monitorStatusReprot.getMonitorName());
            }
            if (!StringUtils.isEmpty(monitorStatusReprot.getDeviceCode())) {
                // 运管同步状态是 0-有效 1-无效，和数据库相反，需要转换一下
                monitorStatusReprot.setStatus(1 == monitorStatusReprot.getStatus() ? 0 : 1);
                //运管删除该设备，应用平台同步更改状态为不可用
                if (Constants.DELETE.equals(monitorStatusReprot.getCanalType())) {
                    monitorStatusReprot.setDeleteFlag(Constants.DELETEFLAG_1);
                    updateList.add(monitorStatusReprot);
                } else {
                    monitorStatusReprot.setDeleteFlag(Constants.DELETEFLAG_0);
                    // 数据库中存在改设备+因子状态，进行更改，不存在则更新
                    MonitorStatusReprot dbMonitorStatusInfo = dbDataMap.get(assembleSensorMapKey(monitorStatusReprot.getDeviceCode(), monitorStatusReprot.getSensorCode(), monitorStatusReprot.getMonitorName()));
                    if (dbMonitorStatusInfo != null) {
                        updateList.add(monitorStatusReprot);
                    } else {
                        insertList.add(monitorStatusReprot);
                    }
                    insertMongoList.add(monitorStatusReprot);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(insertList)) {
            try {
                sensorStatusDAO.insertMonitorBatch(insertList);
            } catch (DuplicateKeyException ee) {
                log.warn("dealSensorDataInfoNew duplicate msg:{}", ee.getMessage());
            } catch (Exception e) {
                log.error("dealSensorDataInfoNew insertMonitorBatch error, e:", e);
            }
        }

        if (CollectionUtils.isNotEmpty(updateList)) {
            sensorStatusDAO.updateMonitorBatch(updateList);
        }

        if (CollectionUtils.isNotEmpty(insertMongoList)) {
            sensorAttributeMongoDAO.insertAll(insertMongoList, Constants.TABLE_NAME_SENSOR_STATUS + tableTime, MongoDBType.PRIMARY);
        }
    }

    //根据经纬度 补全 省市区乡镇编码
    public SensorStatusReport setSensorStatusReportData(SensorStatusReport sensorStatusReport) {
        try {
            if (DeviceConstants.IOT_DEVICE_RADAR_3.equals(sensorStatusReport.getDeviceType())) {
                sensorStatusReport.setDeviceType(DeviceConstants.IOT_DEVICE_RADAR);
                sensorStatusReport.setCatalogType(sensorStatusReport.getCategoryCode());
            }
            if (sensorStatusReport.getRemark() == null){
                sensorStatusReport.setRemark("");
            }
            if (sensorStatusReport.getParAngle() == null){
                sensorStatusReport.setParAngle("");
            }
            if (sensorStatusReport.getPublicSiteCode() == null){
                sensorStatusReport.setPublicSiteCode("");
            }
            String longitude = sensorStatusReport.getLongitude();
            String latitude = sensorStatusReport.getLatitude();
            if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
                return sensorStatusReport;
            }
            // 查询区域信息
            Region region = getGisRegion(longitude, latitude);
            if (region != null) {
                if (StringUtils.isEmpty(region.getProvinceCode())) {
                    sensorStatusReport.setProvinceId(DeviceNorthIntfConstants.BEIJING_PROVINCE);
                    sensorStatusReport.setCityId(DeviceNorthIntfConstants.BEIJING_CITY);
                    sensorStatusReport.setCountyId(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
                } else {
                    sensorStatusReport.setProvinceId(region.getProvinceCode());
                    if (!StringUtils.isEmpty(region.getCityCode())) {
                        sensorStatusReport.setCityId(region.getCityCode());
                    }
                    if (!StringUtils.isEmpty(region.getCountyCode())) {
                        sensorStatusReport.setCountyId(region.getCountyCode());
                    }
                    if (!StringUtils.isEmpty(region.getTownCode())) {
                        sensorStatusReport.setTownCode(region.getTownCode());
                    }
                    if (!StringUtils.isEmpty(region.getTown())) {
                        sensorStatusReport.setTownName(region.getTown());
                    }
                }
            } else {
                sensorStatusReport.setProvinceId(DeviceNorthIntfConstants.BEIJING_PROVINCE);
                sensorStatusReport.setCityId(DeviceNorthIntfConstants.BEIJING_CITY);
                sensorStatusReport.setCountyId(DeviceNorthIntfConstants.HAIDIAN_COUNTY);
            }
            log.info("setSensorStatusReportData sensorStatusReport:{}", JSON.toJSONString(sensorStatusReport));
        } catch (Exception e) {
            log.error("setSensorStatusReportData error, e:", e);
        }
        return sensorStatusReport;
    }

    public Region getGisRegion(String longitude, String latitude) {
        String redisKey = String.format("%s%s_%s", DeviceConstants.DEVICE_LONG_LAT_REDIS_KEY, longitude, latitude);
        Region region = RedisUtil.get(redisKey, Region.class, RedisDBType.PRIMARY);
        if (region != null) {
            return region;
        }
        PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
        req.setLocation(String.format("%s,%s", longitude, latitude));
        Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
        log.info("getGisRegion gisMapApi poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && CollectionUtils.isNotEmpty(result.getData())) {
            try {
                PoiContraryGeocodingQueryRes data = result.getData().get(0);
                region = data.getRegion();
                if (region != null) {
                    RedisUtil.set(redisKey, region, 2, TimeUnit.MINUTES, RedisDBType.PRIMARY);
                }
            } catch (Exception e) {
                log.error("getGisRegion gisMapApi poiContraryGeocodingQuery error, e:", e);
            }
        }
        return region;
    }


    public void auditDeviceInfo(String param) {
        List<AIotDeviceSync> dataList = deviceCommonUtils.auditDeviceInfo(param, AIotPlatformCommonEnum.Platform.AIOT.getPlatformCode());
        if (CollectionUtils.isNotEmpty(dataList)) {
            List<AIotDeviceSync> sensorDataList = dataList.stream().filter(x -> !HORN_DEVICE_TYPE_LIST.contains(x.getSubDevType())).collect(Collectors.toList());
            List<AIotDeviceSync> hornDataList = dataList.stream().filter(x -> HORN_DEVICE_TYPE_LIST.contains(x.getSubDevType())).collect(Collectors.toList());

            // 处理物联设备
            this.dealAiotDeviceData(sensorDataList);
            // 处理广播设备
            broadcastDeviceService.dealDeviceBroadcastInfoFromAIot(hornDataList);
        }
    }

    public void auditDeviceStatusInfo(String param) {
        List<AIotDeviceStatusSync> data = deviceCommonUtils.auditDeviceStatusInfo(param, AIotPlatformCommonEnum.Platform.AIOT.getPlatformCode());
        if (!CollectionUtils.isEmpty(data)) {
            this.dealAIotDeviceStatusData(data);
        }
    }
}
