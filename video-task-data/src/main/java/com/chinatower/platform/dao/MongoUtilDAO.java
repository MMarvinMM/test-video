package com.chinatower.platform.dao;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.constants.Constants;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.exception.TowerMongodbException;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.util.MongoHandler;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @ClassName SensorMongoDAO
 * @Deacription
 * @Author zhujun
 * @Date 2022/3/31
 * @Version 1.0
 **/

@Slf4j
@Component
public class MongoUtilDAO<T>{
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

    public void remove(Query query, MongoDBType type,String collectionName) {
        log.info("---------------------- BaseMongoDAOImpl.removenew start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.PARAMLIST, JSON.toJSON(query), collectionName);
        long deletedCount = this.getTemplate(type).remove(query,collectionName).getDeletedCount();
        log.info("remove count: {}", deletedCount);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.removenew end -----------------------");
    }


    public List<T> queryMany(MongoDBType type, Query query, Class<T> entity, String collectionName) {
        return getTemplate(type).find(query, entity, collectionName);
    }


    public List<T> find(Query query, Class<T> entity, String collectionName) {
        return getTemplate(MongoDBType.PRIMARY).find(query, entity, collectionName);
    }

    public void mongoInsert(T entity, MongoDBType type,String collectionName) {
        log.info("---------------------- BaseMongoDAOImpl.insert start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.PARAMLIST, JSON.toJSON(entity), collectionName);
        T insert = this.getTemplate(type).insert(entity,collectionName);
        log.info("insert success: {}", insert);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.insert end -----------------------");
    }
    public void mongoUpdate(String accordingKey,String accordingValue,Update update, MongoDBType type,String collectionName) {
        log.info("---------------------- BaseMongoDAOImpl.insert start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.PARAMLIST, JSON.toJSON(update), collectionName);
        Criteria criteria = Criteria.where(accordingKey).is(accordingValue);
        Query query = Query.query(criteria);
        UpdateResult updateResult;
        updateResult = this.getTemplate(type).upsert(query, update,collectionName);
        log.info("insert success: {}", updateResult);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.insert end -----------------------");
    }

    public void mongoCreateTable(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.mongoCreate start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, collectionName);
        this.getTemplate(type).createCollection(collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.mongoCreate end -----------------------");
    }

    public void mongoCreateIndexs(String collectionName, MongoDBType type, List<IndexModel> indexModels) {
        log.info("---------------------- BaseMongoDAOImpl.mongoCreateIndexs start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, collectionName);
        this.getTemplate(type).createCollection(collectionName).createIndexes(indexModels);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.mongoCreateIndexs end -----------------------");
    }

    public ListIndexesIterable<Document> mongoListIndexs(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.mongoListIndexs start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, collectionName);
        ListIndexesIterable<Document> listIndexes = this.getTemplate(type).getCollection(collectionName).listIndexes();
        log.info("count result: {}", listIndexes);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.mongoListIndexs end -----------------------");
        return listIndexes;
    }

    public void mongoCreateIndex(String collectionName, MongoDBType type, String[] indexKey) {
        log.info("---------------------- BaseMongoDAOImpl.mongoCreateIndex start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, collectionName);
        Index index = new Index();
        for (int i=0;i<indexKey.length;i++){
            index.on(indexKey[i], Sort.Direction.ASC);
        }
        this.getTemplate(type).indexOps(collectionName).ensureIndex(index);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.mongoCreateIndex end -----------------------");
    }

    public Boolean collectionExists(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.collectionExists start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, collectionName);
        Boolean isExists = this.getTemplate(type).collectionExists(collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.collectionExists end -----------------------");
        return isExists;
    }

    public void saveOrUpdate(Query query, Update update, String collectionName, T entity, MongoDBType type) {
        getTemplate(type).upsert(query, update, entity.getClass(), collectionName);
    }
}
