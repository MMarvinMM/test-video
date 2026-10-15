package com.chinatower.order.mongodb;

import com.alibaba.fastjson.JSON;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.order.dto.WeatherAirHourDailyDTO;
import com.chinatower.platform.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName WeatherAirDailyMongoDAO
 * @Deacription
 * @Author dencycheng
 * @Date 2022/11/9
 * @Version 1.0
 **/
@Component
@Slf4j
public class WeatherAirHourDailyMongoDAO extends AbstractBaseMongoDAO<WeatherAirHourDailyDTO> {

    public List<WeatherAirHourDailyDTO> findPageList(Query query, MongoDBType type, Class<?> entityClass, String collectionName){
        log.info("---------------------- BaseMongoDAOImpl.query start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.RECEIVE_PARAM_AND_COLLECTION_NAME, JSON.toJSON(query), collectionName);
        List result = this.getTemplate(type).find(query, entityClass, collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.COST_TIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.query end -----------------------");
        return result;
    }
}
