package com.chinatower.sync.consumer.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.entity.vo.ReceiveIotAlarmVO;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.receive.api.IReceiveAlarmNewService;
import com.chinatower.sync.consumer.dao.MongoUtilDAO;
import com.chinatower.sync.consumer.dao.SensorAttributeMongoDAO;
import com.chinatower.sync.consumer.dao.SensorStatusDAO;
import com.chinatower.sync.consumer.item.Constants;
import com.chinatower.sync.consumer.item.SensorAttributeReport;
import com.chinatower.sync.consumer.util.MongoHashUtil;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
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
    private MongoUtilDAO<Map<String, Object>> mongoUtilDAO;

    @Autowired
    private IReceiveAlarmNewService receiveAlarmService;

    @Autowired
    private SensorStatusDAO sensorStatusDAO;

    @Autowired
    private MongoHashUtil mongoHashUtil;


    public void dealMsg(List<String> sensorValues, boolean isAIot) {
        Stopwatch sw = Stopwatch.createStarted();
        log.info("传感器属性消费信息:{},size:{}", sensorValues,sensorValues.size());
        List<SensorAttributeReport> sensorAttrList = Lists.newArrayList();
        for (String sensorValue : sensorValues){
            List<SensorAttributeReport> attrList = JSON.parseArray(sensorValue, SensorAttributeReport.class);
            sensorAttrList.addAll(attrList);
        }

        if (!CollectionUtils.isEmpty(sensorAttrList)) {
            List<ReceiveIotAlarmVO> receiveIotAlarmVOList = new ArrayList<>();
            List<Map<String, Object>> minuteSensorTempList = new ArrayList<>();
            List<Map<String, Object>> hourSensorTempList = new ArrayList<>();
            List<Map<String, Object>> daySensorTempList = new ArrayList<>();
            List<Map<String, Object>> sensorTempList = new ArrayList<>();
            for (SensorAttributeReport sensorTemp : sensorAttrList) {
                if (sensorTemp.getMonitor() == null && sensorTemp.getMonitor().size() > 0) {
                    continue;
                }

                if (isAIot && ("10000000001".equals(sensorTemp.getAccessNode()) || StringUtils.isEmpty(sensorTemp.getAccessNode()))) {
                    log.info("SensorAttributeService aIot dealMsg deviceCode:{}, accessNode:{}, monitorTime:{}", sensorTemp.getDeviceCode(), sensorTemp.getAccessNode(), sensorTemp.getMonitorTime());
                }

                this.addReceiveAlarmInfo(sensorTemp, receiveIotAlarmVOList);
                Map<String, Object> monMap = new HashMap<>();
                monMap.putAll(sensorTemp.getMonitor());
                monMap.put("deviceCode", sensorTemp.getDeviceCode());
                monMap.put("monitorTime", sensorTemp.getMonitorTime());
                //不传该字段 默认为null--查实时
                if (StringUtils.isEmpty(sensorTemp.getTimeFrequency())){
                    sensorTemp.setTimeFrequency("null");
                }
                monMap.put("timeFrequency",sensorTemp.getTimeFrequency());
                //null或"" 代表实时--插入原表  否则判断插入哪个表
                switch (sensorTemp.getTimeFrequency()){
                    case "1":
                        //按分查询--插入分钟累计表
                        minuteSensorTempList.add(monMap);
                        break;
                    case "2":
                        //按小时查--插入小时累计表
                        hourSensorTempList.add(monMap);
                        break;
                    case "3":
                        //按天差询--插入按天累计表
                        daySensorTempList.add(monMap);
                        break;
                    default:
                        //其他条件均为原表--实时
                        sensorTempList.add(monMap);
                }
            }
            if (!CollectionUtils.isEmpty(minuteSensorTempList)){
                this.dealSensorTempList(minuteSensorTempList,Constants.TABLE_NAME_+Constants.MINUTE
                        ,Constants.TABLE_NAME_MINUTE_ALL,16);
            }
            if (!CollectionUtils.isEmpty(hourSensorTempList)){
                this.dealSensorTempList(hourSensorTempList,Constants.TABLE_NAME_+Constants.HOUR
                        ,Constants.TABLE_NAME_HOUR_ALL,13);
            }
            if (!CollectionUtils.isEmpty(daySensorTempList)){
                this.dealSensorTempList(daySensorTempList,Constants.TABLE_NAME_+Constants.DAY
                        ,Constants.TABLE_NAME_DAY_ALL,10);
            }
            if (!CollectionUtils.isEmpty(sensorTempList)){
                String dateYM = DateUtils.dateToStringYM(new Date());
                String collectionName = Constants.TABLE_NAME_ALL_YYYYMM + dateYM;
                this.dealSensorTempList(sensorTempList,Constants.TABLE_NAME
                        , collectionName,-1);

                // 只有中心节点才分片
                SysConfig sysConfig = sensorStatusDAO.getSysConfig(Constants.DEPENDENCE_MANAGE);
                if (sysConfig != null && Constants.ONE.equals(sysConfig.getConfigValue())) {
                    // 创建hash索引
                    mongoHashUtil.createHashedIndexIfNeed(collectionName, Constants.DEVICECODE, "idx_deviceCode_hash");
                    // 处理分片
                    mongoHashUtil.shardCollectionIfNeed(collectionName, Constants.DEVICECODE);
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

    private void dealSensorTempList(List<Map<String,Object>> mapList,String collectionName,String allCollectionName,int end){

        List<String> deviceCodeList = mapList.stream().map(stringObjectMap -> String.valueOf(stringObjectMap.get("deviceCode"))).collect(Collectors.toList());
        Criteria criteria = Criteria.where(Constants.DEVICECODE).in(deviceCodeList);
        Query query = Query.query(criteria);
        boolean flagExists = mongoUtilDAO.exists(query, MongoDBType.PRIMARY, collectionName);
        if (flagExists) {
            mongoUtilDAO.remove(query, MongoDBType.PRIMARY, collectionName);
        }
        mongoUtilDAO.insertAll(mapList, collectionName,MongoDBType.PRIMARY);
        //加入重复标识后再添加到全量表--实时数据不加重复标识
        if (end != -1){
            for (Map<String,Object> map : mapList){
                String monitorTime = String.valueOf(map.get("monitorTime"));
                String timeFlag = monitorTime.substring(0,end);
                //加入判断重复数据标识
                map.put(Constants.TIMEFLAG,timeFlag);
            }
        }
        mongoUtilDAO.insertAll(mapList,allCollectionName,MongoDBType.PRIMARY);
        //先检查索引是否已存在
        boolean listIndexs = checkListIndexs(allCollectionName, MongoDBType.PRIMARY);
        if (!listIndexs) {
            try {
                List<IndexModel> indexModels = getIndexModels();
                mongoUtilDAO.mongoCreateIndexs(allCollectionName, MongoDBType.PRIMARY, indexModels);
            } catch (Exception e) {
                log.error("创建索引异常", e);
            }
        }
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
}
