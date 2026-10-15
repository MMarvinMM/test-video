package com.chinatower.order.mongodb;

import com.chinatower.event.constants.Constants;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.order.dto.UavDeviceTrackDTO;
import com.chinatower.platform.entity.order.UavDeviceTrackVO;
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
 * @ClassName UavDeviceTrackMongoDAO
 * @Deacription
 * @Author luo.zhenghong
 * @Date 2023/6/15 14:53
 * @Version release_v1.5.95
 **/
@Component
@Slf4j
public class UavDeviceTrackMongoDAO extends AbstractBaseMongoDAO<UavDeviceTrackDTO> {


    public ListIndexesIterable<Document> mongoListIndexs(String collectionName, MongoDBType type) {
        log.info("---------------------- UavDeviceTrackMongoDAO.mongoListIndexs start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, collectionName);
        ListIndexesIterable<Document> listIndexes = this.getTemplate(type).getCollection(collectionName).listIndexes();
        log.info("count result: {}", listIndexes);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- UavDeviceTrackMongoDAO.mongoListIndexs end -----------------------");
        return listIndexes;
    }


    public void mongoCreateIndexs(String collectionName, MongoDBType type, List<IndexModel> indexModels) {
        log.info("---------------------- UavDeviceTrackMongoDAO.mongoCreateIndexs start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, collectionName);
        this.getTemplate(type).getCollection(collectionName).createIndexes(indexModels);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- UavDeviceTrackMongoDAO.mongoCreateIndexs end -----------------------");
    }

    public List<UavDeviceTrackVO> findList(Date beginTime, Date endTime, String deviceCode, String collectionName){
        Query query = new Query();
        query.addCriteria(Criteria.where(com.chinatower.platform.constants.Constants.INDEXFIELD_UAV_TRACKTIME).gte(beginTime).lte(endTime));
        query.addCriteria(Criteria.where(com.chinatower.platform.constants.Constants.INDEXFIELD_UAV_DEVICECODE).is(deviceCode));
        query.with(Sort.by(Sort.Direction.ASC, com.chinatower.platform.constants.Constants.INDEXFIELD_UAV_TRACKTIME));
        List<UavDeviceTrackVO> uavDeviceTrackDTOList = this.getTemplate(MongoDBType.PRIMARY).find(query, UavDeviceTrackVO.class,
                collectionName);
        return uavDeviceTrackDTOList;
    }
}
