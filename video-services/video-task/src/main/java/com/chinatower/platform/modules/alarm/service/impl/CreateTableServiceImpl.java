package com.chinatower.platform.modules.alarm.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.util.DateUtils;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.common.TableConstant;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.modules.alarm.dao.JudgeStatisticsDAO;
import com.chinatower.platform.modules.alarm.service.CreateTableService;
import com.chinatower.platform.mongo.InterceptMongoMapper;
import com.chinatower.platform.mongo.OperateMongoMapper;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.mongodb.BasicDBObject;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/29 19:46
 */
@Service
@Slf4j
public class CreateTableServiceImpl implements CreateTableService {

    @Autowired
    private JudgeStatisticsDAO judgeStatisticsDAO;
    @Autowired
    private InterceptMongoMapper interceptMongoMapper;
    @Autowired
    private OperateMongoMapper operateMongoMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SystemFeignClient systemFeignClient;

    /**
     * 创建表
     * @param alarmTime
     */
    @Override
    public void createNewTable(String alarmTime) {
        try{
            Date date;
            if(StringUtils.isEmpty(alarmTime)){
                date = new Date();
            }else {
                date = DateUtils.string2Date(alarmTime);
            }
            String oldYM = DateUtils.dateToStringYM(date);
            String newYM = DateUtils.dateToStringYM(DateUtils.getDateAfter(date, 1));
            // 如果新年月和旧年月不同，则创建表
            if (!(oldYM).equalsIgnoreCase(newYM)) {
                judgeStatisticsDAO.createNewTable(TableConstant.WARN_ORDER_TEBLE + newYM, TableConstant.WARN_ORDER_TEBLE + oldYM);
                judgeStatisticsDAO.createNewTable(TableConstant.WARN_ORDER_PROCESS_TEBLE + newYM, TableConstant.WARN_ORDER_PROCESS_TEBLE + oldYM);
                try {
                    //创建流程环节用户处理表
                    judgeStatisticsDAO.createNewTable(TableConstant.VM_LINK_USER_DEAL + newYM, TableConstant.VM_LINK_USER_DEAL + oldYM);
                } catch (Exception e) {
                    log.error("流程环节用户处理表创建失败:", e);
                }
                try{
                    interceptMongoMapper.mongoCreateTable(TableConstant.WARN_ALARM_INTERCEPT+"_"+newYM, MongoDBType.PRIMARY);
                    //回收站表创建索引
                    this.checkIndexsExist(TableConstant.WARN_ALARM_INTERCEPT+"_"+newYM, newYM, Constants.ZERO);
                    operateMongoMapper.mongoCreateTable(TableConstant.WARN_ALARM_OPERATE+"_"+newYM,MongoDBType.PRIMARY);
                    //运营研判表创建索引
                    this.checkIndexsExist(TableConstant.WARN_ALARM_OPERATE+"_"+newYM, newYM, Constants.ZERO);
                    //运营研判表创建地理位置索引
                    this.checkAndCreateLocation2dsphereIndex(TableConstant.WARN_ALARM_OPERATE+"_"+newYM);
                    operateMongoMapper.mongoCreateTable(TableConstant.WARN_LOCAL_ALARM_OPERATE+"_"+newYM,MongoDBType.PRIMARY);
                    //属地运营研判表创建索引
                    this.checkIndexsExist(TableConstant.WARN_LOCAL_ALARM_OPERATE+"_"+newYM, newYM, Constants.ONE);
                }catch (Exception e){
                    log.info("创建回收站运营研判mongo表失败:",e);
                }
            }
        }catch (Exception e){
            log.error("createNewTable异常:"+e);
        }
    }


    /**
     * 检查索引是否存在
     * @param tableName
     * @param dateYM
     * @param flag 1-中心mongo、2-属地es
     */
    private void checkIndexsExist(String tableName, String dateYM, String flag) {
        try {
            // 先检查索引是否已存在
            boolean listIndexs = checkListIndexs(tableName, MongoDBType.PRIMARY);
            if (!listIndexs) {
                //需要创建索引的集合
                List<IndexModel> indexModels = getIndexModels(tableName, dateYM);
                log.info("mongo表创建索引,获取索引列集合 {}", JSONObject.toJSONString(indexModels));
                interceptMongoMapper.mongoCreateIndexs(tableName, MongoDBType.PRIMARY, indexModels);
            }

            //获取平台编码，只中心节点执行mongo分片
            String platformCode = this.getConfigValue(Constants.PLATFORM_CODE);
            if(Constants.CENTER_CODE.equals(platformCode) && Constants.ZERO.equals(flag)){
                // 检查并创建tenantId哈希索引
                checkAndCreateTenantIdHashIndex(tableName);

                // 检查并配置分片（以tenant_id为哈希分片键）
                checkAndConfigureSharding(tableName);
            }
        } catch (Exception e) {
            log.error("WarningOrderRepositoryImpl ===> checkIndexsExist 创建集合[{}]索引失败！",tableName, e);
            return;
        }
    }

    /**
     * 检查并创建tenantId字段的哈希索引
     * @param tableName 集合名称
     */
    private void checkAndCreateTenantIdHashIndex(String tableName) {
        try {
            // 检查哈希索引是否存在
            boolean hasTenantIdHashIndex = interceptMongoMapper.getIndexes(tableName, MongoDBType.PRIMARY)
                    .stream()
                    .anyMatch(index -> {
                        Map<String, Object> key = (Map<String, Object>) index.get(Constants.KEY);
                        return key != null &&
                                key.containsKey(Constants.RES_TENANT_ID) &&
                                Constants.HASHED.equals(key.get(Constants.RES_TENANT_ID).toString());
                    });

            if (!hasTenantIdHashIndex) {
                MongoCollection<Document> collection = mongoTemplate.getCollection(tableName);
                collection.createIndex(
                        new Document(Constants.RES_TENANT_ID, Constants.HASHED),
                        new IndexOptions().name("hash_idx_tenantId")
                );
                log.info("为集合[{}]创建tenant_id哈希索引成功", tableName);
            } else {
                log.info("集合[{}]的tenant_id哈希索引已存在，无需创建", tableName);
            }
        } catch (Exception e) {
            log.error("集合[{}]创建tenant_id哈希索引失败", tableName, e);
            throw new RuntimeException("创建哈希索引失败", e);
        }
    }

    /**
     * 检查并配置集合分片
     * @param collectionName
     */
    private void checkAndConfigureSharding(String collectionName) {
        try {
            String businessDbName = mongoTemplate.getDb().getName(); // 项目配置的业务数据库名
            String fullCollectionName = businessDbName + "." + collectionName;

            MongoDatabase adminDb = mongoTemplate.getMongoDbFactory().getMongoDatabase("admin");
            // 检查集合是否已分片
            boolean isSharded = isCollectionSharded(collectionName);
            if (!isSharded) {
                // 配置分片
                Document shardCmd = new Document()
                        .append("shardCollection", fullCollectionName)
                        .append("key", new Document(Constants.RES_TENANT_ID, Constants.HASHED));

                adminDb.runCommand(shardCmd);
                log.info("为集合[{}]配置tenant_id哈希分片成功", collectionName);
            } else {
                log.info("集合[{}]已配置分片，无需重复操作", collectionName);
            }
        } catch (Exception e) {
            log.error("为集合[{}]配置分片失败", collectionName, e);
        }
    }

    /**
     * 检查集合是否已分片
     * @param collectionName
     * @return
     */
    private boolean isCollectionSharded(String collectionName) {
        try {
            Document collStatsCmd = new Document("collStats", collectionName);
            Document stats = mongoTemplate.getDb().runCommand(collStatsCmd);
            //sharded:代表集合是否已分片（true:已分片）
            return stats.containsKey("sharded") && Boolean.TRUE.equals(stats.getBoolean("sharded"));
        } catch (Exception e) {
            log.warn("检查集合[{}]分片状态失败: {}", collectionName, e.getMessage());
            return false;
        }
    }

    /**
     * 获取节点编码
     * @param platformCode
     * @return
     */
    private String getConfigValue(String platformCode) {
        String configValue = "";
        AjaxResult ajaxResult = systemFeignClient.getConfValue(platformCode);
        if (ResultCode.SUCCESS.getCode() == (int)ajaxResult.get(AjaxResult.CODE_TAG)) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if(!ObjectUtils.isEmpty(sysConfig)){
                configValue = sysConfig.getConfigValue();
            }
        }
        return configValue;
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
            ListIndexesIterable<Document> indexList = interceptMongoMapper.mongoListIndexs(tableName, primary);
            for (Document document : indexList) {
                Object key = document.get(Constants.KEY);
                if (null != key && key instanceof Document) {
                    Document keyDocument = (Document) key;
                    if (keyDocument.containsKey(Constants.INDEXFIELD_WARNING_ORDER_ID)) {
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
     * @param table
     * @return
     */
    private List<IndexModel> getIndexModels(String table, String date) {
        List<IndexModel> indexModels = new ArrayList<>();
        String[] str = new String[0];
        if (table.equals(TableConstant.WARN_ALARM_OPERATE+"_"+date)) {
            //运营研判表索引
            str = new String[]{Constants.INDEXFIELD_WARNING_ORDER_ID, Constants.INDEXFIELD_TENANT_ID, Constants.INDEXFIELD_PROVINCE_ID,
                    Constants.INDEXFIELD_CITY_ID, Constants.INDEXFIELD_COUNTY_ID, Constants.INDEXFIELD_CREATE_USER, Constants.INDEXFIELD_WARNING_TYPE_ID,
                    Constants.INDEXFIELD_EMERGENCY_LEVEL, Constants.INDEXFIELD_ALARM_TIME, Constants.INDEXFIELD_ALGORITHM_CODE, Constants.INDEXFIELD_DUPLICATE_ORDER_ID,
                    Constants.INDEX_DEL_TIME_RESULT, Constants.INDEX_DEL_ALARMTIME_TYPE, Constants.INDEX_TIME_RESULT_SOURCE, Constants.INDEX_TIME_TENANTID_RESULT, Constants.INDEX_TIME_DEVICE_RESULT,
                    Constants.INDEX_TIME_TYPE_RESULT, Constants.INDEX_TIME_PROVINCEID_CITYID_COUNTYID_RESULT};
        } else if (table.equals(TableConstant.WARN_ALARM_INTERCEPT+"_"+date)) {
            //告警拦截表索引
            str = new String[]{Constants.INDEXFIELD_WARNING_ORDER_ID, Constants.INDEXFIELD_ALARM_TIME, Constants.INDEXFIELD_PROVINCE_CODE, Constants.INDEXFIELD_TENANT_ID,
                    Constants.INDEXFIELD_TASK_ID, Constants.INDEXFIELD_CITY_CODE, Constants.INDEXFIELD_COUNTY_CODE, Constants.INDEXFIELD_PROVINCE_CITY,
                    Constants.INDEXFIELD_WARNING_TYPE_ID, Constants.INDEXFIELD_LONGITUDE_LATITUDE, Constants.INDEXFIELD_DEVICE_CODE, Constants.INDEXFIELD_ALARM_BODY,
                    Constants.INDEXFIELD_ALGORITHM_CODE, Constants.INDEXFIELD_CREATE_TIME, Constants.INDEX_TENANTID_TIME_FLAG_RESULT, Constants.INDEX_TIME_FLAG_RESULT,
                    Constants.INDEX_TIME_RESULT, Constants.INDEX_TIME_RESULT_SOURCE, Constants.INDEX_TIME_TYPE_RESULT, Constants.INDEX_TIME_DEVICE_RESULT,
                    Constants.INDEX_TIME_PROVINCE_CITY_COUNTY_RESULT, Constants.INDEX_CONFIDENCE_LEVEL_MAX, Constants.INDEX_INTERCEPT, Constants.INDEX_ENLARGE_FLAG};
        }
        BasicDBObject indexObj ;
        for (String index : str) {
            IndexOptions aIndexOptions;
            aIndexOptions = new IndexOptions().background(true).name(index);
            indexObj = new BasicDBObject();
            if (index.contains(Constants.COMMA)) {
                String[] str1 = index.split(Constants.COMMA);
                for (String index1 : str1) {
                    indexObj.put(index1, Constants.INDEXASC_ONE);
                }
            } else {
                indexObj.put(index, Constants.INDEXASC_ONE);
            }
            indexModels.add(new IndexModel(indexObj, aIndexOptions));
        }
        return indexModels;
    }

    /**
     * 检查并创建地理位置索引（2dsphere）
     * @param tableName 集合名称
     */
    private void checkAndCreateLocation2dsphereIndex(String tableName) {
        try {
            String indexName = com.chinatower.platform.common.Constants.LOCATION_2DSPHERE_IDX;
            String indexField = com.chinatower.platform.common.Constants.LOCATION;
            
            // 检查地理位置索引是否已存在
            boolean hasLocationIndex = interceptMongoMapper.getIndexes(tableName, MongoDBType.PRIMARY)
                    .stream()
                    .anyMatch(index -> {
                        // 检查索引名称
                        Object nameObj = index.get("name");
                        if (nameObj != null && indexName.equals(nameObj.toString())) {
                            return true;
                        }
                        // 检查索引字段
                        if (index.containsKey(Constants.KEY)) {
                            Object keyObj = index.get(Constants.KEY);
                            if (keyObj instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> key = (Map<String, Object>) keyObj;
                                // 检查是否包含location字段且值为2dsphere
                                if (key.containsKey(indexField)) {
                                    Object locationValue = key.get(indexField);
                                    return locationValue != null && com.chinatower.platform.common.Constants.TWO_DSPHERE.equals(locationValue.toString());
                                }
                            }
                        }
                        return false;
                    });

            if (!hasLocationIndex) {
                MongoCollection<Document> collection = mongoTemplate.getCollection(tableName);
                // 创建2dsphere地理位置索引
                collection.createIndex(
                        new Document(indexField, com.chinatower.platform.common.Constants.TWO_DSPHERE),
                        new IndexOptions().name(indexName).background(true)
                );
                log.info("为集合[{}]创建地理位置索引[{}]成功", tableName, indexName);
            } else {
                log.info("集合[{}]的地理位置索引[{}]已存在，无需创建", tableName, indexName);
            }
        } catch (Exception e) {
            log.error("集合[{}]创建地理位置索引失败", tableName, e);
            // 不抛出异常，避免影响其他表的创建
        }
    }

}
