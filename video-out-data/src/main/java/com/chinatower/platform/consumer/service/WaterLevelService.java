package com.chinatower.platform.consumer.service;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.consumer.dao.MongoUtilDAO;
import com.chinatower.platform.consumer.item.WaterLevelVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

/**
 * @ClassName: WaterLevelService
 * @Author: wangyt
 * @CreateTime: 2024-12-13  22:27
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@Component
public class WaterLevelService {

    @Autowired
    private MongoUtilDAO<Object> mongoUtilDAO;

    private static final String DEVICECODE = "deviceCode";
    private static final String REPORTTIME = "reportTime";
    private static final String MSGID = "msgid";
    private static final String WATERLEVELINDEX = "waterLevelIndex";
    private static final String WATERLEVEL = "waterLevel";
    private static final String PICADDR = "picAddr";
    private static final String WATER_LEVEL_LATEST = "water_level_latest";

    public void saveWaterLevelData(String message) {
        log.info("saveWaterLevelData消费的message为:{}",message);
        // 先保存到水位的最新数据表
        try {
            JSONObject messageJsonData = JSONObject.parseObject(message);
            String deviceCode = messageJsonData.getString(DEVICECODE);
            String reportTime = messageJsonData.getString(REPORTTIME);
            String msgId = messageJsonData.getString(MSGID);
            JSONObject waterLevelIndexJsonData = messageJsonData.getJSONObject(WATERLEVELINDEX);
            String waterLevel = waterLevelIndexJsonData.getString(WATERLEVEL);
            String picAddr = waterLevelIndexJsonData.getString(PICADDR);
            WaterLevelVO waterLevelDataMap = new WaterLevelVO();
            waterLevelDataMap.setDeviceCode(deviceCode);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            waterLevelDataMap.setGatherTime(sdf.parse(reportTime));
            waterLevelDataMap.setMsgid(msgId);
            waterLevelDataMap.setWaterLevel(waterLevel);
            waterLevelDataMap.setPicAddr(picAddr);
            Criteria criteria = Criteria.where(DEVICECODE).is(waterLevelDataMap.getDeviceCode());
            Query query = Query.query(criteria);
            mongoUtilDAO.remove(query, MongoDBType.PRIMARY, WATER_LEVEL_LATEST);
            mongoUtilDAO.mongoInsert(waterLevelDataMap, MongoDBType.PRIMARY, WATER_LEVEL_LATEST);
        }catch (Exception e){
            log.error("saveWaterLevelData异常:{}",e);
        }
    }
}
