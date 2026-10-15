package com.chinatower.platform.consumer.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.VmDeviceStatisticsVO;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.consumer.dao.MongoUtilDAO;
import com.chinatower.platform.consumer.dao.SensorAttributeMongoDAO;
import com.chinatower.platform.consumer.dao.SensorStatusDAO;
import com.chinatower.platform.consumer.item.CanalBean;
import com.chinatower.platform.consumer.item.Constants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.model.dto.BaseWarningOrderDTO;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Description 工单表、工单环节流程表数据监听
 * @Author lyy
 * @Date 2022/10/19
 * @Version release_v1.5_10
 **/
@Slf4j
@Component
public class WarningOrderService {

    @Autowired
    private SensorAttributeMongoDAO sensorAttributeMongoDAO;

    @Autowired
    private MongoUtilDAO<Object> mongoUtilDAO;

    @Autowired
    private SensorStatusDAO sensorStatusDAO;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    /** 工单未完结统计专用有界线程池 */
    @Autowired
    @Qualifier("deviceWarningCountExecutor")
    private ThreadPoolTaskExecutor deviceWarningCountExecutor;

    private static final String YYYYMMDD_FORMAT = "yyyyMMdd";
    private static final String TABLE_PREFIX = "vm_device_unfinished_warnings_";

    public void dealOrderMsg(String orderValue) {
        //log.info("工单表数据消费信息:{}", orderValue);
        CanalBean canalBeans = JSON.parseObject(orderValue, CanalBean.class);
        this.dealOrderDataInfo(canalBeans);
        //log.info("工单表数据消费成功！");
    }

    public void dealRecordMsg(String recordValue) {
        //log.info("工单环节流程表数据消费信息:{}", recordValue);
        CanalBean canalBeans = JSON.parseObject(recordValue, CanalBean.class);
        this.dealFlowRecordDataInfo(canalBeans);
        //log.info("工单环节流程表数据消费成功！");
    }

    /**
     * 工单表数据信息
     * 根据 Canal 消息中的数据类型（INSERT、UPDATE、DELETE 等）对 MongoDB 中的工单数据进行增删改操作，
     * 同时根据行业编码分表存储。
     * @param canalBeans
     */
    private void dealOrderDataInfo(CanalBean canalBeans) {
        //解析json放入list集合
        if (StringUtils.isNotEmpty(canalBeans.getData())) {
            // 解析 Canal 消息中的 data 字段，放入 List 集合
            List<Map<String, Object>> warningOrderList = new ArrayList<>();
            for (Object obj: canalBeans.getData()) {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.putAll(JSON.parseObject(JSON.toJSONString(obj)));
                warningOrderList.add(orderMap);
            }
            log.info("工单表数据消费："+JSON.toJSONString(warningOrderList.stream().map(map -> map.get(Constants.WARN_ORDER_ID)).collect(Collectors.toList())));
            //v1.7.20 异步统计设备未完结告警数量，并入mongo库
            CompletableFuture.runAsync(() -> {
                this.deviceWarningCountHandle(warningOrderList, canalBeans.getType());
            }, deviceWarningCountExecutor);

            //根据行业应用编码industry_code分组，过滤掉行业编码为 "000000" 的数据
            Map<Object, List<Map<String, Object>>> orderMap = warningOrderList.stream().filter(item -> !"000000".equals(item.get(Constants.PARAM_INDUSTRY_CODE)))
                    .collect(Collectors.groupingBy(item->item.get(Constants.PARAM_INDUSTRY_CODE)));
            orderMap.entrySet().forEach(entry -> {
                // 拼接表名，例如 "vm_warning_order_t_行业编码"
                String tableName = Constants.WARN_ORDER_TEBLE + entry.getKey();
                //根据报文中的type字段进行判断, INSERT:新增、DELETE:删除、UPDATE:修改...
                if (canalBeans.getType().equals(Constants.INSERT)) {
                    try {
                        // 获取当前分组中所有工单 ID
                        List<Object> orderIdList = entry.getValue().stream().map(item -> item.get(Constants.WARN_ORDER_ID)).collect(Collectors.toList());
                        // 检查 MongoDB 中是否已存在这些工单数据
                        Criteria criteria = Criteria.where(Constants.WARN_ORDER_ID).in(orderIdList);
                        Query query = Query.query(criteria);
                        boolean flagExists = mongoUtilDAO.exists(query, MongoDBType.PRIMARY, tableName);
                        // 如果已存在，先删除旧数据
                        if (flagExists) {
                            mongoUtilDAO.remove(query, MongoDBType.PRIMARY, tableName);
                        }
                        // 插入新的工单数据到对应的表中
                        sensorAttributeMongoDAO.insertAll(entry.getValue(), tableName, MongoDBType.PRIMARY);
                        try {
                            String table = Constants.ORDER;
                            // 检查该表是否已存在索引
                            boolean listIndexs = checkListIndexs(tableName, MongoDBType.PRIMARY);
                            if (!listIndexs) {
                                // 如果索引不存在，则创建索引
                                List<IndexModel> indexModels = getIndexModels(table); // 构造索引
                                mongoUtilDAO.mongoCreateIndexs(tableName, MongoDBType.PRIMARY, indexModels); // 创建索引
                            }
                            //获取平台编码，只中心节点执行mongo分片
                            String platformCode = this.getConfigValue(Constants.PLATFORM_CODE);
                            if(Constants.CENTER_CODE.equals(platformCode)){
                                // 检查并创建tenantId哈希索引
                                checkAndCreateTenantIdHashIndex(tableName, table);

                                // 检查并配置分片（以tenantId为哈希分片键）
                                checkAndConfigureSharding(tableName, table);
                            }
                        } catch (Exception e) {
                            return;
                        }
                    } catch (Exception e) {
                        log.error("同步工单表数据操作异常", e);
                    }
                } else if (canalBeans.getType().equals(Constants.UPDATE) || canalBeans.getType().equals(Constants.DELETE)) {
                    // 如果是 UPDATE 或 DELETE 操作，逐条处理数据
                    for (Map<String, Object> orderDTO : entry.getValue()) {
                        Criteria criteria = Criteria.where(Constants.WARN_ORDER_ID).is(orderDTO.get(Constants.WARN_ORDER_ID));
                        Query query = Query.query(criteria);
                        // 先删除旧数据，后增新数据
                        mongoUtilDAO.remove(query, MongoDBType.PRIMARY, tableName);
                        mongoUtilDAO.mongoInsert(orderDTO, MongoDBType.PRIMARY, tableName);
                    }
                }
            });
        } else {
            // 如果 Canal 消息的 data 字段为空，执行 CREATE 操作逻辑
            if (canalBeans.getType().equals(Constants.CREATE)) {
                List<Map<String, Object>> orderList = sensorStatusDAO.queryOrderList(canalBeans.getTable());
                Map<Object, List<Map<String, Object>>> warnMap = orderList.stream().filter(item -> item.get(Constants.PARAM_INDUSTRY_CODE)!=null)
                        .collect(Collectors.groupingBy(item -> item.get(Constants.PARAM_INDUSTRY_CODE)));
                warnMap.entrySet().forEach(entry -> {
                    // 检查对应表是否存在
                    Boolean exists = mongoUtilDAO.collectionExists(Constants.WARN_ORDER_TEBLE + entry.getKey(), MongoDBType.PRIMARY);
                    if (!exists) {
                        // 如果表不存在，则创建表
                        mongoUtilDAO.mongoCreateTable(Constants.WARN_ORDER_TEBLE + entry.getKey(), MongoDBType.PRIMARY);
                        //log.info("创建工单表：" + Constants.WARN_ORDER_TEBLE + entry.getKey());
                    }
                });
            }
            //log.error("工单表data为:{}", canalBeans.getData());
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
     * 清理七天前的表
     */
    private void cleanupOldTables() {
        int keepDays = 7; //保留的天数
        Date currentDate = DateUtil.parse(DateUtil.format(new Date(), YYYYMMDD_FORMAT), YYYYMMDD_FORMAT);
        // 获取所有集合名
        Set<String> collections = mongoTemplate.getCollectionNames();
        // 筛选出符合的表（表名前缀以及超过保留天数的）
        Set<String> oldTables = collections.stream().filter(collection -> collection.startsWith(TABLE_PREFIX))
                .filter(collection -> isTableOlderThanDays(collection, TABLE_PREFIX, keepDays, currentDate))
                .collect(Collectors.toSet());
        log.info("cleanupOldTables-清理过期表:{}", JSON.toJSONString(oldTables));
        // 删除过期的表
        for (String table : oldTables) {
            try {
                mongoTemplate.getCollection(table).drop();
            } catch (Exception e) {
                log.error("cleanupOldTables-清理表异常", e);
            }

        }
    }

    /**
     * 判断表是否超过指定天数
     *
     * @param tableName  表名
     * @param tablePrefix 表名前缀
     * @param keepDays   保留的天数
     * @param currentDate 当前日期
     * @return 是否过期
     */
    private boolean isTableOlderThanDays(String tableName, String tablePrefix, int keepDays, Date currentDate) {
        String datePart = tableName.replace(tablePrefix, "");
        SimpleDateFormat sdf = new SimpleDateFormat(YYYYMMDD_FORMAT);
        try {
            Date tableDate = sdf.parse(datePart); // 将日期部分转为 Date 对象
            long diff = currentDate.getTime() - tableDate.getTime(); // 当前日期减去表日期
            long diffDays = diff / (1000 * 60 * 60 * 24); // 计算天数差
            return diffDays >= keepDays; // 如果超过保留天数，则判断为过期
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 获取参数配置中配置的生效时间
     * @return
     */
    private String getValidTime() {
        String validTime = null;
        AjaxResult ajaxResult = systemFeignClient.getConfValue("valid_time");
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            validTime = sysConfig.getConfigValue();  // 生效时间格式 "2025-01-01"
        }
        if (StringUtils.isEmpty(validTime)) {
            throw new RuntimeException("无法获取validTime失效日期配置！");
        }
        validTime = validTime.replace(Constants.SEP, "");
        return validTime;
    }

    /**
     * 统计设备未完结告警数量，并插入mongo表
     * @param warningOrderList
     * @param type
     */
    private void deviceWarningCountHandle(List<Map<String, Object>> warningOrderList, String type) {
        try {
            // 1、获取参数配置中配置的生效时间，统计大于该生效时间往后的告警（历史数据不做统计）
            String validTime = getValidTime();
            log.info("*******************工单表数据消费,deviceWarningCountHandle-配置的生效时间:{}", validTime);
            // 存储设备未完结告警数量
            Map<String, Integer> deviceWarningCountMap = new ConcurrentHashMap<>();
            Map<String, VmDeviceStatisticsVO> deviceInfoMap = new ConcurrentHashMap<>();
            // 用于记录已处理过的 warningOrderId，防止重复处理
            Set<String> processedOrderIds = new HashSet<>();
            // 2、遍历告警信息，并累计设备未完结告警数量
            List<BaseWarningOrderDTO> resultList = JSON.parseArray(JSON.toJSONString(warningOrderList), BaseWarningOrderDTO.class);
            //雷达告警将透传字段中的设备code放入deviceCode字段中
            for (BaseWarningOrderDTO dto:resultList) {
                if("10".equals(dto.getWarningSource())){
                    //扩展字段
                    String extraField = dto.getExtraField();
                    //雷达设备编码
                    Object[] radarCodes = null;
                    if(!StringUtils.isEmpty(extraField)){
                        JSONObject jsonExtraField = JSON.parseObject(extraField);
                        if (jsonExtraField != null && jsonExtraField.containsKey(com.chinatower.event.constants.Constants.RADAR_CODES)) {
                            radarCodes=jsonExtraField.getJSONArray(com.chinatower.event.constants.Constants.RADAR_CODES).toArray();
                            for(Object radarCode:radarCodes){
                                if(!ObjectUtil.isEmpty(radarCode)){
                                    dto.setDeviceCode(String.valueOf(radarCode));
                                }
                            }
                        }
                    }
                }
            }
            resultList.stream().filter(item -> !StringUtils.isEmpty(item.getDeviceCode())).forEach(item -> {
                String yearMonthDay = item.getWarningOrderId().substring(Constants.ZERO_NUM, 8); //年月日
                String deviceCode = item.getDeviceCode();
                String industryCode = item.getIndustryCode();
                String tenantId = item.getTenantId();
                String orderStatus = item.getOrderStatus();
                String warningOrderId = item.getWarningOrderId();
                // 构造唯一标识key
                String key = deviceCode + Constants.SEP + tenantId + Constants.SEP + industryCode;

                // 1、初始化历史未完结告警数量
                if (!deviceWarningCountMap.containsKey(key)) {
                    // 查询历史数据
                    Query query = new Query(Criteria.where(Constants.INDEXFIELD_DEVICE_CODE).is(deviceCode)
                            .and(Constants.INDEXFIELD_TENANT_ID).is(tenantId)
                            .and(Constants.PARAM_INDUSTRY_CODE).is(industryCode));
                    VmDeviceStatisticsVO historyData = mongoTemplate.findOne(query, VmDeviceStatisticsVO.class, TABLE_PREFIX + yearMonthDay.substring(0, 6));
                    // 初始化历史未完结数量
                    int historyCount = historyData != null && historyData.getOrderUnCompleteNum() != null
                            ? historyData.getOrderUnCompleteNum()
                            : Constants.ZERO_NUM;
                    // 初始化历史值到 map
                    deviceWarningCountMap.put(key, historyCount);

                    // 添加其他字段信息到 deviceInfoMap
                    VmDeviceStatisticsVO deviceInfo = new VmDeviceStatisticsVO();
                    deviceInfo.setDeviceCode(deviceCode);
                    deviceInfo.setTenantId(tenantId);
                    deviceInfo.setIndustryCode(industryCode);
                    deviceInfo.setDate(yearMonthDay.substring(Constants.ZERO_NUM, 6)); // 年月
                    deviceInfoMap.putIfAbsent(key, deviceInfo);
                    log.info("初始化历史数据: key={}, historyCount={}", key, historyCount);
                }

                // 2、处理当前批次数据
                if (yearMonthDay.compareTo(validTime) >= Constants.ZERO_NUM) {
                    // 获取当前批次的统计值
                    int currentCount = deviceWarningCountMap.getOrDefault(key, Constants.ZERO_NUM);

                    if (Constants.INSERT.equals(type)) {
                        //kafka推送的工单数据存在重复的，这里需要做下兼容，数量+1后重复的不再加
                        // 判断是否已经处理过该工单
                        if (!processedOrderIds.contains(warningOrderId)) {
                            // 未处理过，数量 +1，并记录到 processedOrderIds
                            currentCount += Constants.ONE_NUM;
                            processedOrderIds.add(warningOrderId);
                            log.info("INSERT操作: 未完结数量+1, key={}, currentCount={}", key, currentCount);
                        }else {
                            log.info("INSERT操作: 重复工单，已处理过，跳过, warningOrderId={}", warningOrderId);
                        }
                    } else if (Constants.UPDATE.equals(type) || Constants.DELETE.equals(type)){
                        // 判断是否是未完结告警
                        if (Constants.ZERO.equals(orderStatus) || Constants.SIX.equals(orderStatus)
                                || Constants.SEVEN.equals(orderStatus)) {
                            // 已完结告警，数量减一（确保不小于0）
                            currentCount = Math.max(Constants.ZERO_NUM, currentCount - Constants.ONE_NUM);
                            log.info("UPDATE操作: 已完结工单，未完结数量-1, key={}, currentCount={}", key, currentCount);
                        }
                    }
                    deviceWarningCountMap.put(key, currentCount);
                    log.info("deviceWarningCountHandle统计设备未完结数量-处理数据: deviceCode={}, tenantId={}, industryCode={}, orderStatus={}, key={}, count={}",
                            deviceCode, tenantId, industryCode, orderStatus, key, currentCount);
                }
            });
            // 3、插入并更新mongo
            deviceWarningCountMap.forEach((key, count) -> {
                VmDeviceStatisticsVO info = deviceInfoMap.get(key);
                String deviceCode = info.getDeviceCode();
                String tenantId = info.getTenantId();
                String industryCode = info.getIndustryCode();
                String yyyyMM = info.getDate();
                // 动态生成集合名称（按月分表）
                String collectionName = TABLE_PREFIX + yyyyMM;

                // 构建查询条件去更新表数据
                Query query = new Query(Criteria.where(Constants.INDEXFIELD_DEVICE_CODE).is(deviceCode)
                        .and(Constants.INDEXFIELD_TENANT_ID).is(tenantId).and(Constants.PARAM_INDUSTRY_CODE).is(industryCode));

                Update update = new Update()
                        .set(Constants.INDEXFIELD_TENANT_ID, tenantId)
                        .set(Constants.PARAM_INDUSTRY_CODE, industryCode)
                        .set(Constants.INDEXFIELD_DEVICE_CODE, deviceCode)
                        .set("orderUnCompleteNum", count);
                log.info("*******************工单表数据消费,deviceWarningCountHandle-Update内容:{}", JSON.toJSONString(update));
                // 如果集合不存在，创建集合
                if (!mongoUtilDAO.collectionExists(collectionName, MongoDBType.PRIMARY)) {
                    mongoUtilDAO.mongoCreateTable(collectionName, MongoDBType.PRIMARY);
                }

                mongoUtilDAO.mongoUpdate(query, update, MongoDBType.PRIMARY, collectionName);

                // 4、创建索引
                try {
                    // 检查该表是否已存在索引
                    boolean listIndexs = checkListIndexs(collectionName, MongoDBType.PRIMARY);
                    if (!listIndexs) {
                        // 如果索引不存在，则创建索引
                        List<IndexModel> indexModels = getIndexModels("dev"); // 构造索引
                        mongoUtilDAO.mongoCreateIndexs(collectionName, MongoDBType.PRIMARY, indexModels); // 创建索引
                    }
                } catch (Exception e) {
                    return;
                }
            });
        } catch (Exception e) {
            log.error("异步统计设备未完结告警数量异常", e);
        }
    }

    /**
     * 查询索引列：检查 MongoDB 数据表是否存在特定索引。
     * @param tableName 数据表名称
     * @param primary   MongoDB 数据库类型（主库/从库等）
     * @return  如果存在包含指定字段的索引，返回 true；否则返回 false
     */
    private boolean checkListIndexs(String tableName, MongoDBType primary) {
        boolean success = false;
        try {
            // 调用 mongoUtilDAO 工具类方法，获取指定表的所有索引信息
            ListIndexesIterable<Document> indexList = mongoUtilDAO.mongoListIndexs(tableName, primary);
            for (Document document : indexList) {
                // 获取索引中的 "key" 字段，表示索引的键值
                Object key = document.get("key");
                if (null != key && key instanceof Document) {
                    Document keyDocument = (Document) key;
                    // 检查索引键中是否包含特定字段（例如 "process_id"）
                    if (keyDocument.containsKey(Constants.INDEXFIELD_PROCESS_ID) || keyDocument.containsKey(Constants.INDEXFIELD_DEVICE_CODE)) {
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
     * 根据表名（table）生成对应的索引模型列表。不同的表有不同的索引字段集合，该方法根据字段集合生成 MongoDB 的索引模型（IndexModel）。
     * @param table 表名，用于指定需要生成索引的表，支持 "order" 和 "record"。
     * @return 索引模型列表（List<IndexModel>），包含表的所有需要的索引配置。
     */
    private List<IndexModel> getIndexModels(String table) {
        // 初始化索引字段数组，默认为空
        String[] str = new String[0];
        if (table.equals(Constants.ORDER)) {
            // 如果是 "order" 表，对应的索引字段集合
            str = new String[]{Constants.INDEXFIELD_ALARM_TIME, Constants.INDEXFIELD_PROVINCE_CODE,
                    Constants.INDEXFIELD_TASK_ID, Constants.INDEXFIELD_CITY_CODE, Constants.INDEXFIELD_COUNTY_CODE,
                    Constants.INDEXFIELD_WARNING_TYPE_ID, Constants.INDEXFIELD_PROVINCE_CITY, Constants.INDEXFIELD_LONGITUDE_LATITUDE,
                    Constants.INDEXFIELD_DEVICE_CODE, Constants.INDEXFIELD_PROCESS_ID, Constants.INDEXFIELD_GROUP_ID,
                    Constants.INDEXFIELD_ALARM_BODY, Constants.INDEXFIELD_ALGORITHM_CODE, Constants.INDEXFIELD_TENANT_ID,
                    Constants.INDEXFIELD_APP_VER_CODE, Constants.PARAM_INDUSTRY_CODE, Constants.INDEXFIELD_JUDGE_TIME,
                    Constants.INDEXFIELD_DUPLICATE_ORDER_ID, Constants.INDEXFIELD_WARNING_ORDER_ID, Constants.INDEXFIELD_CHANNEL_CODE, Constants.INDEXFIELD_TOWN_CODE};
        } else if (table.equals(Constants.RECORD)) {
            // 如果是 "record" 表，对应的索引字段集合
            str = new String[]{Constants.INDEXFIELD_ORDER_ID, Constants.INDEXFIELD_FLOW_NODE_TIME, Constants.INDEXFIELD_FLOW_END_TIME,
                    Constants.INDEXFIELD_ORDER_FLOW_ID, Constants.INDEXFIELD_PROCESS_ID, Constants.INDEXFIELD_GROUP_ID};
        } else if (table.equals("dev")) {
            str = new String[]{Constants.INDEXFIELD_DEVICE_CODE, Constants.INDEXFIELD_TENANT_ID, Constants.PARAM_INDUSTRY_CODE,
            Constants.INDEX_DEVICE_TENANTID_CODE};
        }
        List<IndexModel> indexModels = new ArrayList<>();
        BasicDBObject indexObj ;
        for (String index : str) {
            IndexOptions aIndexOptions ;
            // 如果字段名包含逗号（,），表示这是一个复合索引
            if (index.contains(Constants.COMMA)) {
                indexObj = new BasicDBObject();
                // 分割字段名（复合索引由多个字段组成，用逗号分隔）
                String[] indexArr = index.split(Constants.COMMA);
                // 根据字段数量动态生成索引
                for (String field : indexArr) {
                    indexObj.put(field, Constants.INDEXASC_ONE); // 添加字段升序索引
                }
                // 设置索引名称（字段名用下划线拼接）
                aIndexOptions = new IndexOptions()
                        .background(true)
                        .name(Constants.INDEXNAME_PREFIX + String.join("_", indexArr));
                // 添加索引模型到列表
                indexModels.add(new IndexModel(indexObj, aIndexOptions));
                /*// 为两个字段设置升序索引（1 表示升序）
                indexObj.put(indexArr[0], Constants.INDEXASC_ONE);
                indexObj.put(indexArr[1], Constants.INDEXASC_ONE);
                // 设置索引选项，包含索引名称（以两字段名拼接）
                aIndexOptions = new IndexOptions().background(true).name(Constants.INDEXNAME_PREFIX + indexArr[0] + "_" + indexArr[1]);
                // 添加复合索引模型到列表
                indexModels.add(new IndexModel(indexObj, aIndexOptions));*/
            } else {
                // 如果字段名不包含逗号，则是单字段索引
                indexObj = new BasicDBObject();
                // 为字段设置升序索引
                indexObj.put(index, Constants.INDEXASC_ONE);
                // 设置索引选项，包含索引名称（以字段名命名）
                aIndexOptions = new IndexOptions().background(true).name(Constants.INDEXNAME_PREFIX + index);
                // 添加单字段索引模型到列表
                indexModels.add(new IndexModel(indexObj, aIndexOptions));
            }
        }
        return indexModels;
    }

    /**
     * 工单环节流程数据信息
     * 根据 Canal 消息中的数据类型（INSERT、UPDATE、DELETE 等）对 MongoDB 中的工单环节流程数据进行增删改操作，
     * 并根据工单表中的行业编码分表存储。
     * @param canalBeans Canal 消息对象，包含表名、操作类型和数据等信息
     */
    private void dealFlowRecordDataInfo(CanalBean canalBeans) {
        Map mapIn = new HashMap();
        //获取工单环节表，拼接工单表表名
        String flowRecordTableName = canalBeans.getTable();
        String warnOrderTableName = Constants.WARN_ORDER_TEBLE + flowRecordTableName.substring(23, 29);
        mapIn.put(Constants.PARAM_FLOWRECORD_TABLE_NAME, flowRecordTableName); //工单环节流程表
        mapIn.put(Constants.PARAM_WARNORDER_TABLE_NAME, warnOrderTableName); //工单表
        //解析json放入list集合
        if (StringUtils.isNotEmpty(canalBeans.getData())) {
            // 解析 Canal 消息中的 data 字段，放入 List 集合
            List<Map<String, Object>> flowRecordList = new ArrayList<>();
            for (Object obj: canalBeans.getData()) {
                Map<String, Object> recordMap = new HashMap<>();
                recordMap.putAll(JSON.parseObject(JSON.toJSONString(obj)));
                flowRecordList.add(recordMap);
            }
            log.info("工单环节表数据消费："+JSON.toJSONString(flowRecordList.stream().map(map -> map.get(Constants.ORDER_FLOW_ID)).collect(Collectors.toList())));
            // 获取工单环节流程 ID 集合
            List<Object> orderFlowIds = flowRecordList.stream().map(item->item.get(Constants.ORDER_FLOW_ID)).collect(Collectors.toList());
            mapIn.put("orderFlowIds", orderFlowIds);
            // 根据工单环节流程 ID 关联工单表，匹配对应的行业编码
            List<Map<String, Object>> flowRecordReportList = sensorStatusDAO.queryOrderFlowRecordList(mapIn);
            // 检查是否成功查询到关联的工单数据
            if(ObjectUtil.isNotNull(flowRecordReportList) && !CollectionUtils.isEmpty(flowRecordReportList)){
                // 将行业编码合并到原始工单环节流程数据中
                for (Map<String, Object> order : flowRecordReportList) {
                    for(Map<String, Object> reportDTO : flowRecordList) {
                        if(order.get(Constants.ORDER_FLOW_ID).equals(reportDTO.get(Constants.ORDER_FLOW_ID))){
                            reportDTO.put(Constants.PARAM_INDUSTRY_CODE, order.get(Constants.PARAM_INDUSTRY_CODE));
                        }
                    }
                }
                // 根据行业编码（industry_code）对工单环节流程数据进行分组，过滤掉行业编码为 "000000" 的数据
                Map<Object, List<Map<String, Object>>> recordMap = flowRecordList.stream().filter(item -> !"000000".equals(item.get(Constants.PARAM_INDUSTRY_CODE)))
                        .collect(Collectors.groupingBy(item -> item.get(Constants.PARAM_INDUSTRY_CODE)));
                recordMap.entrySet().forEach(entry -> {
                    List<Map<String, Object>> flowList = new ArrayList<>();
                    // 拼接表名，如：vm_order_flow_record_t_000000
                    String tableName = Constants.WARN_ORDER_PROCESS_TEBLE + entry.getKey();
                    for (Map<String, Object> report : entry.getValue()) {
                        if (report.get(Constants.PARAM_INDUSTRY_CODE).equals(entry.getKey())) {
                            report.remove(Constants.PARAM_INDUSTRY_CODE);
                        }
                        flowList.add(report);
                        //根据报文中的type字段进行判断, INSERT:新增、DELETE:删除、UPDATE:修改...
                        if (canalBeans.getType().equals(Constants.UPDATE) || canalBeans.getType().equals(Constants.DELETE)) {
                            // 如果是 UPDATE 或 DELETE 操作，先删除旧数据再插入新数据
                            Criteria criteria = Criteria.where(Constants.ORDER_FLOW_ID).is(report.get(Constants.ORDER_FLOW_ID));
                            Query query = Query.query(criteria);
                            mongoUtilDAO.remove(query, MongoDBType.PRIMARY, tableName);
                            mongoUtilDAO.mongoInsert(report, MongoDBType.PRIMARY, tableName);
                        }
                    }
                    if (canalBeans.getType().equals(Constants.INSERT)) {
                        try {
                            // 获取当前分组中所有工单环节流程 ID
                            List<Object> flowIdList = entry.getValue().stream().map(item -> item.get(Constants.ORDER_FLOW_ID)).collect(Collectors.toList());
                            Criteria criteria = Criteria.where(Constants.ORDER_FLOW_ID).in(flowIdList);
                            Query query = Query.query(criteria);
                            // 检查 MongoDB 中是否已存在这些工单环节流程数据
                            boolean flagExists = mongoUtilDAO.exists(query, MongoDBType.PRIMARY, tableName);
                            if (flagExists) {
                                //如果存在，删除旧数据
                                mongoUtilDAO.remove(query, MongoDBType.PRIMARY, tableName);
                            }
                            //插入告警流程流转数据
                            sensorAttributeMongoDAO.insertAll(flowList, tableName, MongoDBType.PRIMARY);
                            try {
                                String table = Constants.RECORD;
                                //先检查索引是否已存在
                                boolean listIndexs = checkListIndexs(tableName, MongoDBType.PRIMARY);
                                if (!listIndexs) {
                                    //需要创建索引的集合
                                    List<IndexModel> indexModels = getIndexModels(table);
                                    mongoUtilDAO.mongoCreateIndexs(tableName, MongoDBType.PRIMARY, indexModels);
                                }
                                //获取平台编码，只中心节点执行mongo分片
                                String platformCode = this.getConfigValue(Constants.PLATFORM_CODE);
                                if(Constants.CENTER_CODE.equals(platformCode)) {
                                    // 检查并创建orderFlowId哈希索引
                                    checkAndCreateTenantIdHashIndex(tableName, table);

                                    // 检查并配置分片（以orderFlowId为哈希分片键）
                                    checkAndConfigureSharding(tableName, table);
                                }
                            } catch (Exception e) {
                                return;
                            }
                        } catch (Exception e) {
                            log.error("同步工单环节流程表数据操作异常", e);
                        }
                    }
                });
            }else{
                log.info("工单环节表数据消费异常，未关联查询到工单表信息："+JSON.toJSONString(flowRecordList.stream().map(map -> map.get(Constants.ORDER_FLOW_ID)).collect(Collectors.toList())));
            }
        } else {
            if (canalBeans.getType().equals(Constants.CREATE)) {
                mapIn.put("orderFlowIds", null);
                // 根据工单环节流程 ID 关联工单表，匹配对应的行业编码
                List<Map<String, Object>> flowRecordReportList = sensorStatusDAO.queryOrderFlowRecordList(mapIn);
                Map<Object, List<Map<String, Object>>> warnMap = flowRecordReportList.stream().filter(item->item.get(Constants.PARAM_INDUSTRY_CODE) != null)
                        .collect(Collectors.groupingBy(item->item.get(Constants.PARAM_INDUSTRY_CODE)));
                // 遍历分组结果，检查并创建表
                warnMap.entrySet().forEach(entry -> {
                    Boolean exists = mongoUtilDAO.collectionExists(Constants.WARN_ORDER_PROCESS_TEBLE + entry.getKey(), MongoDBType.PRIMARY);
                    if (!exists) {
                        mongoUtilDAO.mongoCreateTable(Constants.WARN_ORDER_PROCESS_TEBLE + entry.getKey(), MongoDBType.PRIMARY);
                        //log.info("创建工单环节流程表：" + Constants.WARN_ORDER_PROCESS_TEBLE + entry.getKey());
                    }
                });
            }
            //log.error("工单环节流程表data为:{}", canalBeans.getData());
        }
    }

    /**
     * 检查并创建哈希索引
     * @param tableName 集合名称
     * @param table 区分表的标识
     */
    private void checkAndCreateTenantIdHashIndex(String tableName, String table) {
        try {
            String hashIndex = table.equals(Constants.ORDER) ? Constants.INDEXFIELD_TENANT_ID : Constants.INDEXFIELD_ORDER_FLOW_ID;
            String indexName = table.equals(Constants.ORDER) ? "hash_idx_tenantId" : "hash_idx_orderFlowId";
            // 检查哈希索引是否存在
            boolean hasTenantIdHashIndex = mongoUtilDAO.getIndexes(tableName, MongoDBType.PRIMARY)
                    .stream()
                    .anyMatch(index -> {
                        Map<String, Object> key = (Map<String, Object>) index.get(Constants.KEY);
                        return key != null &&
                                key.containsKey(hashIndex) &&
                                Constants.HASHED.equals(key.get(hashIndex).toString());
                    });

            if (!hasTenantIdHashIndex) {
                // 兼容不同Spring Data MongoDB版本的哈希索引创建方式
                MongoCollection<Document> collection = mongoTemplate.getCollection(tableName);
                collection.createIndex(
                        new Document(hashIndex, Constants.HASHED),
                        new IndexOptions().name(indexName)
                );
                log.info("为集合[{}]创建哈希索引成功", tableName);
            } else {
                log.debug("集合[{}]的哈希索引已存在，无需创建", tableName);
            }
        } catch (Exception e) {
            log.error("集合[{}]创建哈希索引失败", tableName, e);
            throw new RuntimeException("创建哈希索引失败", e);
        }
    }

    /**
     * 检查并配置集合分片
     * @param collectionName 集合名称
     * @param table 区分表的标识
     */
    private void checkAndConfigureSharding(String collectionName, String table) {
        try {
            String hashIndex = table.equals(Constants.ORDER) ? Constants.INDEXFIELD_TENANT_ID : Constants.INDEXFIELD_ORDER_FLOW_ID;
            MongoDatabase database = mongoTemplate.getDb();
            String fullCollectionName = database.getName() + "." + collectionName;

            MongoDatabase adminDb = mongoTemplate.getMongoDbFactory().getMongoDatabase("admin");

            // 检查集合是否已分片
            boolean isSharded = isCollectionSharded(collectionName);
            if (!isSharded) {
                // 配置分片
                Document shardCmd = new Document()
                        .append("shardCollection", fullCollectionName)
                        .append(Constants.KEY, new Document(hashIndex, Constants.HASHED));

                adminDb.runCommand(shardCmd);
                log.info("为集合[{}]配置哈希分片成功", collectionName);
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
}
