package com.chinatower.event.mongodb;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.vo.ExtraInfoVO;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.OperateMongo;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.util.subscenes.SubScenesMongoHelper;
import com.chinatower.event.util.DateUtils;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
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

/**
 * @ClassName OperateMongoMapper
 * @Deacription
 * @Author sxf
 * @Date 2023/08/02 14:56
 * @Version 1.0
 **/
@Slf4j
@Component
public class OperateMongoMapper extends AbstractBaseMongoDAO<OperateMongo> {

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

    public void insertOperate(List<ReceiveAlarmVO> operates, String alarmTime) {
        Date date = DateUtils.string2Date(alarmTime);
        String time = DateUtils.dateToStringYM(date);
        String collectionName = TableConstant.WARN_ALARM_OPERATE + "_" + time;
        List<OperateMongo> mongoList = new ArrayList<>();
        for (ReceiveAlarmVO vo : operates) {
            OperateMongo mongo = new OperateMongo();
            BeanUtil.copyProperties(vo, mongo);
            ExtraInfoVO infoVO = JSONObject.parseObject(JSON.toJSONString(vo.getExtraInfo()),ExtraInfoVO.class);
            if(infoVO != null){
                mongo.setPresetId(infoVO.getPresetId());
                mongo.setNoticeResource(String.valueOf(infoVO.getNoticeResource()));
                mongo.setMsgId(infoVO.getMsgId());
            }
            mongo.setWarningTypeId(vo.getAlarmType());
            mongo.setProvinceId(vo.getProvinceCode());
            mongo.setCityId(vo.getCityCode());
            mongo.setCountyId(vo.getCountyCode());
            mongo.setPtzP(vo.getPan());
            mongo.setPtzT(vo.getTilt());
            mongo.setPtzZ(vo.getZoom());
            mongo.setFileImgUrlSrc(vo.getOriginalPictureUrl());
            mongo.setFileImgUrlIcon(vo.getAlarmPictureUrl());
            mongo.setCreateTime(DateUtils.currentDateToString());
            mongo.setDeleteFlag(Constants.ZERO);
            mongo.setEmergencyLevel("01");
            mongo.setEmergencyLevelName("一般");
            mongo.setJudgeResult(Constants.THREE);
            mongo.setVideoUrl(vo.getAlarmVideoUrl());
            mongo.setOperateType(vo.getIsOperation());
            mongo.setDeviceHorizontal(vo.getHoriAzimuthAngle());
            mongo.setIsSecondIdentify(vo.getIsSecondIdentify());
            SubScenesMongoHelper.applyToOperateMongo(mongo, vo.getSubScenes(), vo.getAlarmType());
            //item.alarmInterval item.alarmIntervalType  item.alarmParam  item.alarmParamValue
            mongoList.add(mongo);
        }
        this.insertAll(mongoList, collectionName, MongoDBType.PRIMARY);
    }

    /**
     * 插入运营研判告警同步数据
     * @param vo
     * @param alarmTime
     */
    public void insertLocalOperate(ReceiveAlarmVO vo, String alarmTime) {
        Date date = DateUtils.string2Date(alarmTime);
        String time = DateUtils.dateToStringYM(date);
        String collectionName = TableConstant.WARN_LOCAL_ALARM_OPERATE + "_" + time;
        //判断表是否已存在，不存在则创建表
        Boolean exists = this.collectionExists(collectionName, MongoDBType.PRIMARY);
        if (!exists) {
            this.mongoCreateTable(collectionName, MongoDBType.PRIMARY);
        }
        List<OperateMongo> mongoList = new ArrayList<>();

            OperateMongo mongo = new OperateMongo();
            BeanUtil.copyProperties(vo, mongo);
            ExtraInfoVO infoVO = JSONObject.parseObject(JSON.toJSONString(vo.getExtraInfo()),ExtraInfoVO.class);
            if(infoVO != null){
                mongo.setPresetId(infoVO.getPresetId());
                mongo.setNoticeResource(String.valueOf(infoVO.getNoticeResource()));
                mongo.setMsgId(infoVO.getMsgId());
            }
            mongo.setWarningTypeId(vo.getAlarmType());
            mongo.setProvinceId(vo.getProvinceCode());
            mongo.setCityId(vo.getCityCode());
            mongo.setCountyId(vo.getCountyCode());
            mongo.setPtzP(vo.getPan());
            mongo.setPtzT(vo.getTilt());
            mongo.setPtzZ(vo.getZoom());
            mongo.setFileImgUrlSrc(vo.getOriginalPictureUrl());
            mongo.setFileImgUrlIcon(vo.getAlarmPictureUrl());
            mongo.setCreateTime(DateUtils.currentDateToString());
            mongo.setDeleteFlag(Constants.ZERO);
            mongo.setEmergencyLevel("01");
            mongo.setEmergencyLevelName("一般");
            mongo.setJudgeResult(Constants.THREE);
            mongo.setVideoUrl(vo.getAlarmVideoUrl());
            mongo.setOperateType(vo.getIsOperation());
            SubScenesMongoHelper.applyToOperateMongo(mongo, vo.getSubScenes(), vo.getAlarmType());
            //item.alarmInterval item.alarmIntervalType  item.alarmParam  item.alarmParamValue
            mongoList.add(mongo);

        this.insertAll(mongoList, collectionName, MongoDBType.PRIMARY);
        //检查索引是否存在
        boolean listIndexs = checkListIndexs(collectionName, MongoDBType.PRIMARY);
        if (!listIndexs) {
            List<IndexModel> indexModels = getIndexModels();
            this.mongoCreateIndexs(collectionName, MongoDBType.PRIMARY, indexModels);
        }
    }

    /**
     * 检查索引是否已创建
     * @param collectionName
     * @param primary
     * @return
     */
    private boolean checkListIndexs(String collectionName, MongoDBType primary) {
        boolean success = false;
        try {
            ListIndexesIterable<Document> indexList = this.mongoListIndexs(collectionName, primary);
            for (Document document : indexList) {
                Object key = document.get("key");
                if (null != key && key instanceof Document) {
                    Document keyDocument = (Document) key;
                    if (keyDocument.containsKey(Constants.RES_WARNING_ORDER_ID)) {
                        success = true;
                    }
                }
            }
        } catch (Exception e) {
            success = false;
        }
        return success;
    }

    /**
     * 创建索引列
     * @return
     */
    private List<IndexModel> getIndexModels() {
        List<IndexModel> indexModels = new ArrayList<>();
        String[] str = new String[]{Constants.INDEXFIELD_WARNING_ORDER_ID, Constants.INDEXFIELD_TENANT_ID, Constants.INDEXFIELD_PROVINCE_ID,
                Constants.INDEXFIELD_CITY_ID, Constants.INDEXFIELD_COUNTY_ID, Constants.INDEXFIELD_CREATE_USER, Constants.INDEXFIELD_CREATE_TIME, Constants.INDEXFIELD_WARNING_TYPE_ID,
                Constants.INDEXFIELD_EMERGENCY_LEVEL, Constants.INDEXFIELD_ALARM_TIME, Constants.INDEXFIELD_ALGORITHM_CODE, Constants.INDEXFIELD_DUPLICATE_ORDER_ID,
                Constants.INDEX_DEL_TIME_RESULT, Constants.INDEX_DEL_ALARMTIME_TYPE};
        BasicDBObject indexObj ;
        for (String index : str) {
            IndexOptions aIndexOptions;
            aIndexOptions = new IndexOptions().background(true).name(index);
            indexObj = new BasicDBObject();
            if(index.equals(Constants.INDEXFIELD_ALARM_TIME)){
                indexObj.put(index, -1);
            }else{
                indexObj.put(index, Constants.INDEXASC_ONE);
            }
            indexModels.add(new IndexModel(indexObj, aIndexOptions));
        }
        return indexModels;
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

    /**
     * 运营研判数据修改
     * @param operateMongoList
     */
    public void updateOperate(List<OperateMongo> operateMongoList){
        log.info("updateOperate-运营研判修改实体:{}", JSON.toJSONString(operateMongoList));
        for (OperateMongo vo:operateMongoList) {
            // 获取表名
            String tableName = TableConstant.WARN_ALARM_OPERATE +"_"+ vo.getWarningOrderId().substring(0, 6);

            Query updateQuery = new Query();
            updateQuery.addCriteria(Criteria.where(Constants.RES_WARNING_ORDER_ID).is(vo.getWarningOrderId()));
            Update updateEntity = new Update();
            if(!StringUtils.isEmpty(vo.getModifierTime())){
                updateEntity.set("modifier_time",vo.getModifierTime());
            }
            if(!StringUtils.isEmpty(vo.getJudgeResult())){
                updateEntity.set("judge_time",DateUtils.currentDateToString());
                updateEntity.set("judge_result",vo.getJudgeResult());
            }
            if(!StringUtils.isEmpty(vo.getCreateUser())){
                updateEntity.set("create_user",vo.getCreateUser());
            }
            if(!StringUtils.isEmpty(vo.getCreateUserName())){
                updateEntity.set("create_user_name",vo.getCreateUserName());
            }
            if(!StringUtils.isEmpty(vo.getExtraField())){
                updateEntity.set("extra_field",vo.getExtraField());
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
            if(!StringUtils.isEmpty(vo.getSource())){
                updateEntity.set("source",vo.getSource());
            }
            if(!StringUtils.isEmpty(vo.getWarningTypeId())){
                updateEntity.set("warning_type_id",vo.getWarningTypeId());
            }
            if (!CollectionUtils.isEmpty(vo.getSubScenes())) {
                updateEntity.set("sub_scenes", vo.getSubScenes());
            }
            if(!StringUtils.isEmpty(vo.getEmergencyLevel())){
                updateEntity.set("emergency_level",vo.getEmergencyLevel());
            }
            if(!StringUtils.isEmpty(vo.getEmergencyLevelName())){
                updateEntity.set("emergency_level_name",vo.getEmergencyLevelName());
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
            if(!StringUtils.isEmpty(vo.getProvinceId())){
                updateEntity.set("province_id",vo.getProvinceId());
            }
            if(!StringUtils.isEmpty(vo.getCityId())){
                updateEntity.set("city_id",vo.getCityId());
            }
            if(!StringUtils.isEmpty(vo.getCountyId())){
                updateEntity.set("county_id",vo.getCountyId());
            }
            if(!StringUtils.isEmpty(vo.getProvinceName())){
                updateEntity.set("province_name",vo.getProvinceName());
            }
            if(!StringUtils.isEmpty(vo.getCityName())){
                updateEntity.set("city_name",vo.getCityName());
            }
            if(!StringUtils.isEmpty(vo.getCountyName())){
                updateEntity.set("county_name",vo.getCountyName());
            }
            if(!StringUtils.isEmpty(vo.getGridId())){
                updateEntity.set("grid_id",vo.getGridId());
            }
            if(!StringUtils.isEmpty(vo.getGridName())){
                updateEntity.set("grid_name",vo.getGridName());
            }
            if(!StringUtils.isEmpty(vo.getImportanceLevel())){
                updateEntity.set("importance_level",vo.getImportanceLevel());
            }
            if(!StringUtils.isEmpty(vo.getDeleteFlag())){
                updateEntity.set("delete_flag",vo.getDeleteFlag());
            }
            if(!StringUtils.isEmpty(vo.getDevicePitch())){
                updateEntity.set("device_pitch",vo.getDevicePitch());
            }
            if(!StringUtils.isEmpty(vo.getDeviceHorizontal())){
                updateEntity.set("device_horizontal",vo.getDeviceHorizontal());
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
}
