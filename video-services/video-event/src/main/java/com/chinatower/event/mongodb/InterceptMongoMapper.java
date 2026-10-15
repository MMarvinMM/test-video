package com.chinatower.event.mongodb;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.vo.ExtraInfoVO;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.InterceptDTO;
import com.chinatower.event.entity.dto.InterceptMongo;
import com.chinatower.event.util.subscenes.SubScenesMongoHelper;
import com.chinatower.event.entity.dto.OperateMongo;
import com.chinatower.event.entity.vo.InterceptVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName InterceptMongoMapper
 * @Deacription
 * @Author sxf
 * @Date 2023/08/02 14:56
 * @Version 1.0
 **/
@Slf4j
@Component
public class InterceptMongoMapper extends AbstractBaseMongoDAO<InterceptMongo> {

    public void mongoCreateTable(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.mongoCreate start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, "未指定操作的集合名");
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
        Boolean isCollectionExist = this.getTemplate(type).collectionExists(collectionName);
        if(Boolean.TRUE.equals(isCollectionExist)){
            log.info("集合名collectionName存在，创建索引：{}", collectionName);
            this.getTemplate(type).getCollection(collectionName).createIndexes(indexModels);
        } else {
            log.info("集合名collectionName不存在，创建索引：{}", collectionName);
            this.getTemplate(type).createCollection(collectionName).createIndexes(indexModels);
        }
//        this.getTemplate(type).createCollection(collectionName).createIndexes(indexModels);
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

    public void updateIntercept(InterceptDTO vo){
        log.info("updateIntercept-回收站修改实体:{}", JSON.toJSONString(vo));
        // 获取表名
        String tableName = "";
        if (!StringUtils.isEmpty(vo.getOrderId())){
             tableName = TableConstant.WARN_ALARM_INTERCEPT +"_"+ vo.getOrderId().substring(0, 6);
        }else {
             tableName = TableConstant.WARN_ALARM_INTERCEPT +"_"+ vo.getOrderIds().get(0).substring(0, 6);
        }
        Query updateQuery = new Query();
        if(CollectionUtils.isEmpty(vo.getOrderIds())){
            updateQuery.addCriteria(Criteria.where(Constants.RES_WARNING_ORDER_ID).is(vo.getOrderId()));
        }else{
            updateQuery.addCriteria(Criteria.where(Constants.RES_WARNING_ORDER_ID).in(vo.getOrderIds()));
        }
        Update updateEntity = new Update();
        updateEntity.set("modifier_time",DateUtils.currentDateToString());
        if(!StringUtils.isEmpty(vo.getWarningTypeId())){
            updateEntity.set("warning_type_id",vo.getWarningTypeId());
        }
        if(!StringUtils.isEmpty(vo.getEmergencyLevel())){
            updateEntity.set("emergency_level",vo.getEmergencyLevel());
        }
        if(!StringUtils.isEmpty(vo.getExtraField())){
            updateEntity.set("extra_field",vo.getExtraField());
        }
        if(!StringUtils.isEmpty(vo.getLatitude())){
            updateEntity.set("latitude",vo.getLatitude());
        }
        if(!StringUtils.isEmpty(vo.getLongitude())){
            updateEntity.set("longitude",vo.getLongitude());
        }
        if(!StringUtils.isEmpty(vo.getAddress())){
            updateEntity.set("address",vo.getAddress());
        }
        if(!StringUtils.isEmpty(vo.getJudgeResult())){
            updateEntity.set("judge_result",vo.getJudgeResult());
        }
        if(!StringUtils.isEmpty(vo.getProvinceCode())){
            updateEntity.set("province_code",vo.getProvinceCode());
        }
        if(!StringUtils.isEmpty(vo.getProvinceName())){
            updateEntity.set("province_name",vo.getProvinceName());
        }
        if(!StringUtils.isEmpty(vo.getCityCode())){
            updateEntity.set("city_code",vo.getCityCode());
        }
        if(!StringUtils.isEmpty(vo.getCityName())){
            updateEntity.set("city_name",vo.getCityName());
        }
        if(!StringUtils.isEmpty(vo.getCountyCode())){
            updateEntity.set("county_code",vo.getCountyCode());
        }
        if(!StringUtils.isEmpty(vo.getCountyName())){
            updateEntity.set("county_name",vo.getCountyName());
        }
        if(!StringUtils.isEmpty(vo.getHoriAzimuthAngle())){
            updateEntity.set("hori_azimuth_angle",vo.getHoriAzimuthAngle());
        }
        if(!StringUtils.isEmpty(vo.getCheckGridFlag()) && Constants.ONE.equals(vo.getCheckGridFlag())){
            updateEntity.set("grid_id",vo.getGridId());
            updateEntity.set("grid_name",vo.getGridName());
        }
        if(!StringUtils.isEmpty(vo.getImportanceLevel())){
            updateEntity.set("importance_level",vo.getImportanceLevel());
        }
        if(!StringUtils.isEmpty(vo.getVideoUrl())){
            updateEntity.set("video_url",vo.getVideoUrl());
        }
        if(!StringUtils.isEmpty(vo.getFileImgUrlSrc())){
            updateEntity.set("img_url",vo.getFileImgUrlSrc());
        }
        if(!StringUtils.isEmpty(vo.getFileImgUrlSrc())){
            updateEntity.set("file_img_url_src",vo.getFileImgUrlSrc());
        }
        if(!StringUtils.isEmpty(vo.getFileImgUrlIcon())){
            updateEntity.set("file_img_url_icon",vo.getFileImgUrlIcon());
        }
        if(!StringUtils.isEmpty(vo.getIsSecondIdentify())){
            updateEntity.set("is_second_identify",vo.getIsSecondIdentify());
        }
        if(!StringUtils.isEmpty(vo.getSecondIdentifyResult())){
            updateEntity.set("second_identify_result",vo.getSecondIdentifyResult());
        }
        if(!StringUtils.isEmpty(vo.getLinkageAlgorithm())){
            updateEntity.set("linkage_algorithm",vo.getLinkageAlgorithm());
        }
        this.mongoUpdate(updateQuery,updateEntity,MongoDBType.PRIMARY, tableName,true);
    }

    public void updateInterceptList(List<InterceptDTO> interceptMongoList){
        log.info("updateInterceptList-回收站修改实体:{}", JSON.toJSONString(interceptMongoList));
        for (InterceptDTO vo:interceptMongoList) {
            // 获取表名
            String tableName = TableConstant.WARN_ALARM_INTERCEPT +"_"+ vo.getOrderId().substring(0, 6);
            Query updateQuery = new Query();
            if(CollectionUtils.isEmpty(vo.getOrderIds())){
                updateQuery.addCriteria(Criteria.where(Constants.RES_WARNING_ORDER_ID).is(vo.getOrderId()));
            }else{
                updateQuery.addCriteria(Criteria.where(Constants.RES_WARNING_ORDER_ID).in(vo.getOrderIds()));
            }
            Update updateEntity = new Update();
            updateEntity.set("modifier_time",DateUtils.currentDateToString());
            if(!StringUtils.isEmpty(vo.getWarningTypeId())){
                updateEntity.set("warning_type_id",vo.getWarningTypeId());
            }
            if(!StringUtils.isEmpty(vo.getEmergencyLevel())){
                updateEntity.set("emergency_level",vo.getEmergencyLevel());
            }
            if(!StringUtils.isEmpty(vo.getExtraField())){
                updateEntity.set("extra_field",vo.getExtraField());
            }
            if(!StringUtils.isEmpty(vo.getLatitude())){
                updateEntity.set("latitude",vo.getLatitude());
            }
            if(!StringUtils.isEmpty(vo.getLongitude())){
                updateEntity.set("longitude",vo.getLongitude());
            }
            if(!StringUtils.isEmpty(vo.getAddress())){
                updateEntity.set("address",vo.getAddress());
            }
            if(!StringUtils.isEmpty(vo.getJudgeResult())){
                updateEntity.set("judge_result",vo.getJudgeResult());
            }
            if(!StringUtils.isEmpty(vo.getProvinceCode())){
                updateEntity.set("province_code",vo.getProvinceCode());
            }
            if(!StringUtils.isEmpty(vo.getProvinceName())){
                updateEntity.set("province_name",vo.getProvinceName());
            }
            if(!StringUtils.isEmpty(vo.getCityCode())){
                updateEntity.set("city_code",vo.getCityCode());
            }
            if(!StringUtils.isEmpty(vo.getCityName())){
                updateEntity.set("city_name",vo.getCityName());
            }
            if(!StringUtils.isEmpty(vo.getCountyCode())){
                updateEntity.set("county_code",vo.getCountyCode());
            }
            if(!StringUtils.isEmpty(vo.getCountyName())){
                updateEntity.set("county_name",vo.getCountyName());
            }
            if(!StringUtils.isEmpty(vo.getHoriAzimuthAngle())){
                updateEntity.set("hori_azimuth_angle",vo.getHoriAzimuthAngle());
            }
            if(!StringUtils.isEmpty(vo.getCheckGridFlag()) && Constants.ONE.equals(vo.getCheckGridFlag())){
                updateEntity.set("grid_id",vo.getGridId());
                updateEntity.set("grid_name",vo.getGridName());
            }
            if(!StringUtils.isEmpty(vo.getImportanceLevel())){
                updateEntity.set("importance_level",vo.getImportanceLevel());
            }
            if(!StringUtils.isEmpty(vo.getVideoUrl())){
                updateEntity.set("video_url",vo.getVideoUrl());
            }
            if(!StringUtils.isEmpty(vo.getFileImgUrlSrc())){
                updateEntity.set("img_url",vo.getFileImgUrlSrc());
            }
            if(!StringUtils.isEmpty(vo.getFileImgUrlIcon())){
                updateEntity.set("file_img_url_icon",vo.getFileImgUrlIcon());
            }
            if(!StringUtils.isEmpty(vo.getIsSecondIdentify())){
                updateEntity.set("is_second_identify",vo.getIsSecondIdentify());
            }
            if(!StringUtils.isEmpty(vo.getSecondIdentifyResult())){
                updateEntity.set("second_identify_result",vo.getSecondIdentifyResult());
            }
            if(!StringUtils.isEmpty(vo.getLinkageAlgorithm())){
                updateEntity.set("linkage_algorithm",vo.getLinkageAlgorithm());
            }
            this.mongoUpdate(updateQuery,updateEntity,MongoDBType.PRIMARY, tableName,true);
        }

    }

}
