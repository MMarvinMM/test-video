package com.chinatower.order.mongodb;

import com.chinatower.event.constants.Constants;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.platform.entity.order.RecorderDeviceTrackDTO;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * 执法记录仪mongo 数据层
 */
@Component
@Slf4j
public class RecorderDeviceTrackMongoDAO extends AbstractBaseMongoDAO<RecorderDeviceTrackDTO> {


    public ListIndexesIterable<Document> mongoListIndexs(String collectionName, MongoDBType type) {
        log.info("---------------------- RecorderDeviceTrackMongoDAO.mongoListIndexs start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, collectionName);
        ListIndexesIterable<Document> listIndexes = this.getTemplate(type).getCollection(collectionName).listIndexes();
        log.info("count result: {}", listIndexes);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- RecorderDeviceTrackMongoDAO.mongoListIndexs end -----------------------");
        return listIndexes;
    }


    public void mongoCreateIndexs(String collectionName, MongoDBType type, List<IndexModel> indexModels) {
        log.info("---------------------- RecorderDeviceTrackMongoDAO.mongoCreateIndexs start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, collectionName);
        this.getTemplate(type).getCollection(collectionName).createIndexes(indexModels);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- RecorderDeviceTrackMongoDAO.mongoCreateIndexs end -----------------------");
    }

    public List<RecorderDeviceTrackDTO> findList(Date beginTime, Date endTime, String deviceCode, String collectionName){
        Query query = new Query();
        query.addCriteria(Criteria.where(com.chinatower.platform.constants.Constants.INDEXFIELD_RECORDER_TIME).gte(beginTime).lte(endTime));
        query.addCriteria(Criteria.where(com.chinatower.platform.constants.Constants.INDEXFIELD_UAV_DEVICECODE).is(deviceCode));
        query.with(Sort.by(Sort.Direction.ASC, com.chinatower.platform.constants.Constants.INDEXFIELD_RECORDER_TIME));
        List<RecorderDeviceTrackDTO> recorderDeviceTrackDTOList = this.getTemplate(MongoDBType.PRIMARY).find(query, RecorderDeviceTrackDTO.class,
                collectionName);
        return recorderDeviceTrackDTOList;
    }
}
