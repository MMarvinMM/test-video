package com.chinatower.common.core.cache;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MongoTemplateDelegate {
    @Resource
    MongoTemplate mongoTemplate;

    public Set<String> getCollectionNames() {
        return mongoTemplate.getCollectionNames();
    }

    public MongoDatabase getDb() {
        return mongoTemplate.getDb();
    }

  /*  private final Cache<String, Long> CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .recordStats()
            .build();*/

    public <T> List<T> find(Query query, Class<T> entityClass, String collectionName) {
        return mongoTemplate.find(query, entityClass, collectionName);
    }

    public long count(Query query, String collectionName) {
        log.info("count query = {} , collectionName = {}", query, collectionName);
        try {
            String key = key(query, collectionName);
            String count = RedisUtil.get(key, RedisDBType.PRIMARY);
            if(!StringUtils.isEmpty(count)){
                return Long.valueOf(count);
            }else{
                long num = mongoTemplate.count(query, collectionName);
                RedisUtil.set(key,num,RedisDBType.PRIMARY);
                RedisUtil.expire(key, 100, TimeUnit.SECONDS, RedisDBType.PRIMARY);
                return num;
            }
        } catch (Exception e) {
            log.error("缓存失败", e);
            return mongoTemplate.count(query, collectionName);
        }
    }

    private static String key(Query query, String collectionName) {
        String key = query + collectionName;
        log.info("key = {}", key);
        return key;
    }
}
