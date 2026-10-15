package com.chinatower.platform.modules.statistics.service.impl;



import com.chinatower.event.constants.Constants;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.modules.statistics.service.IotConfigMongoTaskService;
import com.chinatower.platform.mongo.IotConfigFactoryMongoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class IotConfigMongoTaskServiceImpl implements IotConfigMongoTaskService {

    @Autowired
    IotConfigFactoryMongoMapper iotConfigFactoryMongoMapper;

    /**
     * 定时删除 MongoDB 中物联配置的累计值数据。
     * 删除条件：删掉 "mergeTime" 小于当前时间 24 小时之前的记录。
     *
     * @return 如果任务执行成功返回 true，执行失败返回 false
     */
    @Override
    public boolean iotConfigMongoFactoryDelete() {
        try {
            // 获取当前时间，并计算 24 小时前的时间
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime previousDateTime = currentDateTime.minusHours(24);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String endTime = previousDateTime.format(formatter);
            // 构建查询条件，筛选 "mergeTime" 小于 endTime 的数据
            Criteria criteria = new Criteria();
            criteria.and("mergeTime").lt(endTime);
            Query query = new Query();
            query.addCriteria(criteria);
            // 调用 MongoDB 的删除方法，删除符合条件的数据
            iotConfigFactoryMongoMapper.removeData(query, MongoDBType.PRIMARY, Constants.IOT_CONFIG_FACTORY_SUM_VALUE);
            log.info("物联配置累计值MongoDB定时删->执行任务成功");
            return true;
        } catch (Exception e) {
            log.error("物联配置累计值MongoDB定时删->执行任务失败:{}", e.getMessage(), e);
        }
        return false;
    }


}
