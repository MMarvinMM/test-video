package com.chinatower.platform.mongodb;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.InterceptMongo;
import com.chinatower.event.entity.vo.InterceptVO;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.platform.constant.TableConstant;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Slf4j
@Component
public class InterceptMongoMapper extends AbstractBaseMongoDAO<InterceptMongo> {

    public void mongoUpdate(Query query,Update update, MongoDBType type, String collectionName, boolean multi) {
        log.info("---------------------- BaseMongoDAOImpl.insert start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.PARAMLIST, JSON.toJSON(update), collectionName);
       /* Criteria criteria = Criteria.where(accordingKey).is(accordingValue);
        Query query = Query.query(criteria);*/
        UpdateResult updateResult;
        if(multi){
            updateResult = this.getTemplate(type).updateMulti(query, update,collectionName);
        }else {
            updateResult = this.getTemplate(type).upsert(query, update,collectionName);
        }
        log.info("insert success: {}", updateResult);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.insert end -----------------------");
    }

    public void updateIntercept(InterceptVO vo){
        log.info("updateIntercept-回收站修改实体:{}", JSON.toJSONString(vo));
        // 获取表名
        String tableName = TableConstant.WARN_ALARM_INTERCEPT +"_"+ vo.getWarningOrderId().substring(0, 6);
        Query updateQuery = new Query();
        updateQuery.addCriteria(Criteria.where(Constants.RES_WARNING_ORDER_ID).is(vo.getWarningOrderId()));
        Update updateEntity = new Update();
        if(!StringUtils.isEmpty(vo.getFileImgUrlSrc())){
            updateEntity.set("file_img_url_src",vo.getFileImgUrlSrc());
        }
        if(!StringUtils.isEmpty(vo.getFileImgUrlIcon())){
            updateEntity.set("file_img_url_icon",vo.getFileImgUrlIcon());
        }
        this.mongoUpdate(updateQuery,updateEntity,MongoDBType.PRIMARY, tableName,true);
    }
}
