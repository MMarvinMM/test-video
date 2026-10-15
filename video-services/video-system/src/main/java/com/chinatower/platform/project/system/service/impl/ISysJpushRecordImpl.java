package com.chinatower.platform.project.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.event.api.ITradeWarningOrderService;
import com.chinatower.event.entity.dto.TradeAlarmOrderDTO;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.UpdateEntity;
import com.chinatower.platform.model.dto.WarningOrderMsgDto;
import com.chinatower.platform.project.system.domain.BsJpushRecordMongo;
import com.chinatower.platform.project.system.domain.vo.SysJpushRecord;
import com.chinatower.platform.project.system.dao.SysJpushMongoDAO;
import com.chinatower.platform.project.system.service.ISysJpushRecord;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.chinatower.platform.project.system.constants.MongoConstants.*;

@Slf4j
@Service
public class ISysJpushRecordImpl implements ISysJpushRecord {


    @Resource
    private ITradeWarningOrderService tradeWarningOrderService;

    @Resource
    private SysJpushMongoDAO sysJpushMongoDAO;
    @Resource
    private MongoTemplate mongoTemplate;

    private final String HOUR_CHN = "小时";
    private final String MIN_CHN = "分";
    private final String SEC_CHN = "秒";

    private final String COLON = ":";

    private final String MONGO_id = "_id";

    private final String COUNT = "count";

    @Override
    public List<BsJpushRecordMongo> getJpushRecordlist(SysJpushRecord vo) {

        Query query = new Query();
        query.addCriteria(Criteria.where(JPUSH_FIELD_ALIAS).is(vo.getAlias()));
        query.addCriteria(Criteria.where(JPUSH_FIELD_DEL_FLAG).is(JPUSH_FIELD_DEL_FLAG_0));
        query.addCriteria(Criteria.where(JPUSH_FIELD_SEND_RESULT).is(JPUSH_FIELD_SEND_RESULT_1));
        query.addCriteria(Criteria.where(JPUSH_FIELD_SEND_IS_READ).is(JPUSH_FIELD_SEND_IS_READ_0));
        query.addCriteria(Criteria.where(JPUSH_FIELD_TYPE).is(JPUSH_FIELD_TYPE_1));
        query.with(Sort.by(new Sort.Order(Sort.Direction.DESC, JPUSH_FIELD_CREATE_TIME)));
        List<BsJpushRecordMongo> jpushRecordlist = new ArrayList<>();
        log.info("查询mongo开始");
        try {
            MongoCursor<Document> cursor =
                    //指定查询集合
                    mongoTemplate.getCollection(JPUSH_COLLECTION_NAME_VALUE)
                            //组装查询条件
                            .find(query.getQueryObject())
                            .sort(query.getSortObject())
                            .skip((vo.getPageNum() - 1) * vo.getPageSize())
                            .limit(vo.getPageSize())
                            //设置游标查询不超时
                            .noCursorTimeout(true)
                            .batchSize(1000)
                            .cursor();
            Document doc;
            //Map map;
            while (cursor.hasNext()) {
                doc = cursor.next();
                log.info("doc:{}", doc);
                BsJpushRecordMongo bsJpushRecordMongo = new BsJpushRecordMongo();
                bsJpushRecordMongo.setId(String.valueOf(doc.get("_id")));
                bsJpushRecordMongo.setAlias(String.valueOf(doc.get("alias")));
                bsJpushRecordMongo.setNotificationTitle(String.valueOf(doc.get("notificationTitle")));
                bsJpushRecordMongo.setMsgTitle(String.valueOf(doc.get("msgTitle")));
                bsJpushRecordMongo.setMsgContent(String.valueOf(doc.get("msgContent")));
                bsJpushRecordMongo.setExtrasparam(String.valueOf(doc.get("extrasparam")));
                bsJpushRecordMongo.setSendTime(DateUtil.date((Date) doc.get("sendTime")));
                bsJpushRecordMongo.setSendResult(String.valueOf(doc.get("sendResult")));
                bsJpushRecordMongo.setFailReason(String.valueOf(doc.get("failReason")));
                bsJpushRecordMongo.setDelFlag(String.valueOf(doc.get("delFlag")));
                bsJpushRecordMongo.setDeptId(String.valueOf(doc.get("deptId")));
                bsJpushRecordMongo.setIsRead(String.valueOf(doc.get("isRead")));
                bsJpushRecordMongo.setCreateBy(String.valueOf(doc.get("createBy")));
                bsJpushRecordMongo.setCreateTime(DateUtil.date((Date) doc.get("createTime")));
                bsJpushRecordMongo.setUpdateBy(String.valueOf(doc.get("updateBy")));
                bsJpushRecordMongo.setUpdateTime(DateUtil.date((Date) doc.get("updateTime")));
                bsJpushRecordMongo.setType(String.valueOf(doc.get("type")));
                bsJpushRecordMongo.setOrderStatusName(String.valueOf(doc.get("orderStatusName")));
                bsJpushRecordMongo.setAddress(String.valueOf(doc.get("address")));
                jpushRecordlist.add(bsJpushRecordMongo);
            }
            log.info("查询mongo结束");
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info(jpushRecordlist.toString());
        List<String> orderIds = new ArrayList<>();
        jpushRecordlist.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getExtrasparam())) {
                JSONObject jsonObject = JSONObject.parseObject(item.getExtrasparam());
                String orderId = jsonObject.getString("orderId");
                if (StringUtils.isEmpty(orderId)) {
                    String warningOrderId = jsonObject.getString("warningOrderId");
                    if (StringUtils.isNotEmpty(warningOrderId)) {
                        orderIds.add(warningOrderId);
                    }
                } else {
                    orderIds.add(orderId);
                }
            }
        });
        if (orderIds.isEmpty()) {
            return jpushRecordlist;
        }
        TradeAlarmOrderDTO alarmOrderDTO = new TradeAlarmOrderDTO();
        alarmOrderDTO.setWarningOrderIds(orderIds);
        alarmOrderDTO.setPageNum(1);
        alarmOrderDTO.setPageSize(orderIds.size());
        alarmOrderDTO.setAlarmTimeStart(DateUtil.parseDateTime("2021-07-01 00:00:00"));
        alarmOrderDTO.setAlarmTimeEnd(DateUtil.date());
        //不过滤数据权限标识（必传1）
        alarmOrderDTO.setScopeFlag("1");
        log.info("请求event服务/alarm/orderForTrade/getOrderList入参:{}", JSON.toJSONString(alarmOrderDTO));
        TableDataInfo orderList = tradeWarningOrderService.getOrderList(alarmOrderDTO);
        log.info("请求event服务/alarm/orderForTrade/getOrderList返回:{}", JSON.toJSONString(orderList));
        if(orderList == null || orderList.getRows() == null || orderList.getRows().size() == 0){
            return jpushRecordlist;
        }
        List<JSONObject> rows = (List<JSONObject>) (orderList.getRows());
        Map<String, WarningOrderListVO> idAndOrderMap = rows.stream()
                .map(item -> JSONObject.parseObject(JSON.toJSONString(item), WarningOrderListVO.class))
                .collect(Collectors.toMap(WarningOrderListVO::getOrderId, warningOrderListVO -> warningOrderListVO, (a, b) -> b));
        jpushRecordlist.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getExtrasparam())) {
                JSONObject parse = JSONObject.parseObject(item.getExtrasparam());
                String orderId = parse.getString("orderId");
                if (StringUtils.isEmpty(orderId)) {
                    orderId = parse.getString("warningOrderId");
                }
                WarningOrderListVO warningOrderListVO = idAndOrderMap.get(orderId);
                WarningOrderMsgDto warningOrderMsgDto = new WarningOrderMsgDto();
                if (ObjectUtil.isNotEmpty(warningOrderListVO)) {
                    BeanUtils.copyProperties(warningOrderListVO, warningOrderMsgDto);
                    if (null != warningOrderMsgDto.getDuration()) {
                        String duration = warningOrderMsgDto.getDuration().replaceAll(HOUR_CHN, COLON).replaceAll(MIN_CHN, COLON).replaceAll(SEC_CHN, "");
                        warningOrderMsgDto.setDuration(duration);
                    }
                    if (null != warningOrderMsgDto.getShowTime()) {
                        String showTime = warningOrderMsgDto.getShowTime().replaceAll(HOUR_CHN, COLON).replaceAll(MIN_CHN, COLON).replaceAll(SEC_CHN, "");
                        warningOrderMsgDto.setShowTime(showTime);
                    }
                    item.setWarningOrder(warningOrderMsgDto);
                }
            }
        });
        return jpushRecordlist;
    }

    /**
     * 修改极光推送记录状态
     *
     * @param vo
     * @return 结果
     */
    @Override
    public int updateJpushRecordById(SysJpushRecord vo) {
        QueryEntity queryEntity = getBaseJpushQueryEntity();
        queryEntity.eq(JPUSH_FIELD_ID, vo.getId());
        return updateJpushIsReadByMongo(vo, queryEntity);
    }

    @Override
    public int countJpushRecord(SysJpushRecord vo) {
        return getJpushRecord(vo);
    }

    private int getJpushRecord(SysJpushRecord vo) {
        String pipeline = "[{\n" +
                "    '$match': {\n" +
                "        'sendResult': '1',\n" +
                "        'alias': '" + vo.getAlias() + "',\n" +
                "        'isRead': '0',\n" +
                "        'delFlag': '0',\n" +
                "        'type': '1'\n" +
                "    }\n" +
                "}, {\n" +
                "    '$limit': 100\n" +
                "}, {\n" +
                "    '$group': {\n" +
                "        '_id': 1,\n" +
                "        'count': {\n" +
                "            '$sum': 1\n" +
                "        }\n" +
                "    }\n" +
                "}]";
        String command = "{'aggregate':'" + JPUSH_COLLECTION_NAME_VALUE + "','pipeline':" +
                pipeline
                + ",'cursor': {'batchSize': 100}}";
        log.info(command);
        Document document = mongoTemplate.executeCommand(command);
        Map<String, Object> o1 = (Map<String, Object>) document.get("cursor");
        ArrayList list = (ArrayList) o1.get("firstBatch");
        if (!list.isEmpty()) {
            Map<String, Object> res = (Map<String, Object>) list.get(0);
            return (int) res.get("count");
        }
        return 0;
    }


    /**
     * 修改极光推送记录状态
     * @Author liuxt
     * @param vo
     * @return 结果
     */
    @Override
    public int updateJpushRecord(SysJpushRecord vo) {
        //2023-01-28 修改为mongo操作
        QueryEntity queryEntity = getBaseJpushQueryEntity();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(vo.getId())) {
            queryEntity.eq(JPUSH_FIELD_ID, vo.getId());
        }
        queryEntity.eq(JPUSH_FIELD_ALIAS, vo.getAlias());
        queryEntity.eq(JPUSH_FIELD_DEL_FLAG, JPUSH_FIELD_DEL_FLAG_0);
        queryEntity.eq(JPUSH_FIELD_SEND_RESULT, JPUSH_FIELD_SEND_RESULT_1);
        queryEntity.eq(JPUSH_FIELD_SEND_IS_READ, JPUSH_FIELD_SEND_IS_READ_0);
        if (StringUtils.isNotEmpty(vo.getType())) {
            queryEntity.eq(JPUSH_FIELD_TYPE, vo.getType());
        } else {
            //现阶段  消息类型 1、4都在一个列表中展示。以此为适配
            queryEntity.eq(JPUSH_FIELD_TYPE, JPUSH_FIELD_TYPE_1);
        }

        return updateJpushIsReadByMongo(vo, queryEntity);
    }


    @Override
    public Integer getJpushRecordlistCount(SysJpushRecord vo) {
        return getJpushRecord(vo);

    }

    /**
     * 更新极光消息 已读状态
     *
     * @param vo 极光实体
     * @param queryEntity mongo查询entity
     * @return
     */
    private int updateJpushIsReadByMongo(SysJpushRecord vo, QueryEntity queryEntity) {
        UpdateEntity updateEntity = new UpdateEntity();
        updateEntity.set(JPUSH_FIELD_SEND_IS_READ, JPUSH_FIELD_SEND_IS_READ_1);
        updateEntity.set(JPUSH_FIELD_UPDATE_TIME, DateUtil.now());
        updateEntity.set(JPUSH_FIELD_UPDATE_BY, vo.getUpdateBy());

        Long res = sysJpushMongoDAO.updateMulti(queryEntity, updateEntity, MongoDBType.PRIMARY);
        return res.intValue();
    }


    private QueryEntity getBaseJpushQueryEntity() {
        QueryEntity queryEntity = new QueryEntity();
        queryEntity.put(JPUSH_COLLECTION_NAME, JPUSH_COLLECTION_NAME_VALUE);
        queryEntity.put(JPUSH_ENTITY_CLASS, BsJpushRecordMongo.class);
        return queryEntity;
    }
}
