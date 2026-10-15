package com.chinatower.platform.mongo;

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
import com.chinatower.platform.common.TableConstant;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @ClassName InterceptMongoMapper
 * @Deacription
 * @Author Liulg9
 * @Date 2024/08/29 16:15
 * @Version 1.0
 **/
@Slf4j
@Component
public class InterceptMongoMapper extends AbstractBaseMongoDAO<InterceptMongo> {

    /**
     * 创建 MongoDB 集合
     *
     * @param collectionName 集合名称
     * @param type           MongoDB 类型（主库、副库等）
     */
    public void mongoCreateTable(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.mongoCreate start -----------------------");
        long start = System.currentTimeMillis();
        // 确保集合名称不为空
        Assert.notNull(collectionName, "未指定操作的集合名");
        log.info(Constants.PARAMLIST, collectionName);
        Boolean isCollectionExist = this.getTemplate(type).collectionExists(collectionName);
        //判断集合是否存在
        if(!Boolean.TRUE.equals(isCollectionExist)){
            this.getTemplate(type).createCollection(collectionName);
        }
        // 调用 MongoDB 创建集合的方法
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
        // 确保集合名称不为空
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
     * 为指定集合创建索引
     *
     * @param collectionName 集合名称
     * @param type           MongoDB 类型（主库、副库等）
     * @param indexModels    索引模型列表
     */
    public void mongoCreateIndexs(String collectionName, MongoDBType type, List<IndexModel> indexModels) {
        log.info("---------------------- BaseMongoDAOImpl.mongoCreateIndexs start -----------------------");
        long start = System.currentTimeMillis();
        // 确保集合名称不为空
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, collectionName);
        // 创建集合并添加索引
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

    /**
     * 列出指定集合的索引
     *
     * @param collectionName 集合名称
     * @param type           MongoDB 类型（主库、副库等）
     * @return 索引信息列表
     */
    public ListIndexesIterable<Document> mongoListIndexs(String collectionName, MongoDBType type) {
        log.info("---------------------- BaseMongoDAOImpl.mongoListIndexs start -----------------------");
        long start = System.currentTimeMillis();
        // 确保集合名称不为空
        Assert.notNull(collectionName, Constants.SETNAME_NOT_SPECIFIED);
        log.info(Constants.PARAMLIST, collectionName);
        // 获取集合的索引列表
        ListIndexesIterable<Document> listIndexes = this.getTemplate(type).getCollection(collectionName).listIndexes();
        log.info("count result: {}", listIndexes);
        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- BaseMongoDAOImpl.mongoListIndexs end -----------------------");
        return listIndexes;
    }

    /**
     * 批量插入告警拦截数据到 MongoDB
     *
     * @param intercepts 报警拦截数据列表
     * @param alarmTime  报警时间（用于动态生成集合名称）
     */
    public void insertIntercept(List<ReceiveAlarmVO> intercepts,String alarmTime){
            // 将报警时间转换为日期对象
            Date date = DateUtils.string2Date(alarmTime);
            String time = DateUtils.dateToStringYM(date);
            // 动态生成集合名称，例如：warn_alarm_intercept_202312
            String collectionName = TableConstant.WARN_ALARM_INTERCEPT+"_"+time;
            List<InterceptMongo> mongoList = new ArrayList<>();
            // 遍历报警数据并转换为 MongoDB 存储对象
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
                // 设置其他字段
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
            // 批量插入数据到 MongoDB
            this.insertAll(mongoList,collectionName,MongoDBType.PRIMARY);
    }

    /**
     * 按月表 sub_scenes 同步拦截 Mongo（精确对齐，不按 warningTypeId 兜底）。
     * targetJson 为空时 unset 字段，纠正历史误写的场景主类型。
     *
     * @param warningOrderId 工单 ID（前 6 位为 YYYYMM）
     * @param targetJson     月表 sub_scenes JSON 文本，可为 null
     */
    public void syncSubScenes(String warningOrderId, String targetJson) {
        if (!StringUtils.hasText(warningOrderId) || warningOrderId.length() < 6) {
            return;
        }
        String tableName = TableConstant.WARN_ALARM_INTERCEPT + "_" + warningOrderId.substring(0, 6);
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
        // 打印更新内容和集合名称
        log.info(Constants.PARAMLIST, JSON.toJSON(update), collectionName);
       /* Criteria criteria = Criteria.where(accordingKey).is(accordingValue);
        Query query = Query.query(criteria);*/
        UpdateResult updateResult;
        // 根据是否批量更新执行对应的更新操作
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
     * 获取集合的所有索引
     * @param collectionName 集合名称
     * @param type MongoDB类型
     * @return 索引列表
     */
    public List<Map<String, Object>> getIndexes(String collectionName, MongoDBType type) {
        log.info("---------------------- OperateMongoMapper.getIndexes start -----------------------");
        long start = System.currentTimeMillis();
        Assert.notNull(collectionName, "集合名称不能为空");

        List<Map<String, Object>> indexes = new ArrayList<>();
        MongoTemplate template = this.getTemplate(type);

        try {
            // 获取集合的所有索引
            for (Document indexDoc : template.getCollection(collectionName).listIndexes()) {
                Map<String, Object> indexMap = new HashMap<>();
                indexMap.put("name", indexDoc.getString("name"));
                indexMap.put("key", indexDoc.get("key"));
                indexMap.put("unique", indexDoc.getBoolean("unique", false));
                indexes.add(indexMap);
            }
        } catch (Exception e) {
            log.error("获取集合[{}]索引失败", collectionName, e);
        }

        long end = System.currentTimeMillis();
        log.info(Constants.USETIME, end - start);
        log.info("---------------------- OperateMongoMapper.getIndexes end -----------------------");
        return indexes;
    }
}
