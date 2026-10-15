package com.chinatower.platform.consumer.utils;

import com.chinatower.event.constants.Constants;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/26 15:57
 */

@Slf4j
@Component
public class MongoHashUtil {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 检查并配置集合分片
     * @param collectionName
     * @param param 分片字段
     */
    private void checkAndConfigureSharding(String collectionName,String param) {
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
                        .append("key", new Document(param, Constants.HASHED));

                adminDb.runCommand(shardCmd);
                log.info("为集合[{}]配置tenant_id哈希分片成功", collectionName);
            } else {
                log.info("集合[{}]已配置分片，无需重复操作", collectionName);
            }
        } catch (Exception e) {
            log.error("为集合[{}]配置分片失败", collectionName, e);
        }
    }

    /**
     * 检查集合是否已分片
     * @param collectionName
     * @return
     */
    private boolean isCollectionSharded(String collectionName) {
        try {
            Document collStatsCmd = new Document("collStats", collectionName);
            Document stats = mongoTemplate.getDb().runCommand(collStatsCmd);
            //sharded:代表集合是否已分片（true:已分片）
            return stats.containsKey("sharded") && Boolean.TRUE.equals(stats.getBoolean("sharded"));
        } catch (Exception e) {
            log.warn("检查集合[{}]分片状态失败: {}", collectionName, e.getMessage());
            return false;
        }
    }
}
