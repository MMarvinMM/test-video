package com.chinatower.device.mongodb;

import com.chinatower.device.mongodb.dto.AlgorithmHornTaskDTO;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class HornTaskMongoDAO extends AbstractBaseMongoDAO<AlgorithmHornTaskDTO> {

    public void removeRecord(Query query, MongoDBType type, String collectionName) {
        this.getTemplate(type).remove(query,collectionName).getDeletedCount();
    }
}
