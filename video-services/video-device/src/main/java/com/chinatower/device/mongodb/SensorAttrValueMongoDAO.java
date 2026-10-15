package com.chinatower.device.mongodb;

import com.alibaba.fastjson.JSON;

import com.chinatower.device.entity.vo.SensorAttrMapVO;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.exception.TowerMongodbException;
import com.chinatower.platform.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName SensorAttrValueMongoDAO
 * @Deacription
 * @Author zheng.yan
 * @Date 2022/5/10
 * @Version 1.0
 **/
@Slf4j
@Component
public class SensorAttrValueMongoDAO<T>{

    @Autowired
    @Qualifier("primaryMongoTemplate")
    private MongoTemplate primaryMongoTemplate;
    @Autowired(
            required = false
    )
    @Qualifier("secondaryMongoTemplate")
    private MongoTemplate secondaryMongoTemplate;

    private MongoTemplate getTemplate(MongoDBType type) {
        if (type == null) {
            throw new TowerMongodbException("MongoDB数据源为空!");
        } else {
            switch(type) {
                case PRIMARY:
                    return this.primaryMongoTemplate;
                case SECONDARY:
                    return this.secondaryMongoTemplate;
                default:
                    return this.primaryMongoTemplate;
            }
        }
    }

    public MongoTemplate getTemplateInfo(MongoDBType type){
        return this.getTemplate(type);
    }

    public List<SensorAttrMapVO> findList(Query query, MongoDBType type, Class<?> entityClass, String collectionName) {
        log.info("---------------------- BaseMongoDAOImpl.query start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.RECEIVE_PARAM_AND_COLLECTION_NAME, JSON.toJSON(query), collectionName);
        List result = this.getTemplate(type).find(query, entityClass, collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.COST_TIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.query end -----------------------");
        return result;
    }

    public Map<String,String> mongoInsert(Map<String,String> entity, MongoDBType type, String collectionName) {
        log.info("---------------------- BaseMongoDAOImpl.insert start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.RECEIVE_PARAM_AND_COLLECTION_NAME, JSON.toJSON(entity), collectionName);
        Map<String,String> insert = this.getTemplate(type).insert(entity,collectionName);
        log.info("insert success: {}", insert);
        long end = System.currentTimeMillis();
        log.info(Constants.COST_TIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.insert end -----------------------");
        return insert;
    }

    public boolean exists(Query query, MongoDBType type,String collectionName) {
        log.info("---------------------- BaseMongoDAOImpl.query start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.RECEIVE_PARAM_AND_COLLECTION_NAME, JSON.toJSON(query), collectionName);
        boolean result = this.getTemplate(type).exists(query,collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.COST_TIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.query end -----------------------");
        return result;
    }

    public void remove(Query query, MongoDBType type,String collectionName) {
        log.info("---------------------- BaseMongoDAOImpl.removenew start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.RECEIVE_PARAM_AND_COLLECTION_NAME, JSON.toJSON(query), collectionName);
        long deletedCount = this.getTemplate(type).remove(query,collectionName).getDeletedCount();
        log.info("remove count: {}", deletedCount);
        long end = System.currentTimeMillis();
        log.info(Constants.COST_TIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.removenew end -----------------------");
    }

}
