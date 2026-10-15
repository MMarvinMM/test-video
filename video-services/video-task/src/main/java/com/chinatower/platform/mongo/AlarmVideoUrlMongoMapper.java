package com.chinatower.platform.mongo;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.entity.dto.OperateMongo;
import com.chinatower.event.entity.vo.CaptureVideoVO;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.service.AbstractBaseMongoDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName AlarmVideoUrlMongoMapper
 * @Deacription
 * @Author Liulg9
 * @Date 2024/08/29 16:15
 * @Version 1.0
 **/
@Slf4j
@Component
public class AlarmVideoUrlMongoMapper extends AbstractBaseMongoDAO<CaptureVideoVO> {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 更新 MongoDB 中的 `video_url` 字段。
     * 遍历传入的 `operateMongoList`，根据 `warning_order_id` 在 MongoDB 中查找对应的数据记录，
     * 如果 `video_url` 存在，则更新该字段，同时更新 `modifier_time` 字段。
     *
     * @param operateMongoList 需要操作的 `CaptureVideoVO` 列表，包含 `warning_order_id`
     * @param table            MongoDB 表名前缀
     * @param list             包含 `warning_order_id` 和 `video_url` 的 `OrderInfoVO` 列表，用于匹配更新
     */
    public void updateVideoUrl(List<CaptureVideoVO> operateMongoList,String table,List<OrderInfoVO> list){
        log.info("updateVideoUrl-mongoDB修改实体operateMongoList:{}", JSON.toJSONString(operateMongoList));
        log.info("updateVideoUrl-mongoDB修改实体list:{}", JSON.toJSONString(list));
        // 遍历 `operateMongoList`，逐条处理数据
        for (CaptureVideoVO vo:operateMongoList) {
            // 获取表名
            String tableName = table +"_"+ vo.getWarningOrderId().substring(0, 6);

            // 构建查询条件，根据 `warning_order_id` 查找对应记录
            Query updateQuery = new Query();
            updateQuery.addCriteria(Criteria.where("warning_order_id").is(vo.getWarningOrderId()));
            // 构建更新内容
            Update updateEntity = new Update();
            updateEntity.set("modifier_time", DateUtils.currentDateToString());
            // 在 `list` 中查找与当前 `warning_order_id` 匹配的记录
            List<OrderInfoVO> filterList = list.stream().filter(orderInfoVO -> orderInfoVO.getWarningOrderId().equals(vo.getWarningOrderId())).collect(Collectors.toList());
            // 判断啊video_url是否存在
            if (!CollectionUtils.isEmpty(filterList) && !StringUtils.isEmpty(filterList.get(0).getVideoUrl())){
                updateEntity.set("video_url", filterList.get(0).getVideoUrl());
            }
            // 执行查询操作
            log.info("---------------------- BaseMongoDAOImpl.insert start -----------------------");
            long start = System.currentTimeMillis();
            log.info(com.chinatower.event.constants.Constants.PARAMLIST, JSON.toJSON(updateEntity), tableName);
            UpdateResult updateResult;
            updateResult =mongoTemplate.updateMulti(updateQuery, updateEntity,tableName);
            log.info("insert success: {}", updateResult);
            long end = System.currentTimeMillis();
            log.info(com.chinatower.event.constants.Constants.USETIME, end - start);
            log.info("---------------------- BaseMongoDAOImpl.insert end -----------------------");
        }
    }
}
