package com.chinatower.device.mongodb;

import com.alibaba.fastjson.JSON;
import com.chinatower.device.dto.EnergyDeviceAlarmDTO;
import com.chinatower.device.entity.dto.EnergyDeviceAlarmQueryReq;
import com.chinatower.device.entity.dto.EnergyDeviceAlarmStatisticsQueryReq;
import com.chinatower.device.entity.dto.EnergyDeviceMonitorQueryReq;
import com.chinatower.device.entity.vo.EnergyDeviceAlarmVO;
import com.chinatower.device.entity.vo.EnergyDeviceStationVO;
import com.chinatower.device.entity.vo.EnergyMonitorDataMapVO;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EnergyDeviceMongoDAO {

    private static final String MONITOR_TIME = "monitorTime";
    private static final String DEVICE_CODE = "deviceCode";
    private static final String DEVICE_NAME = "deviceName";
    private static final String DEVICE_TYPE = "deviceType";

    private static final String ALARM_TIME = "alarmTime";
    private static final String BUSINESS_TYPE = "businessType";
    private static final String ALARM_LEVEL = "alarmLevel";
    private static final String ALARM_FLAG = "alarmFlag";
    private static final String ALARM_NAME = "alarmName";

    private static final String TABLE_ENERGY_DEVICE_PERF_REPORT = "energy_device_perf_report";
    private static final String TABLE_ENERGY_DEVICE_PERF_REPORT_ALL = "energy_device_perf_report_all";
    private static final String TABLE_ENERGY_DEVICE_ALARM_REPORT = "energy_device_alarm_report";

    @Autowired
    private SensorAttrValueMongoDAO<Object> sensorAttrValueMongoDAO;

    public Page<EnergyMonitorDataMapVO> pageMonitorDataList(EnergyDeviceMonitorQueryReq param){
        Page<EnergyMonitorDataMapVO> energyMonitorDataPage = new Page<>();
        Query query = new Query();
        if (StringUtils.isNotBlank(param.getStartTime()) && StringUtils.isNotBlank(param.getEndTime())) {
            query.addCriteria(Criteria.where(MONITOR_TIME).gte(param.getStartTime()).lte(param.getEndTime()));
        }
        if (StringUtils.isNotBlank(param.getDevCode())) {
            query.addCriteria(Criteria.where(DEVICE_CODE).is(param.getDevCode()));
        }
        if (StringUtils.isNotBlank(param.getBusinessType())) {
            query.addCriteria(Criteria.where(DEVICE_TYPE).is(param.getBusinessType()));
        }

        String collectionName = TABLE_ENERGY_DEVICE_PERF_REPORT_ALL;
        query.with(Sort.by(Sort.Direction.DESC, MONITOR_TIME));
        energyMonitorDataPage.setCount(sensorAttrValueMongoDAO.getTemplateInfo(MongoDBType.PRIMARY).count(query, EnergyMonitorDataMapVO.class,
                collectionName));

        log.info("pageMonitorDataList count receive param: {}, collectionName: {}", JSON.toJSON(query), collectionName);
        PageRequest pageRequest = PageRequest.of((param.getPageNum() - 1), param.getPageSize(),Sort.by(Sort.Direction.DESC, MONITOR_TIME));
        query.with(pageRequest);
        log.info("pageMonitorDataList list receive param: {}, collectionName: {}", JSON.toJSON(query), collectionName);
        List<EnergyMonitorDataMapVO> attrValues = sensorAttrValueMongoDAO.getTemplateInfo(MongoDBType.PRIMARY).find(query, EnergyMonitorDataMapVO.class,
                collectionName);
        energyMonitorDataPage.setList(attrValues);
        return energyMonitorDataPage;
    }

    public Page<EnergyDeviceAlarmVO> pageDeviceAlarmDataList(EnergyDeviceAlarmQueryReq param, List<String> deviceCodeList){

        Page<EnergyDeviceAlarmVO> energyMonitorDataPage = new Page<>();
        Query query = new Query();
        if (StringUtils.isNotBlank(param.getStartTime()) && StringUtils.isNotBlank(param.getEndTime())) {
            query.addCriteria(Criteria.where(ALARM_TIME).gte(param.getStartTime()).lte(param.getEndTime()));
        }
//        if (StringUtils.isNotBlank(param.getDevCode())) {
////            query.addCriteria(Criteria.where(DEVICE_CODE).is(param.getDevCode()));
//            query.addCriteria(Criteria.where(DEVICE_CODE).regex(".*?\\" +param.getDevCode()+ ".*"));
//        }
        if (StringUtils.isNotBlank(param.getDevName())) {
//            query.addCriteria(Criteria.where(DEVICE_NAME).is(param.getDevName()));
            query.addCriteria(Criteria.where(DEVICE_NAME).regex(".*" +param.getDevName()+ ".*"));
        }
        if (StringUtils.isNotBlank(param.getAlarmName())) {
//            query.addCriteria(Criteria.where(ALARM_NAME).is(param.getAlarmName()));
            query.addCriteria(Criteria.where(ALARM_NAME).regex(".*" +param.getAlarmName()+ ".*"));
        }
        if (StringUtils.isNotBlank(param.getAlarmLevel())) {
            query.addCriteria(Criteria.where(ALARM_LEVEL).is(param.getAlarmLevel()));
        }
        if (StringUtils.isNotBlank(param.getAlarmStatus())) {
            query.addCriteria(Criteria.where(ALARM_FLAG).is(param.getAlarmStatus()));
        }
        if (CollectionUtils.isNotEmpty(deviceCodeList)) {
            query.addCriteria(Criteria.where(DEVICE_CODE).in(deviceCodeList));
        }

        if (StringUtils.isNotBlank(param.getBusinessType())) {
            query.addCriteria(Criteria.where(BUSINESS_TYPE).is(param.getBusinessType()));
        }

        query.with(Sort.by(Sort.Direction.DESC, ALARM_TIME));
        energyMonitorDataPage.setCount(sensorAttrValueMongoDAO.getTemplateInfo(MongoDBType.PRIMARY).count(query, EnergyDeviceAlarmDTO.class,
                TABLE_ENERGY_DEVICE_ALARM_REPORT));

        PageRequest pageRequest = PageRequest.of((param.getPageNum() - 1), param.getPageSize(),Sort.by(Sort.Direction.DESC, ALARM_TIME));
        query.with(pageRequest);
        List<EnergyDeviceAlarmDTO> attrValues = sensorAttrValueMongoDAO.getTemplateInfo(MongoDBType.PRIMARY).find(query, EnergyDeviceAlarmDTO.class,
                TABLE_ENERGY_DEVICE_ALARM_REPORT);

        /**
         * private String alarmName;
         *     private String alarmLevel;
         *     private String alarmStatus;
         *     private String alarmType;
         *     private String alarmDesc;
         *     private String businessType;
         *     private String provinceId;
         *     private String provinceName;
         *     private String cityId;
         *     private String cityName;
         *     private String countyId;
         *     private String countyName;
         *     private String devCode;
         *     private String devName;
         *     private String devType;
         *     private String mid;
         *     private String midName;
         *     private String reportDevCode;
         *     private String reportDevName;
         *     private String firstAlarmTime;
         */

        List<EnergyDeviceAlarmVO> dataList = attrValues.stream().map(x -> {
            EnergyDeviceAlarmVO data = new EnergyDeviceAlarmVO();
            data.setAlarmName(x.getAlarmName());
            data.setAlarmLevel(x.getAlarmLevel());
            data.setAlarmStatus(x.getAlarmFlag());
            data.setAlarmType(x.getSource());
            data.setAlarmDesc(x.getAlarmDesc());
            data.setDevType(x.getDeviceType());
            data.setMid(x.getMid());
            data.setMidValue(x.getAlarmValue());
            data.setBusinessType(x.getBusinessType());
            data.setProvinceId(x.getCrewProvinceId());
            data.setProvinceName(x.getCrewProvinceName());
            data.setCityId(x.getCrewCityId());
            data.setCityName(x.getCrewCityName());
            data.setCountyId(x.getCrewCountyId());
            data.setCountyName(x.getCrewCountyName());
            data.setDevCode(x.getDeviceCode());
            data.setDevName(x.getDeviceName());

            data.setReportDevCode(x.getDeviceCode());
            data.setReportDevName(x.getDeviceName());
            data.setFirstAlarmTime(x.getAlarmTime());
            return data;
        }).collect(Collectors.toList());

        energyMonitorDataPage.setList(dataList);
        return energyMonitorDataPage;
    }

    public Long countDeviceAlarm(EnergyDeviceAlarmStatisticsQueryReq param, List<String> deviceCodeList){
        Query query = new Query();
        if (StringUtils.isNotBlank(param.getStartTime()) && StringUtils.isNotBlank(param.getEndTime())) {
            query.addCriteria(Criteria.where(ALARM_TIME).gte(param.getStartTime()).lte(param.getEndTime()));
        }
        if (StringUtils.isNotBlank(param.getAlarmStatus())) {
            query.addCriteria(Criteria.where(ALARM_FLAG).is(param.getAlarmStatus()));
        }
        if (CollectionUtils.isNotEmpty(deviceCodeList)) {
            query.addCriteria(Criteria.where(DEVICE_CODE).in(deviceCodeList));
        }
        if (StringUtils.isNotBlank(param.getBusinessType())) {
            query.addCriteria(Criteria.where(BUSINESS_TYPE).is(param.getBusinessType()));
        }
        query.with(Sort.by(Sort.Direction.DESC, ALARM_TIME));
        return sensorAttrValueMongoDAO.getTemplateInfo(MongoDBType.PRIMARY).count(query, EnergyDeviceAlarmDTO.class, TABLE_ENERGY_DEVICE_ALARM_REPORT);
    }

}
