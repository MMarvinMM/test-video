package com.chinatower.sync.consumer.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.sync.consumer.dao.AiotEnergyDeviceDAO;
import com.chinatower.sync.consumer.dao.MongoUtilDAO;
import com.chinatower.sync.consumer.item.*;
import com.chinatower.sync.consumer.util.DeviceCommonUtils;
import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AiotEnergyDeviceService {

    @Autowired
    private AiotEnergyDeviceDAO aiotEnergyDeviceDAO;

    @Autowired
    private MongoUtilDAO mongoUtilDAO;

    @Autowired
    private DeviceCommonUtils deviceCommonUtils;

    private final static String ALARM_KEY = "alarmKey";
    private final static String ALARM_FLAG = "alarmFlag";
    private final static String ALARM_CLEAR_TIME = "alarmClearTime";
    private final static String DEVICE_CODE = "deviceCode";

    public void dealAiotEnergyDeviceMsg(List<String> messages) {
        log.info("dealAiotEnergyDeviceMsg消费信息size:{} messages:{}", messages.size(), messages);
        List<AiotEnergyDeviceReport> dataList = Lists.newArrayList();
        for (String sensorValue : messages) {
            CanalBean canalBeans = JSON.parseObject(sensorValue, CanalBean.class);
            List<AiotEnergyDeviceReport> subDataList = canalBeans.getData().stream().map(x -> {
                AiotEnergyDeviceReport data = JSON.parseObject(JSON.toJSONString(x), AiotEnergyDeviceReport.class);
                data.setTs(canalBeans.getTs());
                data.setCanalType(canalBeans.getType());
                return data;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(subDataList)) {
                dataList.addAll(subDataList);
            }
        }
        this.dealDeviceDataInfo(dataList);
    }

    public void dealEChargePerfSyncMsg(List<String> messages) {
        try {
            Stopwatch sw = Stopwatch.createStarted();
            log.info("dealEChargePerfSyncMsg消费信息:{},size:{}", messages,messages.size());
            List<EChargePerfSyncReport> reportDataList = Lists.newArrayList();
            for (String message : messages){
                EChargePerfSyncReport reportData = JSON.parseObject(message, EChargePerfSyncReport.class);
                // 是否需要处理上报，只有运管同步过来的设备才需要处理
                boolean needHandle = isNeedHandleReport(reportData.getDeviceCode());
                log.info("dealEChargePerfSyncMsg needHandle:{}",needHandle);
                if (needHandle) {
                    reportDataList.add(reportData);
                }
            }
            if (CollectionUtils.isNotEmpty(reportDataList)) {
                String collectionName = Constants.TABLE_ENERGY_DEVICE_PERF_REPORT;
                String allCollectionName = Constants.TABLE_ENERGY_DEVICE_PERF_REPORT_ALL;

                List<Map<String, Object>> inserMapList = new ArrayList<>();
                for (EChargePerfSyncReport reportData : reportDataList) {
                    if (CollectionUtils.isEmpty(reportData.getSignalDatas())) {
                        continue;
                    }
                    Map<String, Object> monMap = new HashMap<>();

                    Date earliestTime = null;
                    for (EnergySignalData signalData : reportData.getSignalDatas()) {
                        monMap.put(signalData.getMid(), signalData.getValue());
                        Date collectTimeDate = DateUtils.parseDate(signalData.getCollectTime(), DateUtils.YYYY_MM_DD_HH_MM_SS);
                        if (earliestTime == null || earliestTime.after(collectTimeDate)) {
                            earliestTime = collectTimeDate;
                        }
                    }
                    monMap.put(DEVICE_CODE, reportData.getDeviceCode());
                    monMap.put("monitorTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, earliestTime));
                    monMap.put("deviceType", "4");
                    inserMapList.add(monMap);
                }
                if (CollectionUtils.isNotEmpty(inserMapList)){
                    List<String> deviceCodeList = inserMapList.stream().map(stringObjectMap -> String.valueOf(stringObjectMap.get(DEVICE_CODE))).collect(Collectors.toList());
                    Criteria criteria = Criteria.where(Constants.DEVICECODE).in(deviceCodeList);
                    Query query = Query.query(criteria);
                    boolean flagExists = mongoUtilDAO.exists(query, MongoDBType.PRIMARY, collectionName);
                    if (flagExists) {
                        mongoUtilDAO.remove(query, MongoDBType.PRIMARY, collectionName);
                    }
                    mongoUtilDAO.insertAll(inserMapList, collectionName, MongoDBType.PRIMARY);
                    //加入重复标识后再添加到全量表--实时数据不加重复标识
                    mongoUtilDAO.insertAll(inserMapList, allCollectionName, MongoDBType.PRIMARY);

                }
            }
            log.info("dealEChargePerfSyncMsg信息记录完成, sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            log.error("AiotEnergyDeviceService dealEChargePerfSyncMsg error:", e);
        }
    }

    public void dealCarChargePerfSyncMsg(List<String> messages) {
        try {
            Stopwatch sw = Stopwatch.createStarted();
            log.info("dealCarChargePerfSyncMsg消费信息:{},size:{}", messages,messages.size());
            List<CarChargePerfSyncReport> reportDataList = Lists.newArrayList();
            for (String message : messages){
                List<CarChargePerfSyncReport> reportDataSubList = JSON.parseArray(message, CarChargePerfSyncReport.class);
                for (CarChargePerfSyncReport reportData : reportDataSubList) {
                    // 是否需要处理上报，只有运管同步过来的设备才需要处理
                    boolean needHandle = isNeedHandleReport(reportData.getDeviceCode());
                    log.info("dealCarChargePerfSyncMsg needHandle:{}",needHandle);
                    if (needHandle) {
                        reportDataList.add(reportData);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(reportDataList)) {
                String collectionName = Constants.TABLE_ENERGY_DEVICE_PERF_REPORT;
                String allCollectionName = Constants.TABLE_ENERGY_DEVICE_PERF_REPORT_ALL;

                List<Map<String, Object>> inserMapList = new ArrayList<>();
                for (CarChargePerfSyncReport reportData : reportDataList) {
                    if (CollectionUtils.isEmpty(reportData.getSignalDatas())) {
                        continue;
                    }
                    Map<String, Object> monMap = new HashMap<>();

                    Date earliestTime = null;
                    for (EnergySignalData signalData : reportData.getSignalDatas()) {
                        monMap.put(signalData.getMid(), signalData.getValue());
                        Date collectTimeDate = DateUtils.parseDate(signalData.getCollectTime(), DateUtils.YYYY_MM_DD_HH_MM_SS);
                        if (earliestTime == null || earliestTime.after(collectTimeDate)) {
                            earliestTime = collectTimeDate;
                        }
                    }
                    monMap.put(DEVICE_CODE, reportData.getDeviceCode());
                    monMap.put("monitorTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, earliestTime));
                    monMap.put("deviceType", "8");
                    inserMapList.add(monMap);
                }
                if (CollectionUtils.isNotEmpty(inserMapList)){
                    List<String> deviceCodeList = inserMapList.stream().map(stringObjectMap -> String.valueOf(stringObjectMap.get(DEVICE_CODE))).collect(Collectors.toList());
                    Criteria criteria = Criteria.where(Constants.DEVICECODE).in(deviceCodeList);
                    Query query = Query.query(criteria);
                    boolean flagExists = mongoUtilDAO.exists(query, MongoDBType.PRIMARY, collectionName);
                    if (flagExists) {
                        mongoUtilDAO.remove(query, MongoDBType.PRIMARY, collectionName);
                    }
                    mongoUtilDAO.insertAll(inserMapList, collectionName, MongoDBType.PRIMARY);
                    //加入重复标识后再添加到全量表--实时数据不加重复标识
                    mongoUtilDAO.insertAll(inserMapList, allCollectionName, MongoDBType.PRIMARY);
                }
            }
            log.info("dealCarChargePerfSyncMsg信息记录完成, sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            log.error("AiotEnergyDeviceService dealCarChargePerfSyncMsg error:", e);
        }
    }

    private String assembleAlarmKey(String deviceCode, String mid) {
        return String.format("%s#%s", deviceCode, mid);
    }

    private final LoadingCache<String, List<String>> CACHE = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats()
            .build(new CacheLoader<String, List<String>>() {
                @Override
                public List<String> load(String key) throws Exception {
                    // 当缓存中没有对应key的值时调用
                    return aiotEnergyDeviceDAO.getAllDeviceCodeList();
                }
            });

    private boolean isNeedHandleReport(String deviceCode) {
        try {
            List<String> deviceCodeList = CACHE.get("allDeviceCodeList");
            if (CollectionUtils.isEmpty(deviceCodeList)) {
                return false;
            }
            // 只处理订购的设备
            return deviceCodeList.contains(deviceCode);
        } catch (Exception e) {
            log.error("Error checking if need to handle alarm for reportData: {}", JSON.toJSONString(deviceCode), e);
            return false;
        }
    }

    public void dealDevAlarmMsg(List<String> messages) {
        try {
            Stopwatch sw = Stopwatch.createStarted();
            log.info("dealDevAlarmMsg消费信息:{},size:{}", messages,messages.size());
            List<EnergyDevAlarmReport> reportDataList = Lists.newArrayList();
            for (String message : messages){
                List<EnergyDevAlarmReport> reportDataSubList = Lists.newArrayList();
                if (message.startsWith("[")) {
                    reportDataSubList = JSON.parseArray(message, EnergyDevAlarmReport.class);
                } else {
                    reportDataSubList.add(JSON.parseObject(message, EnergyDevAlarmReport.class));
                }
                log.info("dealDevAlarmMsg reportDataSubList:{}", JSON.toJSONString(reportDataSubList));
                for (EnergyDevAlarmReport reportData : reportDataSubList) {
                    // 是否需要处理上报，只有运管同步过来的设备才需要处理
                    boolean needHandle = isNeedHandleReport(reportData.getDeviceCode());
                    log.info("dealDevAlarmMsg needHandle:{}",needHandle);
                    if (needHandle && StringUtils.equals(reportData.getBusinessType(), "4") || StringUtils.equals(reportData.getBusinessType(), "8")) {
                        // 自动拼装key
                        String alarmKey = assembleAlarmKey(reportData.getDeviceCode(), reportData.getMid());
                        reportData.setAlarmKey(alarmKey);
                        reportDataList.add(reportData);
                    }
                }
            }
            log.info("dealDevAlarmMsg reportDataList:{}", JSON.toJSONString(reportDataList));
            if (CollectionUtils.isNotEmpty(reportDataList)) {

                // 保存设备告警逻辑
                saveDeviceAlarmLogic(reportDataList);
            }
            log.info("dealDevAlarmMsg信息记录完成, sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            log.error("AiotEnergyDeviceService dealDevAlarmMsg error:", e);
        }
    }

    private void saveDeviceAlarmLogic(List<EnergyDevAlarmReport> reportDataList) {
        if (CollectionUtils.isEmpty(reportDataList)) {
            return;
        }
        String collectionName = Constants.TABLE_ENERGY_DEVICE_ALARM_REPORT;
        List<String> alarmKeyList = reportDataList.stream().map(EnergyDevAlarmReport::getAlarmKey).distinct().collect(Collectors.toList());

        /*
         * 先根据deviceCode + mid 查询数据库数据
         *  1、如果数据库没数据，上报数据alarmFlag是开启，则插入；上报数据alarmFlag是关闭，则废弃
         *  2、如果数据库有数据，数据库中存在alarmFlag是开启状态的数据，上报数据是关闭，则更新；数据库中不存在alarmFlag是开启状态的数据，上报数据是开启，则新增；其他情况废弃
         */

        Query query = new Query();
        query.addCriteria(Criteria.where(ALARM_KEY).in(alarmKeyList));
        // alarmFlag 1-开始 0-结束
        query.addCriteria(Criteria.where(ALARM_FLAG).is("1"));
        List<EnergyDevAlarmReport> existList = mongoUtilDAO.queryMany(MongoDBType.PRIMARY, query,
                EnergyDevAlarmReport.class, collectionName);

        List<EnergyDevAlarmReport> insertList = Lists.newArrayList();
        List<EnergyDevAlarmReport> updateList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(existList)) {
            insertList = reportDataList.stream().filter(e -> "1".equals(e.getAlarmFlag())).collect(Collectors.toList());
        } else {
            List<String> existAlarmKeyList = existList.stream().map(EnergyDevAlarmReport::getAlarmKey).collect(Collectors.toList());
            for (EnergyDevAlarmReport reportData : reportDataList) {
                String alarmKey = reportData.getAlarmKey();
                String alarmFlag = reportData.getAlarmFlag();
                if (existAlarmKeyList.contains(alarmKey)) {
                    if (StringUtils.equals(alarmFlag, "0")) {
                        reportData.setAlarmClearTime(reportData.getAlarmTime());
                        updateList.add(reportData);
                    }
                } else {
                    if (StringUtils.equals(alarmFlag, "1")) {
                        insertList.add(reportData);
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(insertList)) {
            mongoUtilDAO.insertAll(reportDataList, collectionName, MongoDBType.PRIMARY);
        }

        if (CollectionUtils.isNotEmpty(updateList)) {
            for (EnergyDevAlarmReport updateData : updateList) {
                Update update = new Update();
                update.set(ALARM_FLAG, "0");
                update.set(ALARM_CLEAR_TIME, updateData.getAlarmClearTime());
                mongoUtilDAO.mongoUpdate(ALARM_KEY, updateData.getAlarmKey(), update, MongoDBType.PRIMARY, collectionName);
            }
        }
    }

    public void dealDevAlarmClearClMsg(List<String> messages) {
        try {
            Stopwatch sw = Stopwatch.createStarted();
            log.info("dealDevAlarmClearClMsg消费信息:{},size:{}", messages,messages.size());
            List<EnergyDevAlarmClearReport> reportDataList = Lists.newArrayList();
            for (String message : messages){
                List<EnergyDevAlarmClearReport> reportDataSubList = JSON.parseArray(message, EnergyDevAlarmClearReport.class);
                for (EnergyDevAlarmClearReport reportData : reportDataSubList) {
                    if (StringUtils.equals(reportData.getBusinessType(), "4") || StringUtils.equals(reportData.getBusinessType(), "8")) {
                        reportDataList.add(reportData);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(reportDataList)) {
                List<EnergyDevAlarmReport> handleDataList = reportDataList.stream().map(x -> {
                    EnergyDevAlarmReport handleData = new EnergyDevAlarmReport();
                    handleData.setDeviceCode(x.getDeviceCode());
                    handleData.setMid(x.getMid());
                    handleData.setAlarmTime(x.getClearTime());
                    handleData.setAlarmFlag("0");
                    handleData.setAlarmKey(assembleAlarmKey(x.getDeviceCode(), x.getMid()));
                    return handleData;
                }).collect(Collectors.toList());

                // 保存设备告警逻辑
                saveDeviceAlarmLogic(handleDataList);
            }
            log.info("dealDevAlarmClearClMsg信息记录完成, sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            log.error("AiotEnergyDeviceService dealDevAlarmClearClMsg error:", e);
        }
    }

    public void dealDeviceDataInfo(List<AiotEnergyDeviceReport> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        // Dtu设备不做同步，去除Dtu设备
        dataList = dataList.stream().filter(x -> (!StringUtils.isEmpty(x.getDevCode()))).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        Stopwatch sw = Stopwatch.createStarted();
        String uuid = UUID.randomUUID().toString();
        int preSize = dataList.size();
        // 根据deviceCode取最新的一条
        Map<String, AiotEnergyDeviceReport> dataMap = dataList.stream()
                .collect(Collectors.toMap(
                        AiotEnergyDeviceReport::getDevCode, // keyMapper: 从Record中提取code作为Map的键
                        Function.identity(), // valueMapper: Record对象本身作为值
                        (oldRecord, newRecord) -> (newRecord.getTs() >= oldRecord.getTs()) ? newRecord : oldRecord // mergeFunction: 冲突时选择时间戳最新的Record
                ));
        dataList = Lists.newArrayList(dataMap.values());
        int afterSize = dataList.size();
        log.info("dealDeviceDataInfo preSize:{} afterSize:{} diffSize:{} uuid:{}", preSize, afterSize, (preSize - afterSize), uuid);

        List<AiotEnergyDeviceReport> insertList = Lists.newArrayList();
        List<AiotEnergyDeviceReport> updateList = Lists.newArrayList();

        List<String> deviceCodeList = dataList.stream().map(AiotEnergyDeviceReport::getDevCode).distinct().collect(Collectors.toList());
        List<String> existDeviceCodeList = aiotEnergyDeviceDAO.queryDeviceCodeList(deviceCodeList);

        String deviceCode;
        for (AiotEnergyDeviceReport energyDeviceData : dataList) {
            deviceCode = energyDeviceData.getDevCode();
            if (!StringUtils.isEmpty(deviceCode)) {
                if (Objects.equals(energyDeviceData.getDeviceType(), 10)) {
                    energyDeviceData.setDeviceType(4);
                } else if (Objects.equals(energyDeviceData.getDeviceType(), 11)) {
                    energyDeviceData.setDeviceType(8);
                }
                //补充自己库表属性值
                this.setEnergyDeviceReportData(energyDeviceData);
                //运管删除该设备，应用平台同步更改状态为不可用
                if (Constants.DELETE.equals(energyDeviceData.getCanalType())) {
                    energyDeviceData.setDelFlag(1);
                    updateList.add(energyDeviceData);
                } else {
                    if (Objects.equals(1, energyDeviceData.getDelFlag())) {
                        energyDeviceData.setDelFlag(1);
                    } else {
                        energyDeviceData.setDelFlag(0);
                    }
                    // 数据库是否存在，存在则更新，不存在则新增
                    if (existDeviceCodeList.contains(deviceCode)) {
                        updateList.add(energyDeviceData);
                    } else {
                        insertList.add(energyDeviceData);
                    }
                }
            }
        }

        // 批量插入
        if (CollectionUtils.isNotEmpty(insertList)) {
            log.info("AiotEnergyDeviceService dealDeviceDataInfo insertList:{}", JSON.toJSONString(insertList));
            aiotEnergyDeviceDAO.insertDeviceBatch(insertList);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(updateList)) {
            log.info("AiotEnergyDeviceService dealDeviceDataInfo updateList:{}", JSON.toJSONString(updateList));
            aiotEnergyDeviceDAO.updateDeviceBatch(updateList);
        }
        log.info("AiotEnergyDeviceService dealDeviceDataInfo total sw:{}, uuid:{}", sw.elapsed(TimeUnit.MILLISECONDS), uuid);
    }

    public AiotEnergyDeviceReport setEnergyDeviceReportData(AiotEnergyDeviceReport energyDeviceData) {
        try {
            String longitude = energyDeviceData.getLongitude();
            String latitude = energyDeviceData.getLatitude();
            if (StringUtils.isEmpty(longitude) || StringUtils.isEmpty(latitude)) {
                return energyDeviceData;
            }
            PoiContraryGeocodingQueryRes regionInfo = deviceCommonUtils.getRegionInfoByLonAndLat(energyDeviceData.getLongitude(), energyDeviceData.getLatitude());
            if (regionInfo != null) {
                if (!StringUtils.isEmpty(regionInfo.getRegion().getProvinceCode())) {
                    energyDeviceData.setProvinceId(regionInfo.getRegion().getProvinceCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getRegion().getCityCode())) {
                    energyDeviceData.setCityId(regionInfo.getRegion().getCityCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getRegion().getCountyCode())) {
                    energyDeviceData.setCountyId(regionInfo.getRegion().getCountyCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getRegion().getTownCode())) {
                    energyDeviceData.setTownCode(regionInfo.getRegion().getTownCode());
                }
                if (!StringUtils.isEmpty(regionInfo.getAddress())) {
                    energyDeviceData.setLocation(regionInfo.getAddress());
                }
            }
            log.info("setEnergyDeviceReportData energyDeviceData:{}", JSON.toJSONString(energyDeviceData));
        } catch (Exception e) {
            log.error("setEnergyDeviceReportData error, e:", e);
        }
        return energyDeviceData;
    }

}
