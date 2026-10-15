package com.chinatower.platform.consumer.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.consumer.dao.MongoUtilDAO;
import com.chinatower.platform.consumer.item.CanalBean;
import com.chinatower.platform.consumer.item.Constants;
import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 运营研判表、告警拦截表数据监听
 * @Author lyy
 * @Date 2023/03/16
 * @Version release_v1.5_80
 **/
@Slf4j
@Component
public class AlarmOperateService {

    @Autowired
    private MongoUtilDAO mongoUtilDAO;

    public void dealOperateMsg(String orderValue) {
        CanalBean canalBeans = JSON.parseObject(orderValue, CanalBean.class);
        this.dealOperateDataInfo(canalBeans);
    }

    public void dealInterceptMsg(String recordValue) {
        CanalBean canalBeans = JSON.parseObject(recordValue, CanalBean.class);
        this.dealInterceptDataInfo(canalBeans);
    }

    /**
     * 运营研判表数据信息
     * 根据 Canal 消息中的数据类型（INSERT、UPDATE、DELETE、CREATE）对 MongoDB 中的运营研判表数据进行增删改操作，
     * 并根据工单 ID 的月份分表存储。
     * @param canalBeans Canal 消息对象，包含表名、操作类型和数据等信息
     */
    private void dealOperateDataInfo(CanalBean canalBeans) {
        //解析json放入list集合
        if (!CollectionUtils.isEmpty(canalBeans.getData())) {
            List<Map<String, Object>> alarmOperateList = new ArrayList<>();
            for (Object obj: canalBeans.getData()) {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.putAll(JSON.parseObject(JSON.toJSONString(obj)));
                alarmOperateList.add(orderMap);
            }
            log.info("运营研判表数据消费："+JSON.toJSONString(alarmOperateList.stream().map(map -> map.get(Constants.WARN_ORDER_ID)).collect(Collectors.toList())));

            // 遍历解析后的数据，为每条数据添加月份字段（根据工单 ID 截取）
            alarmOperateList.forEach(item -> {
                // 从工单 ID 中截取月份信息（前 6 位表示年月）
                String orderId = item.get(Constants.WARN_ORDER_ID).toString();
                String dateYM = orderId.substring(0, 6);
                item.put(Constants.DATE_YM,dateYM);
            });
            // 根据月份字段（dateYM）对数据进行分组
            Map<Object, List<Map<String, Object>>> operateMap = alarmOperateList.stream().collect(Collectors.groupingBy(item->item.get(Constants.DATE_YM)));
            operateMap.entrySet().forEach(entry -> {
                // 拼接表名，例如 "warn_alarm_operate_202312"
                String tableName = Constants.WARN_ALARM_OPERATE + entry.getKey();
                insertDataToMongo(entry.getValue(), canalBeans.getType(), tableName, entry.getKey().toString());
            });
        } else {
            if (canalBeans.getType().equals(Constants.CREATE)) {
                // 如果是 CREATE 操作，检查表是否存在
                Boolean exists = mongoUtilDAO.collectionExists(canalBeans.getTable(), MongoDBType.PRIMARY);
                // 如果表不存在，则创建表
                if (!exists) {
                    mongoUtilDAO.mongoCreateTable(canalBeans.getTable(), MongoDBType.PRIMARY);
                }
            }
        }
    }

    /**
     * 告警拦截表数据信息
     * @param canalBeans
     */
    private void dealInterceptDataInfo(CanalBean canalBeans) {
        //解析json放入list集合
        if (!CollectionUtils.isEmpty(canalBeans.getData())) {
            List<Map<String, Object>> warningInterceptList = new ArrayList<>();
            for (Object obj: canalBeans.getData()) {
                Map<String, Object> recordMap = new HashMap<>();
                recordMap.putAll(JSON.parseObject(JSON.toJSONString(obj)));
                warningInterceptList.add(recordMap);
            }
            log.info("告警拦截表数据消费："+JSON.toJSONString(warningInterceptList.stream().map(map -> map.get(Constants.WARN_ORDER_ID)).collect(Collectors.toList())));

            warningInterceptList.forEach(item -> {
                //根据工单id截取月份
                String orderId = item.get(Constants.WARN_ORDER_ID).toString();
                String dateYM = orderId.substring(0, 6);
                item.put(Constants.DATE_YM,dateYM);
            });
            //根据月份进行分组
            Map<Object, List<Map<String, Object>>> interceptMap = warningInterceptList.stream().collect(Collectors.groupingBy(item->item.get(Constants.DATE_YM)));
            interceptMap.entrySet().forEach(entry -> {
                String tableName = Constants.WARN_ALARM_INTERCEPT + entry.getKey();
                insertDataToMongo(entry.getValue(), canalBeans.getType(), tableName, entry.getKey().toString());
            });
        } else {
            if (canalBeans.getType().equals(Constants.CREATE)) {
                //创建表
                Boolean exists = mongoUtilDAO.collectionExists(canalBeans.getTable(), MongoDBType.PRIMARY);
                if (!exists) {
                    mongoUtilDAO.mongoCreateTable(canalBeans.getTable(), MongoDBType.PRIMARY);
                }
            }
        }
    }

    /**
     * 将数据插入到 MongoDB 的指定表中，根据操作类型（INSERT、DELETE、UPDATE）进行处理。
     * 针对 INSERT 类型会检查是否需要创建索引，并在表中批量插入数据。
     *
     * @param dataList  数据列表，需要插入到 MongoDB 的数据
     * @param type      操作类型（INSERT、UPDATE、DELETE 等）
     * @param tableName 表名，表示数据要存储到的 MongoDB 表
     * @param dateYM    月份信息（格式：yyyyMM），用于创建动态索引
     */
    private void insertDataToMongo(List<Map<String, Object>> dataList, String type, String tableName, String dateYM) {
        //根据报文中的type字段进行判断, INSERT:新增、DELETE:删除、UPDATE:修改...
        if (type.equals(Constants.INSERT)) {
            try {
                mongoUtilDAO.insertAll(dataList, tableName, MongoDBType.PRIMARY);
                try {
                    //先检查索引是否已存在
                    boolean listIndexs = checkListIndexs(tableName, MongoDBType.PRIMARY);
                    if (!listIndexs) {
                        //需要创建索引的集合
                        List<IndexModel> indexModels = getIndexModels(tableName, dateYM);
                        mongoUtilDAO.mongoCreateIndexs(tableName, MongoDBType.PRIMARY, indexModels);
                    }
                } catch (Exception e) {
                    return;
                }
            } catch (Exception e) {
                log.error("同步表数据操作异常", e);
            }
        } else if (type.equals(Constants.UPDATE)) {
            // 如果是 UPDATE 类型，逐条更新数据
            for (Map<String, Object> orderDTO : dataList) {
                // 根据 WARN_ORDER_ID 构造查询条件
                Criteria criteria = Criteria.where(Constants.WARN_ORDER_ID).is(orderDTO.get(Constants.WARN_ORDER_ID));
                Query query = Query.query(criteria);
                // 删除旧数据，插入新数据
                mongoUtilDAO.remove(query, MongoDBType.PRIMARY, tableName);
                mongoUtilDAO.mongoInsert(orderDTO, MongoDBType.PRIMARY, tableName);
            }
        }
    }

    /**
     * 查询索引列
     * @param tableName
     * @param primary
     * @return
     */
    private boolean checkListIndexs(String tableName, MongoDBType primary) {
        boolean success = false;
        try {
            ListIndexesIterable<Document> indexList = mongoUtilDAO.mongoListIndexs(tableName, primary);
            for (Document document : indexList) {
                Object key = document.get("key");
                if (null != key && key instanceof Document) {
                    Document keyDocument = (Document) key;
                    if (keyDocument.containsKey(Constants.WARN_ORDER_ID)) {
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
     * 获取索引列
     * 根据表名和日期动态生成 MongoDB 的索引模型列表（IndexModel）。不同的表（如运营研判表、告警拦截表）有不同的索引字段集合。
     * @param table  表名，包含具体的表类型和日期后缀（如 "warn_alarm_operate202312"）
     * @param date  日期后缀（格式 yyyyMM），用于区分动态表
     * @return 索引模型列表（List<IndexModel>），包含要创建的索引配置
     */
    private List<IndexModel> getIndexModels(String table, String date) {
        List<IndexModel> indexModels = new ArrayList<>();
        String[] str = new String[0];
        if (table.equals(Constants.WARN_ALARM_OPERATE+date)) {
            // 如果表是运营研判表，设置对应的索引字段集合
            str = new String[]{Constants.INDEXFIELD_WARNING_ORDER_ID, Constants.INDEXFIELD_TENANT_ID, Constants.INDEXFIELD_PROVINCE_ID,
                    Constants.INDEXFIELD_CITY_ID, Constants.INDEXFIELD_COUNTY_ID, Constants.INDEXFIELD_CREATE_USER, Constants.INDEXFIELD_WARNING_TYPE_ID,
                    Constants.INDEXFIELD_EMERGENCY_LEVEL, Constants.INDEXFIELD_ALARM_TIME, Constants.INDEXFIELD_ALGORITHM_CODE, Constants.INDEXFIELD_DUPLICATE_ORDER_ID,
                    Constants.INDEX_DEL_TIME_RESULT, Constants.INDEX_DEL_ALARMTIME_TYPE};
        } else if (table.equals(Constants.WARN_ALARM_INTERCEPT+date)) {
            // 如果表是告警拦截表，设置对应的索引字段集合
            str = new String[]{Constants.INDEXFIELD_WARNING_ORDER_ID, Constants.INDEXFIELD_ALARM_TIME, Constants.INDEXFIELD_PROVINCE_CODE, Constants.INDEXFIELD_TENANT_ID,
                    Constants.INDEXFIELD_TASK_ID, Constants.INDEXFIELD_CITY_CODE, Constants.INDEXFIELD_COUNTY_CODE, Constants.INDEXFIELD_PROVINCE_CITY,
                    Constants.INDEXFIELD_WARNING_TYPE_ID, Constants.INDEXFIELD_LONGITUDE_LATITUDE, Constants.INDEXFIELD_DEVICE_CODE, Constants.INDEXFIELD_ALARM_BODY,
                    Constants.INDEXFIELD_ALGORITHM_CODE, Constants.INDEXFIELD_CREATE_TIME, Constants.INDEX_TENANTID_TIME_FLAG_RESULT, Constants.INDEX_TIME_FLAG_RESULT};
        }
        BasicDBObject indexObj ;
        for (String index : str) {
            IndexOptions aIndexOptions;
            // 创建索引选项，设置索引为后台创建（background = true），并指定索引名称
            aIndexOptions = new IndexOptions().background(true).name(index);
            // 创建一个 BasicDBObject 用于存储索引字段和排序方式
            indexObj = new BasicDBObject();
            // 特殊处理：如果索引字段是 "alarm_time"，设置为降序索引（-1）
            if(index.equals(Constants.INDEXFIELD_ALARM_TIME)){
                indexObj.put(index, Constants.INDEXASC_MINUS_ONE);
            }else{
                indexObj.put(index, Constants.INDEXASC_ONE); // 其他索引字段默认升序
            }
            // 将索引模型（字段 + 选项）添加到索引模型列表中
            indexModels.add(new IndexModel(indexObj, aIndexOptions));
        }
        return indexModels;
    }
}
