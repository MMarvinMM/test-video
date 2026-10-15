package com.chinatower.platform.consumer.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.text.UUID;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.device.entity.vo.SensorAttrMapVO;
import com.chinatower.event.api.IReceiveAlarmService;
import com.chinatower.event.entity.vo.ReceiveIotAlarmVO;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.outdata.api.entity.Monitor;
import com.chinatower.platform.consumer.dao.MongoUtilDAO;
import com.chinatower.platform.consumer.dao.SensorAttributeMongoDAO;
import com.chinatower.platform.consumer.dao.SensorStatusDAO;
import com.chinatower.platform.consumer.item.Constants;
import com.chinatower.platform.consumer.item.SensorAttrVO;
import com.chinatower.platform.consumer.item.SensorAttributeReport;
import com.chinatower.receive.api.IReceiveAlarmNewService;
import com.google.common.base.Stopwatch;
import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 传感器属性上报
 * Created by zhujun on 2022/3/30.
 */
@Component
@Slf4j
public class SensorAttributeService {

    @Autowired
    private SensorAttributeMongoDAO<SensorAttributeReport> sensorAttributeMongoDAO;

    @Autowired
    private MongoUtilDAO<Object> mongoUtilDAO;

    @Autowired
    private IReceiveAlarmNewService receiveAlarmService;

    @Autowired
    private SensorStatusDAO sensorStatusDAO;

    private static final String DEVICE_CODE_MSG = "deviceCode";

    public void dealMsg(String sensorValue) {
        Stopwatch sw = Stopwatch.createStarted();
        log.info("传感器属性消费信息:{}", sensorValue);
        List<SensorAttributeReport> sensorAttrList = JSON.parseArray(sensorValue, SensorAttributeReport.class);
        if (!CollectionUtils.isEmpty(sensorAttrList)) {
            List<ReceiveIotAlarmVO> receiveIotAlarmVOList = new ArrayList<>();
            for (SensorAttributeReport sensorTemp : sensorAttrList) {
                if (sensorTemp.getMonitor() == null && sensorTemp.getMonitor().size() > 0) {
                    continue;
                }
                this.addReceiveAlarmInfo(sensorTemp, receiveIotAlarmVOList);
                Map<String, Object> monMap = new HashMap<>();
                monMap.putAll(sensorTemp.getMonitor());
                monMap.put(DEVICE_CODE_MSG, sensorTemp.getDeviceCode());
                monMap.put("monitorTime", sensorTemp.getMonitorTime());
                //不传该字段 默认为null--查实时
                if (StringUtils.isEmpty(sensorTemp.getTimeFrequency())){
                    sensorTemp.setTimeFrequency("null");
                }
                monMap.put("timeFrequency",sensorTemp.getTimeFrequency());
                String tableName = "";
                String allTableName = "";
                String timeFlag = sensorTemp.getMonitorTime();
                //null或"" 代表实时--插入原表  否则判断插入哪个表
                switch (sensorTemp.getTimeFrequency()){
                    case "1":
                        //按分查询--插入分钟累计表
                        tableName = Constants.TABLE_NAME_+Constants.MINUTE;
                        allTableName = Constants.TABLE_NAME_MINUTE_ALL;
                        timeFlag = timeFlag.substring(0,16);
                        break;
                    case "2":
                        //按小时查--插入小时累计表
                        tableName = Constants.TABLE_NAME_+Constants.HOUR;
                        allTableName = Constants.TABLE_NAME_HOUR_ALL;
                        timeFlag = timeFlag.substring(0,13);
                        break;
                    case "3":
                        //按天差询--插入按天累计表
                        tableName = Constants.TABLE_NAME_+Constants.DAY;
                        allTableName = Constants.TABLE_NAME_DAY_ALL;
                        timeFlag = timeFlag.substring(0,10);
                        break;
                    default:
                        //其他条件均为原表--实时
                        tableName = Constants.TABLE_NAME;
                        String dateYM = DateUtils.dateToStringYM(new Date());
                        //全量的信号量数据按月分表--实时
                        allTableName = Constants.TABLE_NAME_ALL_YYYYMM + dateYM;
                }
                Criteria criteria = Criteria.where(Constants.DEVICECODE).is(sensorTemp.getDeviceCode());
                Query query = Query.query(criteria);
                boolean flagExists = mongoUtilDAO.exists(query, MongoDBType.PRIMARY, tableName);
                if (flagExists) {
                    mongoUtilDAO.remove(query, MongoDBType.PRIMARY, tableName);
                }
                mongoUtilDAO.mongoInsert(monMap, MongoDBType.PRIMARY, tableName);
                //加入判断重复数据标识
                monMap.put(Constants.TIMEFLAG,timeFlag);
                mongoUtilDAO.mongoInsert(monMap,MongoDBType.PRIMARY, allTableName);
                //先检查索引是否已存在
                boolean listIndexs = checkListIndexs(allTableName, MongoDBType.PRIMARY);
                if (!listIndexs) {
                    try {
                        List<IndexModel> indexModels = getIndexModels();
                        mongoUtilDAO.mongoCreateIndexs(allTableName, MongoDBType.PRIMARY, indexModels);
                    } catch (Exception e) {
                        log.error("创建索引异常", e);
                    }
                }
            }
            if (!CollectionUtils.isEmpty(receiveIotAlarmVOList)) {
                boolean checkFlag = this.alarmReceiveBeforeCheck(receiveIotAlarmVOList);
                log.info("物联设备批量上报匹配校验入参:{}，校验结果:{}", JSON.toJSONString(receiveIotAlarmVOList),checkFlag);
                if (checkFlag) {
                    //调用物联设备批量上报接口
                    //log.info("调用物联设备批量上报接口信息:{}", sensorValue);
                    receiveAlarmService.receiveIotAlarm(receiveIotAlarmVOList);
                }
            }
        }
        log.info("传感器属性信息记录完成, sw:{}", sw.elapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * 调用物联告警上报前，对上报的告警信息进行匹配校验
     * 1、上报的物联设备是否是租户订购的
     * 2、有没有根据物联设备/物联因子配置阈值
     * @param receiveIotAlarmVOList
     * @return
     */
    private boolean alarmReceiveBeforeCheck(List<ReceiveIotAlarmVO> receiveIotAlarmVOList) {
        //获取物联设备编码集合
        List<String> deviceCodeList = receiveIotAlarmVOList.stream().map(ReceiveIotAlarmVO::getDeviceCode).distinct().collect(Collectors.toList());
        List<Map<String, Object>> customerVOList = sensorStatusDAO.getCustomersBySensorDeviceList(deviceCodeList);
        if(CollectionUtils.isEmpty(customerVOList)){
            return false;
        }
        //获取去重后的所有租户id,以及所有物联设备编码
        List<String> tenantIdList = customerVOList.stream().filter(item -> !StringUtils.isEmpty(item.get(Constants.PARAM_TENANT_ID).toString()))
                .map(item -> item.get(Constants.PARAM_TENANT_ID).toString()).distinct().collect(Collectors.toList());
        List<String> devCodeList = customerVOList.stream().filter(item -> !StringUtils.isEmpty(item.get(Constants.DEVICECODE).toString()))
                .map(item -> item.get(Constants.DEVICECODE).toString()).distinct().collect(Collectors.toList());
        Map<String, Object> param = new HashMap<>();
        param.put("deviceCodeList", devCodeList);
        param.put("tenantIdList", tenantIdList);
        //获取物联告警配置对应的配置规则
        Integer equipmentConfigCount = sensorStatusDAO.queryDeviceSensorMonitorCount(param);
        if (equipmentConfigCount <= 0) {
            return false;
        }
        return true;
    }

    private boolean checkListIndexs(String tableName, MongoDBType primary) {
        boolean success = false;
        try {
            ListIndexesIterable<Document> indexList = mongoUtilDAO.mongoListIndexs(tableName, primary);
            for (Document document : indexList) {
                Object key = document.get("key");
                if (null != key && key instanceof Document) {
                    Document keyDocument = (Document) key;
                    if (keyDocument.containsKey(Constants.INDEXFIELD_DEVICECODE)) {
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
        BasicDBObject index = new BasicDBObject();
        index.put(Constants.INDEXFIELD_DEVICECODE, 1);
        index.put(Constants.INDEXFIELD_MONITORTIME, 1);
        IndexOptions indexOptions = new IndexOptions().background(true).name("index_deviceCode_monitorTime");
        indexModels.add(new IndexModel(index, indexOptions));
        // 增加监控时间索引
        BasicDBObject monitorIndex = new BasicDBObject();
        monitorIndex.put(Constants.INDEXFIELD_MONITORTIME, 1);
        IndexOptions monitorOptions = new IndexOptions().background(true).name("index_monitorTime");
        indexModels.add(new IndexModel(monitorIndex, monitorOptions));
        // 增加温度索引，因数据中包含汉字"正常"的字符串，查询需要正则匹配纯数字
        BasicDBObject temperatureIndex = new BasicDBObject();
        temperatureIndex.put(Constants.INDEXFIELD_TEMPERATURE, 1);
        IndexOptions temperatureOptions = new IndexOptions().background(true).name("index_temperature");
        indexModels.add(new IndexModel(temperatureIndex, temperatureOptions));
        return indexModels;
    }


    /**
     * 组装告警上报的内容
     *
     * @param sensorAttributeReport
     * @param receiveIotAlarmVOList
     * @return
     */
    private void addReceiveAlarmInfo(SensorAttributeReport sensorAttributeReport, List<ReceiveIotAlarmVO> receiveIotAlarmVOList) {
        //物联设备编码为空的不上报
        if (StringUtils.isEmpty(sensorAttributeReport.getDeviceCode())) {
            return;
        }
        //kafka中物联设备检测因子值,只上传有检测值的因子,以因子编码为key,检测值为value组装
//        Map<String, Object> monitorMap = FieldUtils.getNonNullPropertyMap(sensorAttributeReport.getMonitor());
        Map<String, Object> monitorMap = new HashMap<>();
        monitorMap.putAll(sensorAttributeReport.getMonitor());
        //因子检测值都为空不上报
        if (MapUtils.isEmpty(monitorMap)) {
            return;
        }

        ReceiveIotAlarmVO vo = new ReceiveIotAlarmVO();
        vo.setMonitor(monitorMap);
        vo.setAlarmTime(sensorAttributeReport.getMonitorTime());
        vo.setDeviceCode(sensorAttributeReport.getDeviceCode());
        vo.setTimeFrequency(sensorAttributeReport.getTimeFrequency());
        receiveIotAlarmVOList.add(vo);
    }

    public List<Monitor> queryAttribute(String deviceCodes) {
        String[] deviceCode = deviceCodes.split(",");
        List<Monitor> attributeList = new ArrayList<>();
        List<String> deviceCodeList = Arrays.asList(deviceCode);
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DEVICECODE).in(deviceCodeList));
        List<SensorAttrVO> sensorAttrVOList = sensorAttributeMongoDAO.findList(query, MongoDBType.PRIMARY, SensorAttrVO.class, Constants.TABLE_NAME);
        if (!CollectionUtils.isEmpty(sensorAttrVOList)){
            List<String> existCodeList = sensorAttrVOList.stream().map(sensorAttrVO -> String.valueOf(sensorAttrVO.get(DEVICE_CODE_MSG))).collect(Collectors.toList());
            List<Monitor> monitorList = sensorStatusDAO.findDeviceInfo(existCodeList);
            if (!CollectionUtils.isEmpty(monitorList)){
                for (SensorAttrVO sensorAttrVO : sensorAttrVOList){
                    String code = String.valueOf(sensorAttrVO.get(DEVICE_CODE_MSG));
                    monitorList.forEach(monitor -> {
                        if (monitor.getDeviceCode().equals(code)){
                            sensorAttrVO.put("deviceName", monitor.getDeviceName());
                            sensorAttrVO.put("modelCode", monitor.getModelCode());
                            sensorAttrVO.put("deviceModelCode", monitor.getDeviceModelCode());
                            sensorAttrVO.put("longitude", monitor.getLongitude());
                            sensorAttrVO.put("latitude", monitor.getLatitude());
                            sensorAttrVO.put("location", monitor.getLocation());
                        }
                    });
                    attributeList.add(JSON.parseObject(JSON.toJSONString(sensorAttrVO), Monitor.class));
                }
            }
        }
        return attributeList;
    }

    public List<Map<String, Object>> queryAttributeMap(String deviceCodes) {
        String[] deviceCode = deviceCodes.split(",");
        List<Map<String, Object>> attributeList = new ArrayList<>();
        List<String> deviceCodeList = Arrays.asList(deviceCode);
        Query query = new Query();
        query.addCriteria(Criteria.where(Constants.DEVICECODE).in(deviceCodeList));
        List<SensorAttrVO> sensorAttrVOList = sensorAttributeMongoDAO.findList(query, MongoDBType.PRIMARY, SensorAttrVO.class, Constants.TABLE_NAME);
        if (!CollectionUtils.isEmpty(sensorAttrVOList)){
            List<String> existCodeList = sensorAttrVOList.stream().map(sensorAttrVO -> String.valueOf(sensorAttrVO.get(DEVICE_CODE_MSG))).collect(Collectors.toList());
            List<Monitor> monitorList = sensorStatusDAO.findDeviceInfo(existCodeList);
            if (!CollectionUtils.isEmpty(monitorList)){
                for (SensorAttrVO sensorAttrVO : sensorAttrVOList){
                    String code = String.valueOf(sensorAttrVO.get(DEVICE_CODE_MSG));
                    monitorList.forEach(monitor -> {
                        if (monitor.getDeviceCode().equals(code)){
                            sensorAttrVO.put("deviceName", monitor.getDeviceName());
                            sensorAttrVO.put("modelCode", monitor.getModelCode());
                            sensorAttrVO.put("deviceModelCode", monitor.getDeviceModelCode());
                            sensorAttrVO.put("longitude", monitor.getLongitude());
                            sensorAttrVO.put("latitude", monitor.getLatitude());
                            sensorAttrVO.put("location", monitor.getLocation());
                        }
                    });
                    attributeList.add(sensorAttrVO);
                }
            }
        }
        return attributeList;
    }



}
