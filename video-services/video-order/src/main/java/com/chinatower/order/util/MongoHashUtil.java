package com.chinatower.order.util;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.constants.Constants;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class MongoHashUtil {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void createHashedIndexIfNeed(String collectionName, String indexKey, String indexName) {
        try {
            if (isHashedIndexExists(collectionName, indexKey)) {
                log.info("createHashedIndex 已经创建hash索引，不再处理, collectionName:{}", collectionName);
                return;
            }
//            Document command = new Document("createIndexes", collectionName)
//                    .append("indexes", Collections.singletonList(
//                            new Document("key", new Document(indexKey, Constants.HASHED))
//                                    .append("name", indexName)
//                    ));
//
//            mongoTemplate.getDb().runCommand(command);

            // 兼容不同Spring Data MongoDB版本的哈希索引创建方式
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
            collection.createIndex(
                    new Document(indexKey, Constants.HASHED),
                    new IndexOptions().name(indexName)
            );

            log.info("createHashedIndex 创建索引成功, collectionName:{}", collectionName);

        } catch (Exception e) {
            log.error("createHashedIndex error, e:", e);
        }
    }

    public void shardCollectionIfNeed(String collectionName, String indexKey) {
        try {
            String businessDbName = mongoTemplate.getDb().getName(); // 项目配置的业务数据库名
            String fullCollectionName = businessDbName + "." + collectionName;

            MongoDatabase adminDb = mongoTemplate.getMongoDbFactory().getMongoDatabase("admin");
            // 检查集合是否已分片
            boolean isSharded = isCollectionSharded(collectionName);
            if (!isSharded) {
                // 配置分片
                Document shardCmd = new Document()
                        .append("shardCollection", fullCollectionName)
                        .append("key", new Document(indexKey, Constants.HASHED));

                adminDb.runCommand(shardCmd);
                log.info("集合哈希分片成功, collectionName:{}", collectionName);
            } else {
                log.info("集合已配置分片，无需重复操作, collectionName:{}", collectionName);
            }
        } catch (Exception e) {
            log.error("集合配置分片失败, collectionName:{}", collectionName, e);
        }
    }


    private boolean isCollectionSharded(String collectionName) {
        try {
            Document collStatsCmd = new Document("collStats", collectionName);
            Document stats = mongoTemplate.getDb().runCommand(collStatsCmd);
            //sharded:代表集合是否已分片（true:已分片）
            return stats.containsKey("sharded") && Boolean.TRUE.equals(stats.getBoolean("sharded"));
        } catch (Exception e) {
            log.error("检查集合分片状态失败, collectionName:{}, e:", collectionName, e);
            return false;
        }
    }

    /**
     * 使用listIndexes命令检查哈希索引
     */
    private boolean isHashedIndexExists(String collectionName, String fieldName) {
        try {
            Document listIndexesCommand = new Document("listIndexes", collectionName);
            Document result = mongoTemplate.getDb().runCommand(listIndexesCommand);

            List<Document> indexes = (List<Document>) result.get("cursor", new Document()).get("firstBatch");
            log.info("isHashedIndexExists indexes:{}", JSON.toJSONString(indexes));
            for (Document index : indexes) {
                Document key = (Document) index.get("key");
                if (key.containsKey(fieldName) && "hashed".equals(key.get(fieldName))) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            log.error("isHashedIndexExists error:", e);
            return false;
        }
    }

}
