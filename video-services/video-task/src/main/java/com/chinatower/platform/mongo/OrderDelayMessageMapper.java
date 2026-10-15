package com.chinatower.platform.mongo;

import com.alibaba.fastjson.JSON;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.modules.alarm.entity.OrderDelayMessage;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @ClassName OrderDelayMessageMapper
 * @Deacription
 * @Author Liulg9
 * @Date 2024/08/29 16:15
 * @Version 1.0
 **/
@Slf4j
@Component
public class OrderDelayMessageMapper extends AbstractBaseMongoDAO<OrderDelayMessage> {

    /**
     * 创建 MongoDB 集合
     *
     * @param collectionName 集合名称
     * @param type           MongoDB 类型（主库、副库等）
     */
    public void mongoCreateTable(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.mongoCreate start -----------------------");
        long start = System.currentTimeMillis();
        // 检查集合名称是否为空
        Assert.notNull(collectionName, "未指定操作的集合名");
        log.info(Constants.PARAMLIST, collectionName);
        Boolean isCollectionExist = this.getTemplate(type).collectionExists(collectionName);
        //判断集合是否存在
        if(!Boolean.TRUE.equals(isCollectionExist)){
            this.getTemplate(type).createCollection(collectionName);
        }
        // 调用 MongoDB 创建集合方法
//        this.getTemplate(type).createCollection(collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.mongoCreate end -----------------------");
    }

    /**
     * 判断 MongoDB 集合是否存在
     *
     * @param collectionName 集合名称
     * @param type           MongoDB 类型（主库、副库等）
     * @return Boolean 集合是否存在
     */
    public Boolean collectionExists(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.collectionExists start -----------------------");
        long start = System.currentTimeMillis();
        // 检查集合名称是否为空
        Assert.notNull(collectionName, "未指定操作的集合名");
        log.info(Constants.PARAMLIST, collectionName);
        // 判断集合是否存在
        Boolean isExists = this.getTemplate(type).collectionExists(collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.collectionExists end -----------------------");
        return isExists;
    }

    /**
     * 更新 MongoDB 集合中的记录
     *
     * @param query          查询条件
     * @param update         更新内容
     * @param type           MongoDB 类型（主库、副库等）
     * @param collectionName 集合名称
     */
    public void mongoUpdate(Query query,Update update, MongoDBType type, String collectionName) {
        log.info("---------------------- BaseMongoDAOImpl.insert start -----------------------");
        long start = System.currentTimeMillis();
        // 打印更新内容和集合名称
        log.info(Constants.PARAMLIST, JSON.toJSON(update), collectionName);
       /* Criteria criteria = Criteria.where(accordingKey).is(accordingValue);
        Query query = Query.query(criteria);*/
        // 执行更新操作（批量更新符合条件的记录）
        UpdateResult updateResult= this.getTemplate(type).updateMulti(query, update,collectionName);
        log.info("insert success: {}", updateResult);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.insert end -----------------------");
    }

    /**
     * 删除 MongoDB 集合中的记录
     *
     * @param query          查询条件
     * @param type           MongoDB 类型（主库、副库等）
     * @param collectionName 集合名称
     */
    public void removeData(Query query,MongoDBType type, String collectionName) {
        log.info("---------------------- BaseMongoDAOImpl.remove start -----------------------");
        // 执行删除操作并获取删除的记录数
        super.getTemplate(type).remove(query,collectionName).getDeletedCount();
    }
}
