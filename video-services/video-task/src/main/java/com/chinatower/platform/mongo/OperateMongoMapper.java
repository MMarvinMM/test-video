package com.chinatower.platform.mongo;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.vo.ExtraInfoVO;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.OperateMongo;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.util.subscenes.SubScenesMongoHelper;
import com.chinatower.event.util.DateUtils;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.chinatower.platform.common.TableConstant;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
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
 * @Author Liulg9
 * @Date 2024/08/29 16:15
 * @Version 1.0
 **/
@Slf4j
@Component
public class OperateMongoMapper extends AbstractBaseMongoDAO<OperateMongo> {

    /**
     * 创建 MongoDB 集合
     *
     * @param collectionName 集合名称
     * @param type           MongoDB 类型（主库、副库等）
     */
    public void mongoCreateTable(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.mongoCreate start -----------------------");
        long start = System.currentTimeMillis();
        // 检查集合名称是否为空
        Assert.notNull(collectionName, "未指定操作的集合名");
        log.info(Constants.PARAMLIST, collectionName);
        Boolean isCollectionExist = this.getTemplate(type).collectionExists(collectionName);
        //判断集合是否存在
        if(!Boolean.TRUE.equals(isCollectionExist)){
            this.getTemplate(type).createCollection(collectionName);
        }
        // 调用 MongoDB 创建集合方法
//        this.getTemplate(type).createCollection(collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.mongoCreate end -----------------------");
    }

    /**
     * 判断 MongoDB 集合是否存在
     *
     * @param collectionName 集合名称
     * @param type           MongoDB 类型（主库、副库等）
     * @return Boolean 集合是否存在
     */
    public Boolean collectionExists(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.collectionExists start -----------------------");
        long start = System.currentTimeMillis();
        // 检查集合名称是否为空
        Assert.notNull(collectionName, "未指定操作的集合名");
        log.info(Constants.PARAMLIST, collectionName);
        // 判断集合是否存在
        Boolean isExists = this.getTemplate(type).collectionExists(collectionName);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.collectionExists end -----------------------");
        return isExists;
    }

    /**
     * 插入运营研判数据至 MongoDB
     *
     * @param operates  运营研判数据列表
     * @param alarmTime 报警时间（用于动态生成集合名称）
     */
    public void insertOperate(List<ReceiveAlarmVO> operates, String alarmTime) {
        // 将报警时间转换为日期对象并生成集合名称
        Date date = DateUtils.string2Date(alarmTime);
        String time = DateUtils.dateToStringYM(date);
        String collectionName = TableConstant.WARN_ALARM_OPERATE + "_" + time;
        List<OperateMongo> mongoList = new ArrayList<>();
        // 遍历报警数据并转换为 MongoDB 存储对象
        for (ReceiveAlarmVO vo : operates) {
            OperateMongo mongo = new OperateMongo();
            BeanUtil.copyProperties(vo, mongo);
            // 解析额外信息字段
            ExtraInfoVO infoVO = JSONObject.parseObject(JSON.toJSONString(vo.getExtraInfo()),ExtraInfoVO.class);
            if(infoVO != null){
                mongo.setPresetId(infoVO.getPresetId());
                mongo.setNoticeResource(String.valueOf(infoVO.getNoticeResource()));
                mongo.setMsgId(infoVO.getMsgId());
            }
            // 设置常用字段
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
        // 批量插入数据到 MongoDB
        this.insertAll(mongoList, collectionName, MongoDBType.PRIMARY);
    }

    /**
     * 插入本地运营数据至 MongoDB
     *
     * @param vo        接收报警数据对象
     * @param alarmTime 报警时间（用于动态生成集合名称）
     */
    public void insertLocalOperate(ReceiveAlarmVO vo, String alarmTime) {
        // 将报警时间转换为日期对象并生成集合名称
        Date date = DateUtils.string2Date(alarmTime);
        String time = DateUtils.dateToStringYM(date);
        String collectionName = TableConstant.WARN_LOCAL_ALARM_OPERATE + "_" + time;
        List<OperateMongo> mongoList = new ArrayList<>();

            OperateMongo mongo = new OperateMongo();
            BeanUtil.copyProperties(vo, mongo);
            // 解析额外信息字段
            ExtraInfoVO infoVO = JSONObject.parseObject(JSON.toJSONString(vo.getExtraInfo()),ExtraInfoVO.class);
            if(infoVO != null){
                mongo.setPresetId(infoVO.getPresetId());
                mongo.setNoticeResource(String.valueOf(infoVO.getNoticeResource()));
                mongo.setMsgId(infoVO.getMsgId());
            }
            // 设置常用字段
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

        // 批量插入数据到 MongoDB
        this.insertAll(mongoList, collectionName, MongoDBType.PRIMARY);
    }

    /**
     * 更新 MongoDB 集合中的记录
     *
     * @param query         查询条件
     * @param update        更新内容
     * @param type          MongoDB 类型（主库、副库等）
     * @param collectionName 集合名称
     * @param multi         是否批量更新
     */
    public void mongoUpdate(Query query,Update update, MongoDBType type, String collectionName, boolean multi) {
        log.info("---------------------- BaseMongoDAOImpl.insert start -----------------------");
        long start = System.currentTimeMillis();
        log.info(Constants.PARAMLIST, JSON.toJSON(update), collectionName);
       /* Criteria criteria = Criteria.where(accordingKey).is(accordingValue);
        Query query = Query.query(criteria);*/
        UpdateResult updateResult;
        // 根据是否批量更新执行对应操作
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
     * 按月表 sub_scenes 同步运营研判 Mongo（精确对齐，不按 warningTypeId 兜底）。
     * targetJson 为空时 unset 字段，纠正历史误写的场景主类型。
     *
     * @param warningOrderId 工单 ID（前 6 位为 YYYYMM）
     * @param targetJson     月表 sub_scenes JSON 文本，可为 null
     */
    public void syncSubScenes(String warningOrderId, String targetJson) {
        if (!StringUtils.hasText(warningOrderId) || warningOrderId.length() < 6) {
            return;
        }
        String tableName = TableConstant.WARN_ALARM_OPERATE + "_" + warningOrderId.substring(0, 6);
        Query updateQuery = new Query();
        updateQuery.addCriteria(Criteria.where(Constants.RES_WARNING_ORDER_ID).is(warningOrderId));
        Update updateEntity = new Update();
        List<String> codes = SubScenesMongoHelper.parseSubScenesExact(targetJson);
        if (CollectionUtils.isEmpty(codes)) {
            updateEntity.unset(SubScenesMongoHelper.FIELD_SUB_SCENES);
        } else {
            updateEntity.set(SubScenesMongoHelper.FIELD_SUB_SCENES, codes);
        }
        this.mongoUpdate(updateQuery, updateEntity, MongoDBType.PRIMARY, tableName, true);
    }

    /**
     * 更新运营研判记录
     *
     * @param operateMongoList 待更新的运营研判记录列表
     */
    public void updateOperate(List<OperateMongo> operateMongoList){
        log.info("updateOperate-运营研判修改实体:{}", JSON.toJSONString(operateMongoList));
        for (OperateMongo vo:operateMongoList) {
            // 获取表名
            String tableName = TableConstant.WARN_ALARM_OPERATE +"_"+ vo.getWarningOrderId().substring(0, 6);

            // 构建查询条件
            Query updateQuery = new Query();
            updateQuery.addCriteria(Criteria.where(Constants.RES_WARNING_ORDER_ID).is(vo.getWarningOrderId()));
            // 构建更新内容
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
            // 执行更新操作
            this.mongoUpdate(updateQuery,updateEntity,MongoDBType.PRIMARY, tableName,true);
        }

    }
}
