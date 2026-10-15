package com.chinatower.receive.core.alarm.common.service;


import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.ReceiveIotAlarmFactorDto;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.receive.mongodb.IotConfigMongoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/**
 * @Author: songxinfeng
 * @Date: 2023/11/28 17:20
 */
@Service
@Slf4j
public class CacheServiceImpl implements CacheService{

    private static final String CACHE_NAME  = "iotAlarm";
    @Autowired
    private IotConfigMongoMapper iotConfigMongoMapper;


    @Override
    public String getIotFactory(ReceiveIotAlarmFactorDto factorDto,String deviceCode,String alarmTime) {
        String key = factorDto.getConfigId()+"_"+factorDto.getFactorCode()+"_"+factorDto.getSumValue()+"_"+factorDto.getSumValueUnit()+"_"+deviceCode;
        log.info("cache load key = {}", key);
            //处理查询MongoDB累计值开始结束时间
            //0.配置id 1.因子编码 2.累加时间 3.累加时间单位 4.设备编码
            String[] split = key.split("_");
            //累计时间
            String sumValue = split[2];
            //累计时间单位
            String sumValueUnit = split[3];
            // 将累计时间值转为整数
            int value = Integer.parseInt(sumValue);

            // 获取上报告警时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime currentDateTime = LocalDateTime.parse(alarmTime, formatter);
            // 根据累计单位进行时间推移
            LocalDateTime resultDateTime = LocalDateTime.now();
            if (sumValueUnit.equals("day")) {
                resultDateTime = currentDateTime.minusDays(value);
            } else if (sumValueUnit.equals("hour")) {
                resultDateTime = currentDateTime.minusHours(value);
            } else if (sumValueUnit.equals("min")) {
                resultDateTime = currentDateTime.minusMinutes(value);
            }

            // 格式化时间输出
            String startTime = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String endTime = resultDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            Criteria criteria = new Criteria();
            criteria.and("deviceCode").is(split[4]);
            criteria.and("mergeTime").gte(endTime).lte(startTime);
            String mongoValue = iotConfigMongoMapper.findOne(criteria, MongoDBType.PRIMARY, Constants.IOT_CONFIG_FACTORY_SUM_VALUE, split[1]);
            log.info("cache load value = {}", mongoValue);
            return mongoValue;
    }

    @Override
    @CacheEvict(value=CACHE_NAME,allEntries = true)
    public void deleteCache() {
        try {
            log.info("删除缓存成功"+new Date());
        } catch (Exception e) {
            log.error("删除缓存失败:", e);
        }
    }
}
