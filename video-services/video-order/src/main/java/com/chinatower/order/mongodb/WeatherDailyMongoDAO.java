package com.chinatower.order.mongodb;

import com.alibaba.fastjson.JSON;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.order.WeatherDailyDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName WeatherDailyMongoDAO
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/31 11:02
 * @Version 1.0
 **/
@Component
@Slf4j
public class WeatherDailyMongoDAO extends AbstractBaseMongoDAO<WeatherDailyDTO> {

    public List<WeatherDailyDTO> findPageList(Query query, MongoDBType type, Class<?> entityClass, String collectionName){
        log.info("---------------------- BaseMongoDAOImpl.query start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.RECEIVE_PARAM_AND_COLLECTION_NAME, JSON.toJSON(query), collectionName);
        List result = this.getTemplate(type).find(query, entityClass, collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.COST_TIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.query end -----------------------");
        return result;
    }

    public <T> List<T> aggregate(Aggregation aggregation, MongoDBType type, Class<T> entityClass, String collectionName) {
        log.info("---------------------- BaseMongoDAOImpl.aggregate start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.RECEIVE_PARAM_AND_COLLECTION_NAME, JSON.toJSON(aggregation), collectionName);
        List<T> result = this.getTemplate(type).aggregate(aggregation, collectionName, entityClass).getMappedResults();
        long end = System.currentTimeMillis();
        log.info(Constants.COST_TIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.aggregate end -----------------------");
        return result;
    }
}
