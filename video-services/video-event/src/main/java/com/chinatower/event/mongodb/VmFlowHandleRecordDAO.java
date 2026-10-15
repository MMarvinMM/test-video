package com.chinatower.event.mongodb;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.HandleRecordMongo;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @ClassName VmFlowHandleRecordDAO
 * @Deacription
 * @Author lyy
 * @Date 2024/06/26 10:34
 * @Version 1.0
 **/
@Slf4j
@Component
public class VmFlowHandleRecordDAO extends AbstractBaseMongoDAO<HandleRecordMongo> {

    public void mongoCreateTable(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.mongoCreate start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, "未指定操作的集合名");
        log.info(Constants.PARAMLIST, collectionName);
        log.info(Constants.PARAMLIST, collectionName);
        Boolean isCollectionExist = this.getTemplate(type).collectionExists(collectionName);
        //判断集合是否存在
        if(!Boolean.TRUE.equals(isCollectionExist)){
            this.getTemplate(type).createCollection(collectionName);
        }
//        this.getTemplate(type).createCollection(collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.mongoCreate end -----------------------");
    }

    public Boolean collectionExists(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.collectionExists start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, "未指定操作的集合名");
        log.info(Constants.PARAMLIST, collectionName);
        Boolean isExists = this.getTemplate(type).collectionExists(collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.collectionExists end -----------------------");
        return isExists;
    }

    public void mongoInsert(HandleRecordMongo entity, MongoDBType type,String collectionName) {
        log.info("---------------------- BaseMongoDAOImpl.insert start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.PARAMLIST, JSON.toJSON(entity), collectionName);
        HandleRecordMongo insert = this.getTemplate(type).insert(entity,collectionName);
        log.info("insert success: {}", insert);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.insert end -----------------------");
    }

    public void mongoUpdate(Query query, Update update, MongoDBType type, String collectionName, boolean multi) {
        log.info("---------------------- BaseMongoDAOImpl.insert start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.PARAMLIST, JSON.toJSON(update), collectionName);
       /* Criteria criteria = Criteria.where(accordingKey).is(accordingValue);
        Query query = Query.query(criteria);*/
        UpdateResult updateResult;
        if(multi){
            updateResult = this.getTemplate(type).updateMulti(query, update,collectionName);
        }else {
            updateResult = this.getTemplate(type).upsert(query, update,collectionName);
        }
        log.info("insert success: {}", updateResult);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.insert end -----------------------");
    }
}
