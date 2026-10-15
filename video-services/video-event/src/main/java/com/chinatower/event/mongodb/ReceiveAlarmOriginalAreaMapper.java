package com.chinatower.event.mongodb;

import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @ClassName ReceiveAlarmOriginalAreaMapper
 * @Deacription
 * @Author cuishijie
 * @Date 2023/03/01 16:00
 * @Version 1.0
 **/
@Slf4j
@Component
public class ReceiveAlarmOriginalAreaMapper extends AbstractBaseMongoDAO<AlarmOriginalAreaVO> {

    public void mongoCreateTable(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.mongoCreate start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, collectionName);
        log.info(Constants.PARAMLIST, collectionName);
        Boolean isCollectionExist = this.getTemplate(type).collectionExists(collectionName);
        //判断集合是否存在
        if(!Boolean.TRUE.equals(isCollectionExist)){
            this.getTemplate(type).createCollection(collectionName);
        }
//        this.getTemplate(type).createCollection(collectionName);
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
