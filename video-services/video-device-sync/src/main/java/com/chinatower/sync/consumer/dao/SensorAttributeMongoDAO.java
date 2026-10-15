package com.chinatower.sync.consumer.dao;

import com.alibaba.fastjson.JSON;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.exception.TowerMongodbException;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.util.MongoHandler;
import com.chinatower.sync.consumer.item.Constants;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
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
public class SensorAttributeMongoDAO<T extends BaseMongoDTO>{
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
    public void mongoRemove(QueryEntity entity, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.removenew start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notEmpty(entity, Constants.PARAM_NOT_EXISTENT);
        String collectionName = (String)entity.get(Constants.PARAM_COLLECTIONNAME);
        Class<?> entityClass = (Class)entity.get(Constants.PARAM_ENTITYCLASS);
        log.info(Constants.PARAMLIST, JSON.toJSON(entity), collectionName);
        long deletedCount = this.getTemplate(type).remove(MongoHandler.handleQueryMap(entity, entityClass),entityClass,collectionName).getDeletedCount();
        log.info("remove count: {}", deletedCount);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.removenew end -----------------------");
    }

    public Object query(QueryEntity entity, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.query start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notEmpty(entity, Constants.PARAM_NOT_EXISTENT);
        String collectionName = (String)entity.get(Constants.PARAM_COLLECTIONNAME);
        Class<?> entityClass = (Class)entity.get(Constants.PARAM_ENTITYCLASS);
        log.info(Constants.PARAMLIST, JSON.toJSON(entity), collectionName);
        Object result = this.getTemplate(type).findOne(MongoHandler.handleQueryMap(entity, entityClass),entityClass,collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.query end -----------------------");
        return result;
    }

    public Object findOne(Query query, MongoDBType type,Class<?> entityClass,String collectionName) {
        log.info("---------------------- BaseMongoDAOImpl.query start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.PARAMLIST, JSON.toJSON(query), collectionName);
        Object result = this.getTemplate(type).findOne(query,entityClass,collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.query end -----------------------");
        return result;
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
        UpdateResult updateResult = this.getTemplate(type).upsert(query, update,collectionName);
        log.info("insert success: {}", updateResult);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.insert end -----------------------");
    }

    public Long mongoCount(QueryEntity entity, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.count start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(entity, Constants.PARAM_NOT_EXISTENT);
        String collectionName = (String)entity.get(Constants.PARAM_COLLECTIONNAME);
        Class<?> entityClass = (Class)entity.get(Constants.PARAM_ENTITYCLASS);
        log.info(Constants.PARAMLIST, JSON.toJSON(entity), collectionName);
        long count = this.getTemplate(type).count(MongoHandler.handleQueryMap(entity, entityClass), entityClass, collectionName);
        log.info("count result: {}", count);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.count end -----------------------");
        return count;
    }

    public void insertAll(List<T> entityList, String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.insertAll start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(entityList, "插入参数为空");
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, JSON.toJSON(entityList), collectionName);
        this.getTemplate(type).insert(entityList, collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.insertAll end -----------------------");
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
}
