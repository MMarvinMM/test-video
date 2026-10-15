package com.chinatower.receive.mongodb;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.vo.ExtraInfoVO;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.InterceptMongo;
import com.chinatower.event.util.subscenes.SubScenesMongoHelper;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.receive.constant.TableConstant;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class InterceptMongoMapper extends AbstractBaseMongoDAO<InterceptMongo> {

    public void mongoCreateTable(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.mongoCreate start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, "未指定操作的集合名");
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
        Assert.notNull(collectionName, "未指定操作的集合名");
        log.info(Constants.PARAMLIST, collectionName);
        Boolean isExists = this.getTemplate(type).collectionExists(collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.collectionExists end -----------------------");
        return isExists;
    }

    public void mongoCreateIndexs(String collectionName, MongoDBType type, List<IndexModel> indexModels) {
        log.info("---------------------- BaseMongoDAOImpl.mongoCreateIndexs start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, collectionName);
        this.getTemplate(type).createCollection(collectionName).createIndexes(indexModels);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.mongoCreateIndexs end -----------------------");
    }

    public ListIndexesIterable<Document> mongoListIndexs(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.mongoListIndexs start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, collectionName);
        ListIndexesIterable<Document> listIndexes = this.getTemplate(type).getCollection(collectionName).listIndexes();
        log.info("count result: {}", listIndexes);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.mongoListIndexs end -----------------------");
        return listIndexes;
    }

    public void insertIntercept(List<ReceiveAlarmVO> intercepts,String alarmTime){
            Date date = DateUtils.string2Date(alarmTime);
            String time = DateUtils.dateToStringYM(date);
            String collectionName = TableConstant.WARN_ALARM_INTERCEPT+"_"+time;
            List<InterceptMongo> mongoList = new ArrayList<>();
            for (ReceiveAlarmVO vo:intercepts) {

                InterceptMongo mongo = new InterceptMongo();
                BeanUtil.copyProperties(vo,mongo);
                mongo.setTransparentFieldsDto(vo.getTransparentFieldsDto());
                mongo.setId(String.valueOf(UUID.randomUUID()));
                if(!StringUtils.isEmpty(vo.getExtraField())){
                    ExtraInfoVO infoVO = JSONObject.parseObject(JSON.toJSONString(vo.getExtraInfo()),ExtraInfoVO.class);
                    if(infoVO != null){
                        mongo.setPresetId(infoVO.getPresetId());
                        mongo.setNoticeResource(String.valueOf(infoVO.getNoticeResource()));
                        mongo.setFrontAlarmPicUrl(infoVO.getFrontAlarmPicUrl());
                        mongo.setFrontAlarmVedioUrl(infoVO.getFrontAlarmVedioUrl());
                    }
                }
                mongo.setPtzP(vo.getPan());
                mongo.setPtzT(vo.getTilt());
                mongo.setPtzZ(vo.getZoom());
                mongo.setVideoUrl(vo.getAlarmVideoUrl());
                mongo.setImgUrl(vo.getOriginalPictureUrl());
                mongo.setFileImgUrlIcon(vo.getAlarmPictureUrl());
                mongo.setCreateTime(DateUtils.currentDateToString());
                mongo.setModifierDate(DateUtils.currentDateToString());
                mongo.setDeleteFlag(Constants.ZERO);
                if(StringUtils.isEmpty(vo.getThresh())){
                    mongo.setThisThresh("");
                }else{
                    mongo.setThisThresh(String.valueOf(vo.getThresh()));
                }
                mongo.setThresh(vo.getNowThresh());
                mongo.setDeviceHorizontal(vo.getHoriAzimuthAngle());
                mongo.setDevicePitch(vo.getDevicePitch());
                mongo.setIsSecondIdentify(vo.getIsSecondIdentify());
                SubScenesMongoHelper.applyToInterceptMongo(mongo, vo.getSubScenes(), vo.getAlarmType());
                //item.alarmInterval item.alarmIntervalType  item.alarmParam  item.alarmParamValue
                mongoList.add(mongo);
            }
            this.insertAll(mongoList,collectionName,MongoDBType.PRIMARY);
    }

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


}
