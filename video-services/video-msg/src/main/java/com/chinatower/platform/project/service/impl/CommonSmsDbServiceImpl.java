package com.chinatower.platform.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.platform.common.constant.MsgConstants;
import com.chinatower.platform.common.utils.CommonUtil;
import com.chinatower.platform.entity.msg.BsJpushRecord;
import com.chinatower.platform.entity.msg.BsSmsRecord;
import com.chinatower.platform.entity.request.JgPushRequest;
import com.chinatower.platform.project.mapper.BsJpushRecordMapper;
import com.chinatower.platform.project.mapper.BsSmsRecordMapper;
import com.chinatower.platform.project.mongodb.JpushMongoDAO;
import com.chinatower.platform.project.mongodb.domain.BsJpushRecordMongo;
import com.chinatower.platform.project.service.CommonSmsDbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能说明：  短信数据操作类
 *
 * @author 王建霞
 * @date 2021/10/12 15:01
 */
@Slf4j
@Service
public class CommonSmsDbServiceImpl implements CommonSmsDbService {

    /**
     * 注入短信发送模块dao
     */
    @Resource
    private BsSmsRecordMapper bsSmsRecordMapper;

    @Resource
    private BsJpushRecordMapper bsJpushRecordMapper;

    @Resource
    JpushMongoDAO jpushMongoDAO;

    @Resource
    MongoTemplate primaryMongoTemplate;

    public static final String ALIAS_STR = "alias";

    public static final String COUNT_STR = "count";

    /**
     * 功能说明：保存短信记录 (原位置：CommonSmsServiceImpl   SmsServiceImpl共用)
     *
     * @param bsSmsRecord
     * @param type
     * @return void
     * @author 王建霞
     * @date 2021/10/12 15:10
     **/
    @Override
    @Async("msgTaskExecutor")
    public void saveSmsRecord(BsSmsRecord bsSmsRecord, String type) {
        String success = MsgConstants.SMS_SUCCESS;
        String fail = MsgConstants.SMS_ERROR;
        log.info("保存短信记录");
        String id = CommonUtil.getUuid();
        Date todayDate = new Date();
        bsSmsRecord.setId(id);
        bsSmsRecord.setCreateTime(todayDate);
        bsSmsRecord.setSendTime(todayDate);
        bsSmsRecord.setDelFlag("0");
        if (fail.equals(type)) {
            bsSmsRecord.setSendResult(fail);
            int resultE = bsSmsRecordMapper.insertSelective(bsSmsRecord);
            log.info("保存短信记录结果：" + resultE);
        } else {
            bsSmsRecord.setSendResult(success);
            int result = bsSmsRecordMapper.insertSelective(bsSmsRecord);
            log.info("保存短信记录结果：" + result);
        }
    }

    @Override
    @Async("msgTaskExecutor")
    public void saveJpushRecord(BsJpushRecord bsJpushRecord,String type) {
        String success = MsgConstants.SMS_SUCCESS;
        String fail = MsgConstants.SMS_ERROR;
        String id = CommonUtil.getUuid();
        Date todayDate = new Date();
        bsJpushRecord.setId(id);
        bsJpushRecord.setCreateTime(todayDate);
        bsJpushRecord.setSendTime(todayDate);
        bsJpushRecord.setDelFlag("0");
        if (fail.equals(type)) {
            bsJpushRecord.setSendResult(fail);
            int resultE = bsJpushRecordMapper.insertSelective(bsJpushRecord);
            log.info("保存极光推送记录结果：" + resultE);
        } else {
            bsJpushRecord.setSendResult(success);
            int result = bsJpushRecordMapper.insertSelective(bsJpushRecord);
            log.info("保存极光推送记录结果：" + result);
        }
    }

    @Override
    @Async("msgTaskExecutor")
    public void saveJpushRecordMongo(BsJpushRecordMongo bsJpushRecord, String type) {
        //异步保存 极光消息 到mongo
        setBsJpushRecord(bsJpushRecord);
        if (MsgConstants.SMS_ERROR.equals(type)) {
            log.info("发送极光推送失败，{},不保存数据库", bsJpushRecord);
//            bsJpushRecord.setSendResult(MsgConstants.SMS_ERROR);
        } else {
            bsJpushRecord.setSendResult(MsgConstants.SMS_SUCCESS);
        }
        jpushMongoDAO.insert(bsJpushRecord, MongoDBType.PRIMARY);
        log.info("保存极光推送记录结果");
    }

    @Override
    public void saveJpushRecordMongoBatch(List<BsJpushRecordMongo> bsJpushRecords) {
        //异步保存 极光消息 到mongo
        jpushMongoDAO.insertAll(bsJpushRecords, MsgConstants.JPUSH_RECORD_COLLECTION_NAME_VALUE , MongoDBType.PRIMARY);
        log.info("保存极光推送记录结果");
    }

    private void setBsJpushRecord(BsJpushRecordMongo bsJpushRecord) {
        String id = CommonUtil.getUuid();
        Date todayDate = new Date();
        bsJpushRecord.setId(id);
        bsJpushRecord.setCreateTime(todayDate);
        bsJpushRecord.setSendTime(todayDate);
        bsJpushRecord.setDelFlag("0");
        bsJpushRecord.setIsRead("0");
        bsJpushRecord.setType(bsJpushRecord.getType());
    }

    @Override
    public int getNoReadByUser(String alias) {
        QueryEntity queryEntity = new QueryEntity();
        queryEntity.put(MsgConstants.JPUSH_RECORD_COLLECTION_NAME, MsgConstants.JPUSH_RECORD_COLLECTION_NAME_VALUE);
        queryEntity.put(MsgConstants.JPUSH_RECORD_ENTITY_CLASS, BsJpushRecordMongo.class);
        queryEntity.eq(MsgConstants.JPUSH_RECORD_FIELD_SEND_IS_READ, MsgConstants.JPUSH_RECORD_FIELD_SEND_IS_READ_0);
        queryEntity.eq(MsgConstants.JPUSH_RECORD_FIELD_SEND_RESULT, MsgConstants.JPUSH_RECORD_FIELD_SEND_RESULT_1);
        queryEntity.eq(MsgConstants.JPUSH_RECORD_FIELD_DEL_FLAG, MsgConstants.JPUSH_RECORD_FIELD_DEL_FLAG_0);
        queryEntity.eq(MsgConstants.JPUSH_RECORD_FIELD_ALIAS, alias);
        Long count = jpushMongoDAO.count(queryEntity, MongoDBType.PRIMARY);
        return count.intValue();
    }

    @Override
    public Map<String, Integer> getNoReadByUserList(List<JgPushRequest> pushNews) {
        List<String> alias = pushNews.stream().map(JgPushRequest::getAlias).collect(Collectors.toList());
        log.info("查询的别名：{}", alias);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where(ALIAS_STR).in(alias)
                                .and("delFlag").is("0")
                                .and("sendResult").is("1")
                                .and("isRead").is("0")
                ),
                Aggregation.group(ALIAS_STR).count().as(COUNT_STR),
                Aggregation.project(COUNT_STR).and("_id").as(ALIAS_STR).andExclude("_id")
        );
        AggregationResults<Map> aggregate = primaryMongoTemplate.aggregate(aggregation, "bs_jpush_record", Map.class);
        List<Map> aliasAndCount = aggregate.getMappedResults();
        log.info(aliasAndCount.toString());
        return aliasAndCount.stream().collect(Collectors.toMap(p -> String.valueOf(p.get(ALIAS_STR)), p -> Integer.parseInt(String.valueOf(p.get(COUNT_STR)))));
    }

    @Override
    @Async("msgTaskExecutor")
    public void saveBatchSmsRecord(BsSmsRecord bsSmsRecord, List<String> phoneList, List<BsSmsRecord> list, String type) {
        String success = MsgConstants.SMS_SUCCESS;
        String fail = MsgConstants.SMS_ERROR;
        log.info("保存批量短信记录:{}",phoneList);
        Date todayDate = new Date();
        JSONObject jsonObject = new JSONObject();
        if ( 1 == phoneList.size()) {
            jsonObject.put(phoneList.get(0), bsSmsRecord.getMsgId());
        } else {
            jsonObject = (JSONObject) JSON.parse(bsSmsRecord.getMsgId());
        }
        for (String item : phoneList) {
            log.info(bsSmsRecord.getMsgId());
            BsSmsRecord bsSmsRecordTem = new BsSmsRecord();
            BeanUtils.copyProperties(bsSmsRecord, bsSmsRecordTem);
            String id = CommonUtil.getUuid();
            String msgId = (String) jsonObject.get(item);
            bsSmsRecordTem.setId(id);
            bsSmsRecordTem.setMsgId(msgId);
            bsSmsRecordTem.setCreateTime(todayDate);
            bsSmsRecordTem.setSendTime(todayDate);
            bsSmsRecordTem.setDelFlag("0");
            bsSmsRecordTem.setPhone(item);
            if (fail.equals(type)) {
                bsSmsRecordTem.setSendResult(fail);
            } else {
                bsSmsRecordTem.setSendResult(success);
            }
            list.add(bsSmsRecordTem);
        }
    }
}