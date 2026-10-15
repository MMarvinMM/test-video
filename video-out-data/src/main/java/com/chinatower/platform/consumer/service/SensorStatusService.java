package com.chinatower.platform.consumer.service;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.Region;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.outdata.api.entity.WarningTargetVO;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.consumer.dao.MongoUtilDAO;
import com.chinatower.platform.consumer.dao.SensorAttributeMongoDAO;
import com.chinatower.platform.consumer.dao.SensorStatusDAO;
import com.chinatower.platform.consumer.dao.VmOrderSensorDevicesDAO;
import com.chinatower.platform.consumer.item.*;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 传感器状态上报
 * Created by zhujun on 2022/3/30.
 */
@Slf4j
@Component
public class SensorStatusService {

    @Autowired
    private SensorAttributeMongoDAO sensorAttributeMongoDAO;
    @Autowired
    private SensorStatusDAO sensorStatusDAO;

    @Autowired
    private MongoUtilDAO mongoUtilDAO;

    @Autowired
    private GisMapApi gisMapApi;

    @Autowired
    private VmOrderSensorDevicesDAO vmOrderSensorDevicesDAO;

    private static final String YYYYMM_FORMAT = "yyyyMM";

    public static final String TENANT_ID = "tenantId";
    public static final String INDUSTRY_CODE = "industryCode";
    public static final String ALARM_TIME = "alarmTime";

    //根据经纬度 补全 省市区乡镇编码
    public SensorStatusReport setSensorStatusReportData(SensorStatusReport sensorStatusReport) {
        try {
            if (DeviceConstants.IOT_DEVICE_RADAR_3.equals(sensorStatusReport.getDeviceType())) {
                sensorStatusReport.setDeviceType(DeviceConstants.IOT_DEVICE_RADAR);
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
                if (!org.springframework.util.StringUtils.isEmpty(region.getProvinceCode())) {
                    sensorStatusReport.setProvinceId(region.getProvinceCode());
                }
                if (!org.springframework.util.StringUtils.isEmpty(region.getCityCode())) {
                    sensorStatusReport.setCityId(region.getCityCode());
                }
                if (!org.springframework.util.StringUtils.isEmpty(region.getCountyCode())) {
                    sensorStatusReport.setCountyId(region.getCountyCode());
                }
                if (!org.springframework.util.StringUtils.isEmpty(region.getTownCode())) {
                    sensorStatusReport.setTownCode(region.getTownCode());
                }
                if (!org.springframework.util.StringUtils.isEmpty(region.getTown())) {
                    sensorStatusReport.setTownName(region.getTown());
                }
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
        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(result.getData())) {
            try {
                PoiContraryGeocodingQueryRes data = result.getData().get(0);
                region = data.getRegion();
                if (region != null) {
                    RedisUtil.set(redisKey, region, 60, TimeUnit.MINUTES, RedisDBType.PRIMARY);
                }
            } catch (Exception e) {
                log.error("getGisRegion gisMapApi poiContraryGeocodingQuery error, e:", e);
            }
        }
        return region;
    }

    //传感器状态跟新
    public void dealMonitorInfo(SensorStatusReport sensorStatusReport, String tableTime) {
        //传感器状态更新
    }


    /**
     * 未使用--删除
     * @param data
     */
    public void dealSensorData(List<SensorStatusReport> data) {
        for (SensorStatusReport sensorStatusReport : data) {
            if (!StringUtils.isEmpty(sensorStatusReport.getDeviceCode())) {
                //补充自己库表属性值
                this.setSensorStatusReportData(sensorStatusReport);
                int hasDevice = sensorStatusDAO.queryDevice(sensorStatusReport.getDeviceCode());
                if (hasDevice > 0) {
                    sensorStatusDAO.updateDevice(sensorStatusReport);
                } else {
                    sensorStatusDAO.addDevice(sensorStatusReport);
                }
                String tableTime = DateUtil.format(new Date(), YYYYMM_FORMAT);
                sensorAttributeMongoDAO.mongoInsert(sensorStatusReport, MongoDBType.PRIMARY, Constants.TABLE_NAME_STATUS + tableTime);
                this.dealMonitorInfo(sensorStatusReport, tableTime);
            }
        }
    }

    public void dealWarningTargetMsg(String message) {
        log.info("雷达告警目标消费信息:{}", message);
        List<WarningTargetReport> warningTargetReportList = JSON.parseArray(message, WarningTargetReport.class);
        this.dealWarningTargetData(warningTargetReportList);
    }

    public void dealWarningTargetData(List<WarningTargetReport> warningTargetReportList) {
        List<String> taskIdList = warningTargetReportList.stream().map(WarningTargetReport::getTaskId).filter(Objects::nonNull).collect(Collectors.toList());
        // 雷达模版信息列表
        Map<String, WarningTargetRadarTemplateDTO> taskTenantIdMap = vmOrderSensorDevicesDAO.selectRadarTemplateInfoByTaskIdList(taskIdList)
                .stream().collect(Collectors.toMap(WarningTargetRadarTemplateDTO::getTaskId, (p) -> p, (a, b) -> a));
        List<WarningTargetVO> insertList = Lists.newArrayList();
        // 任务编码
        String taskId;
        for (WarningTargetReport warningTargetReportData : warningTargetReportList) {
            taskId = warningTargetReportData.getTaskId();
            if (!StringUtils.isEmpty(taskId) && taskTenantIdMap.containsKey(taskId)) {
                WarningTargetVO warningTargetData = new WarningTargetVO();
                warningTargetData.setAlarmTime(warningTargetReportData.getAlarmTime());
                warningTargetData.setAlarmCode(warningTargetReportData.getAlarmCode());
                warningTargetData.setTaskId(taskId);
                warningTargetData.setTargetId(warningTargetReportData.getExtendField() != null ? warningTargetReportData.getExtendField().getTargetId() : null);
                WarningTargetRadarTemplateDTO radarTemplateInfo = taskTenantIdMap.get(taskId);
                if (radarTemplateInfo != null) {
                    warningTargetData.setTenantId(radarTemplateInfo.getTenantId());
                    warningTargetData.setIndustryCode(radarTemplateInfo.getIndustryCode());
                }
                insertList.add(warningTargetData);
            }
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            sensorAttributeMongoDAO.insertAll(insertList, Constants.TABLE_NAME_RADAR_WARNING_TARGET, MongoDBType.PRIMARY);
        }
    }

    /**
     * 查询雷达告警目标信息
     * @return
     */
    public Result<List<WarningTargetVO>> queryWarningTargetInfoList() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return Result.fail("获取登录信息失败");
        }
        int highlightDuration = 300;
        try {
            SysConfig sysConfig = sensorStatusDAO.getSysConfig(Constants.HIGHLIGHT_DURATION);
            if (sysConfig != null && !StringUtils.isEmpty(sysConfig.getConfigValue())) {
                highlightDuration = Integer.parseInt(sysConfig.getConfigValue());
            }
        } catch (Exception e) {
            log.error("SensorStatusService queryWarningTargetInfoList 获取高亮持续时间参数配置失败, e:", e);
        }

        DateTime searchAlarmTime = DateUtil.offset(DateUtil.date(), DateField.SECOND, -highlightDuration);
        String searchAlarmTimeStr = searchAlarmTime.toString();

        String tenantId = loginUser.getUser().getTenantId();
        String industryCode = loginUser.getUser().getIndustryCode();
        Query removeQuery = new Query();
        removeQuery.addCriteria(Criteria.where(ALARM_TIME).lt(searchAlarmTimeStr));
        mongoUtilDAO.remove(removeQuery, MongoDBType.PRIMARY, Constants.TABLE_NAME_RADAR_WARNING_TARGET);

        Query query = new Query();
        query.addCriteria(Criteria.where(ALARM_TIME).gte(searchAlarmTimeStr));
        query.addCriteria(Criteria.where(TENANT_ID).is(tenantId));
        query.addCriteria(Criteria.where(INDUSTRY_CODE).is(industryCode));
        List<WarningTargetVO> resultList = mongoUtilDAO.queryMany(MongoDBType.PRIMARY, query, WarningTargetVO.class, Constants.TABLE_NAME_RADAR_WARNING_TARGET);
        log.info("SensorStatusService queryWarningTargetInfoList highlightDuration:{} searchAlarmTime:{} resultList:{}",
                highlightDuration, searchAlarmTimeStr, JSON.toJSONString(resultList));
        return Result.data(resultList);
    }

    /**
     * 传感器文件信息
     * @param messages
     */
    public void dealSensorFileMsg(List<String> messages) {
        log.info("dealSensorFileMsg消费信息:{}, size:{}", messages, messages.size());
        List<SensorFileReport> dataList = Lists.newArrayList();
        for (String message : messages) {
            SensorFileReport sensorFile = JSON.parseObject(message, SensorFileReport.class);
            if (sensorFile != null) {
                dataList.add(sensorFile);
            }
        }
        if (CollectionUtils.isNotEmpty(dataList)) {
            sensorAttributeMongoDAO.insertAll(dataList, Constants.TABLE_NAME_SENSOR_FILE, MongoDBType.PRIMARY);
        }
    }

    /**
     * 传感器告警信息
     * @param messages
     */
    public void dealSensorEventMsg(List<String> messages) {
        log.info("dealSensorEventMsg消费信息:{}, size:{}", messages, messages.size());
        List<SensorEventReport> dataList = Lists.newArrayList();
        for (String message : messages) {
            SensorEventReport sensorEvent = JSON.parseObject(message, SensorEventReport.class);
            if (sensorEvent != null) {
                dataList.add(sensorEvent);
            }
        }
        if (CollectionUtils.isNotEmpty(dataList)) {
            sensorAttributeMongoDAO.insertAll(dataList, Constants.TABLE_NAME_SENSOR_EVENT, MongoDBType.PRIMARY);
        }
    }
}
