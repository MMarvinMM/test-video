package com.chinatower.platform.dao;

import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.platform.modules.AlarmSyncStatusEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 属地告警同步状态 Mongo DAO
 */
@Slf4j
@Component
public class AlarmSyncStatusMongoDAO extends AbstractBaseMongoDAO<AlarmSyncStatusEntity> {

    /**
     * 新增同步状态记录，每次同步均插入一条，便于追溯历史
     *
     * @param entity 同步状态实体
     */
    public void saveSyncStatus(AlarmSyncStatusEntity entity) {
        log.info("保存属地告警同步状态, platformCode:{}, pullDate:{}, syncType:{}, syncStatus:{}",
                entity.getPlatformCode(), entity.getPullDate(), entity.getSyncType(), entity.getSyncStatus());
        this.getTemplate(MongoDBType.PRIMARY).insert(entity);
    }
}
