package com.chinatower.platform.mongo;

import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * @ClassName IotConfigFactoryMongoMapper
 * @Deacription
 * @Author Liulg9
 * @Date 2024/08/29 16:15
 * @Version 1.0
 **/
@Slf4j
@Component
public class IotConfigFactoryMongoMapper extends AbstractBaseMongoDAO {

    public void removeData(Query query,MongoDBType type, String collectionName) {
        log.info("---------------------- BaseMongoDAOImpl.remove start -----------------------");
        super.getTemplate(type).remove(query,collectionName).getDeletedCount();
    }
}
