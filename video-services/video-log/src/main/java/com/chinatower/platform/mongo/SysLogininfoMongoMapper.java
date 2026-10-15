package com.chinatower.platform.mongo;


import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.platform.entity.SysLogininfoEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class SysLogininfoMongoMapper extends AbstractBaseMongoDAO<SysLogininfoEntity> {

    public MongoTemplate getTemplate(MongoDBType type) {
        return super.getTemplate(type);
    }
}
