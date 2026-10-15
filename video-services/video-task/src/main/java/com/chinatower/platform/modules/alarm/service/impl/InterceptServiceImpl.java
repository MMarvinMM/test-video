package com.chinatower.platform.modules.alarm.service.impl;

import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.util.DateUtils;
import com.chinatower.platform.modules.alarm.service.InterceptService;
import com.chinatower.platform.modules.statistics.dao.StatisticsDAO;
import com.chinatower.platform.modules.statistics.dao.TowerStatisticsDAO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class InterceptServiceImpl implements InterceptService {

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Resource
    private StatisticsDAO statisticsDAO;

    @Resource
    private TowerStatisticsDAO towerStatisticsDAO;

    /**
     * 按小时统计告警压制数量
     * @param dateVO 包含日期信息的对象，用于查询事件数据
     * @param suppressDictData 包含是否压制的字典数据，用于判断数据来源
     * @return 返回事件数量列表，每个事件以 Map 的形式存储
     */
    @Override
    public List<Map> getEventNumListByInterceptHour(DateVO dateVO, Result<List<SysDictDataResponse>> suppressDictData) {
        List<Map> resultList = new ArrayList<>();
        String flag = Constants.ONE;
        if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
            flag= suppressDictData.getData().get(0).getDictValue();
        }
        if(!Constants.ONE.equals(flag)){
            // 根据日期构建 MongoDB 表名
            Date date = DateUtils.stringToDateYMDHMS(dateVO.getHandleDate());
            String yyyyMM = DateUtils.date2Str(date,Constants.YY_MM);
            String tableName = Constants.WARN_WARNING_INTERCEPT+"_"+yyyyMM;
            MongoDatabase database = mongoTemplate.getDb();
            MongoCollection<Document> collection = database.getCollection(tableName);
            // 构建聚合管道
            List<Document> pipeline = Arrays.asList(
                    // Match 阶段：筛选符合条件的数据
                    new Document(Constants.$_MATCH,
                            new Document()
                                    .append(Constants.PARAM_DELETE_FLAG_FIELD, "0")
                                    .append(Constants.RES_JUDGE_RESULT, null)
                                    .append(Constants.PARAM_ALARM_TIME_FIELD, new Document(Constants.$_REGEX, "^" + dateVO.getHandleDate()))
                    ),
                    // Group 阶段：按字段分组并统计事件数量
                    new Document(Constants.$_GROUP,
                            new Document("_id",
                                    new Document(Constants.RES_TENANT_ID, Constants.$_TENANT_ID)
                                            .append(Constants.PARAM_PRO_CODE, Constants.$_PROVINCE_CODE)
                                            .append(Constants.PARAM_CITY_CODE, Constants.$_CITY_CODE)
                                            .append(Constants.PARAM_COUNTY_CODE, Constants.$_COUNTY_CODE)
                                            .append(Constants.PARAM_TOWN_CODE_FILD, Constants.$_TOWN_CODE)
                                            .append(Constants.RES_DEVICE_CODE, Constants.$_DEVICE_CODE)
                                            .append(Constants.PARAM_APP_VER_CODE_FIELD, "$app_ver_code")
                                            .append(Constants.PARAM_INDUSTRY_CODE_FIELD, Constants.$_INDUSTRY_CODE)
                                            .append(Constants.RES_WARNING_SOURCE, Constants.$_WARNING_SOURCE)
                                            .append(Constants.HOUR_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 11, 2)))
                                            .append("year_str", new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 0, 4)))
                                            .append(Constants.MONTH_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 5, 2)))
                                            .append(Constants.DAY_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 8, 2)))
                            )
                                    .append(Constants.EVENT_NUM, new Document("$sum", 1))
                    ),
                    // Project 阶段：格式化输出结果字段
                    new Document(Constants.$_PROJECT,
                            new Document("_id", 0)
                                    .append(Constants.RES_TENANT_ID, Constants.$_ID_TENANT_ID)
                                    .append(Constants.PARAM_PRO_CODE, Constants.$_ID_PROVINCE_CODE)
                                    .append(Constants.PARAM_CITY_CODE, "$_id.city_code")
                                    .append(Constants.PARAM_COUNTY_CODE, Constants.$_ID_COUNTY_CODE)
                                    .append(Constants.PARAM_TOWN_CODE_FILD, "$_id.town_code")
                                    .append(Constants.RES_DEVICE_CODE, Constants.$_ID_DEVICE_CODE)
                                    .append(Constants.PARAM_APP_VER_CODE_FIELD, "$_id.app_ver_code")
                                    .append(Constants.PARAM_INDUSTRY_CODE_FIELD, Constants.$_ID_INDUSTRY_CODE)
                                    .append(Constants.RES_WARNING_SOURCE, Constants.$_ID_WARNING_SOURCE)
                                    .append(Constants.HOUR_STR, Constants.$_ID_HOUR_STR)
                                    .append(Constants.YEAR_STR, "$_id.year_str")
                                    .append(Constants.MONTH_STR, Constants.$_ID_MONTH_STR)
                                    .append(Constants.DAY_STR, Constants.$_ID_DAY_STR)
                                    .append(Constants.EVENT_NUM, Constants.$_EVENT_NUM)
                    )
            );

            // 执行聚合查询
            List<Document> results = collection.aggregate(pipeline).into(new ArrayList<>());
            // 解析查询结果，将 Document 转换为 Map 并加入结果列表
            for (Document doc:results) {
                Map map = new HashMap();
                map.put(Constants.RES_TENANT_ID,doc.get(Constants.RES_TENANT_ID));
                map.put(Constants.PARAM_PRO_CODE,doc.get(Constants.PARAM_PRO_CODE));
                map.put(Constants.PARAM_CITY_CODE,doc.get(Constants.PARAM_CITY_CODE));
                map.put(Constants.PARAM_COUNTY_CODE,doc.get(Constants.PARAM_COUNTY_CODE));
                map.put(Constants.PARAM_TOWN_CODE_FILD,doc.get(Constants.PARAM_TOWN_CODE_FILD));
                map.put(Constants.PARAM_ALARM_TIME_FIELD,doc.get(Constants.PARAM_ALARM_TIME_FIELD));
                map.put(Constants.RES_DEVICE_CODE,doc.get(Constants.RES_DEVICE_CODE));
                map.put(Constants.HOUR_STR,doc.get(Constants.HOUR_STR));
                map.put(Constants.YEAR_STR,doc.get(Constants.YEAR_STR));
                map.put(Constants.MONTH_STR,doc.get(Constants.MONTH_STR));
                map.put(Constants.DAY_STR,doc.get(Constants.DAY_STR));
                map.put(Constants.HANDLE_DATE_TIME,doc.get(Constants.HANDLE_DATE_TIME));
                map.put(Constants.PARAM_APP_VER_CODE_FIELD,doc.get(Constants.PARAM_APP_VER_CODE_FIELD));
                map.put(Constants.RES_WARNING_SOURCE,doc.get(Constants.RES_WARNING_SOURCE));
                map.put(Constants.PARAM_INDUSTRY_CODE_FIELD,doc.get(Constants.PARAM_INDUSTRY_CODE_FIELD));
                resultList.add(map);
            }
        }else{
            // 从统计数据库查询数据
            resultList= statisticsDAO.getEventNumListByInterceptHour(dateVO);
        }
        return resultList;
    }

    @Override
    public List<Map> getEventNumListByInterceptHourNew(DateVO dateVO,Result<List<SysDictDataResponse>> suppressDictData) {
        List<Map> resultList = new ArrayList<>();
        String flag = Constants.ONE;
        if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
            flag= suppressDictData.getData().get(0).getDictValue();
        }
        if(!Constants.ONE.equals(flag)){
            Date date = DateUtils.stringToDateYMDHMS(dateVO.getHandleDate());
            String yyyyMM = DateUtils.date2Str(date,Constants.YY_MM);
            String tableName = Constants.WARN_WARNING_INTERCEPT+"_"+yyyyMM;
            MongoDatabase database = mongoTemplate.getDb();
            MongoCollection<Document> collection = database.getCollection(tableName);
            // 构建聚合管道
            List<Document> pipeline = Arrays.asList(
                    // Match 阶段：筛选符合条件的数据
                    new Document(Constants.$_MATCH,
                            new Document()
                                    .append(Constants.PARAM_DELETE_FLAG_FIELD, "0")
                                    .append(Constants.RES_JUDGE_RESULT, null)
                                    .append(Constants.PARAM_ALARM_TIME_FIELD, new Document(Constants.$_REGEX, "^" + dateVO.getHandleDate()))
                    ),
                    // Group 阶段：按字段分组并统计事件数量
                    new Document(Constants.$_GROUP,
                            new Document("_id",
                                    new Document(Constants.RES_TENANT_ID, Constants.$_TENANT_ID)
                                            .append(Constants.PARAM_PRO_CODE, Constants.$_PROVINCE_CODE)
                                            .append(Constants.PARAM_WARNING_TYPE_ID_FIELD, Constants.$_WARNING_TYPE_ID)
                                            .append(Constants.PARAM_CITY_CODE, Constants.$_CITY_CODE)
                                            .append(Constants.PARAM_COUNTY_CODE, Constants.$_COUNTY_CODE)
                                            .append(Constants.PARAM_TOWN_CODE_FILD, Constants.$_TOWN_CODE)
                                            .append(Constants.RES_DEVICE_CODE, Constants.$_DEVICE_CODE)
                                            .append(Constants.PARAM_APP_VER_CODE_FIELD, "$app_ver_code")
                                            .append(Constants.PARAM_INDUSTRY_CODE_FIELD, Constants.$_INDUSTRY_CODE)
                                            .append(Constants.RES_WARNING_SOURCE, Constants.$_WARNING_SOURCE)
                                            .append(Constants.HOUR_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 11, 2)))
                                            .append(Constants.YEAR_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 0, 4)))
                                            .append(Constants.MONTH_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 5, 2)))
                                            .append(Constants.DAY_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 8, 2)))
                            )
                                    .append(Constants.EVENT_NUM, new Document("$sum", 1))
                    ),
                    // Project 阶段：格式化输出结果字段
                    new Document(Constants.$_PROJECT,
                            new Document("_id", 0)
                                    .append(Constants.RES_TENANT_ID, Constants.$_ID_TENANT_ID)
                                    .append(Constants.PARAM_PRO_CODE, Constants.$_ID_PROVINCE_CODE)
                                    .append(Constants.PARAM_WARNING_TYPE_ID_FIELD, "$_id.warning_type_id")
                                    .append(Constants.PARAM_CITY_CODE, Constants.$_ID_CITY_CODE)
                                    .append(Constants.PARAM_COUNTY_CODE, Constants.$_ID_COUNTY_CODE)
                                    .append(Constants.PARAM_TOWN_CODE_FILD, Constants.$_ID_TOWN_CODE)
                                    .append(Constants.RES_DEVICE_CODE, Constants.$_ID_DEVICE_CODE)
                                    .append(Constants.PARAM_APP_VER_CODE_FIELD, "$_id.app_ver_code")
                                    .append(Constants.PARAM_INDUSTRY_CODE_FIELD, Constants.$_ID_INDUSTRY_CODE)
                                    .append(Constants.RES_WARNING_SOURCE, Constants.$_ID_WARNING_SOURCE)
                                    .append(Constants.HOUR_STR, Constants.$_ID_HOUR_STR)
                                    .append(Constants.YEAR_STR, Constants.$_ID_YEAR_STR)
                                    .append(Constants.MONTH_STR, Constants.$_ID_MONTH_STR)
                                    .append(Constants.DAY_STR, Constants.$_ID_DAY_STR)
                                    .append(Constants.EVENT_NUM, Constants.$_EVENT_NUM)
                    )
            );

            // 执行聚合查询
            List<Document> results = collection.aggregate(pipeline).into(new ArrayList<>());
            for (Document doc:results) {
                Map map = new HashMap();
                map.put(Constants.RES_TENANT_ID,doc.get(Constants.RES_TENANT_ID));
                map.put(Constants.PARAM_PRO_CODE,doc.get(Constants.PARAM_PRO_CODE));
                map.put(Constants.PARAM_CITY_CODE,doc.get(Constants.PARAM_CITY_CODE));
                map.put(Constants.PARAM_COUNTY_CODE,doc.get(Constants.PARAM_COUNTY_CODE));
                map.put(Constants.PARAM_TOWN_CODE_FILD,doc.get(Constants.PARAM_TOWN_CODE_FILD));
                map.put(Constants.PARAM_ALARM_TIME_FIELD,doc.get(Constants.PARAM_ALARM_TIME_FIELD));
                map.put(Constants.RES_DEVICE_CODE,doc.get(Constants.RES_DEVICE_CODE));
                map.put(Constants.HOUR_STR,doc.get(Constants.HOUR_STR));
                map.put(Constants.YEAR_STR,doc.get(Constants.YEAR_STR));
                map.put(Constants.MONTH_STR,doc.get(Constants.MONTH_STR));
                map.put(Constants.DAY_STR,doc.get(Constants.DAY_STR));
                map.put(Constants.HANDLE_DATE_TIME,doc.get(Constants.HANDLE_DATE_TIME));
                map.put(Constants.PARAM_APP_VER_CODE_FIELD,doc.get(Constants.PARAM_APP_VER_CODE_FIELD));
                map.put(Constants.RES_WARNING_SOURCE,doc.get(Constants.RES_WARNING_SOURCE));
                map.put(Constants.PARAM_INDUSTRY_CODE_FIELD,doc.get(Constants.PARAM_INDUSTRY_CODE_FIELD));
                map.put(Constants.PARAM_WARNING_TYPE_ID_FIELD,doc.get(Constants.PARAM_WARNING_TYPE_ID_FIELD));
                resultList.add(map);
            }
        }else{
            resultList= statisticsDAO.getEventNumListByInterceptHourNew(dateVO);
        }
        return resultList;
    }

    @Override
    public List<Map> getInterceptNum(DateVO dateVO,Result<List<SysDictDataResponse>> suppressDictData) {
        List<Map> resultList = new ArrayList<>();
        String flag = Constants.ONE;
        if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
            flag= suppressDictData.getData().get(0).getDictValue();
        }
        if(!Constants.ONE.equals(flag)) {
            Date date = DateUtils.stringToDateYMDHMS(dateVO.getHandleDate());
            String yyyyMM = DateUtils.date2Str(date, Constants.YY_MM);
            String tableName = Constants.WARN_WARNING_INTERCEPT + "_" + yyyyMM;
            MongoDatabase database = mongoTemplate.getDb();
            MongoCollection<Document> collection = database.getCollection(tableName);

            // 构建聚合管道
            List<Document> pipeline = Arrays.asList(
                    // Match 阶段：筛选符合条件的数据
                    new Document(Constants.$_MATCH,
                            new Document()
                                    .append(Constants.PARAM_DELETE_FLAG_FIELD, "0")
                                    .append(Constants.RES_JUDGE_RESULT, null)
                                    .append(Constants.PARAM_ALARM_TIME_FIELD, new Document(Constants.$_REGEX, "^" + dateVO.getHandleDate()))
                    ),
                    // Group 阶段：按字段分组并统计事件数量
                    new Document(Constants.$_GROUP,
                            new Document("_id",
                                    new Document(Constants.PARAM_PROVINCE_ID, Constants.$_PROVINCE_CODE)
                                            .append(Constants.PARAM_CITY_ID, Constants.$_CITY_CODE)
                                            .append(Constants.PARAM_COUNTY_ID, Constants.$_COUNTY_CODE)
                                            .append(Constants.PARAM_PROVINCE_NAME_FIELD, "$province_name")
                                            .append(Constants.PARAM_CITY_NAME_FIELD, "$city_name")
                                            .append(Constants.PARAM_COUNTY_NAME_FIELD, "$county_name")
                                            .append(Constants.PARAM_TOWN_CODE_FILD, Constants.$_TOWN_CODE)
                                            .append(Constants.PARAM_TOWN_NAME_FIELD, "$town_name")
                                            .append(Constants.RES_TENANT_ID, Constants.$_TENANT_ID)
                                            .append(Constants.RES_DEVICE_CODE, Constants.$_DEVICE_CODE)
                                            .append(Constants.RES_WARNING_SOURCE, Constants.$_WARNING_SOURCE)
                                            .append(Constants.PARAM_INDUSTRY_CODE_FIELD, Constants.$_INDUSTRY_CODE)
                                            .append(Constants.YEAR_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 0, 4)))
                                            .append(Constants.MONTH_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 5, 2)))
                                            .append(Constants.DAY_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 8, 2)))
                            )
                                    .append("today_num", new Document("$sum", 1))
                    ),
                    // Project 阶段：格式化输出结果字段
                    new Document(Constants.$_PROJECT,
                            new Document("_id", 0)
                                    .append(Constants.PARAM_PROVINCE_ID, "$_id.province_id")
                                    .append(Constants.PARAM_CITY_ID, "$_id.city_id")
                                    .append(Constants.PARAM_COUNTY_ID, "$_id.county_id")
                                    .append(Constants.PARAM_PROVINCE_NAME_FIELD, "$_id.province_name")
                                    .append(Constants.PARAM_CITY_NAME_FIELD, "$_id.city_name")
                                    .append(Constants.PARAM_COUNTY_NAME_FIELD, "$_id.county_name")
                                    .append(Constants.PARAM_TOWN_CODE_FILD, Constants.$_ID_TOWN_CODE)
                                    .append(Constants.PARAM_TOWN_NAME_FIELD, "$_id.town_name")
                                    .append(Constants.PARAM_ALARM_TIME_FIELD, "$_id.alarm_time")
                                    .append(Constants.RES_MODIFIER_DATE, "$_id.modifier_date")
                                    .append(Constants.RES_TENANT_ID, Constants.$_ID_TENANT_ID)
                                    .append(Constants.RES_DEVICE_CODE, Constants.$_ID_DEVICE_CODE)
                                    .append(Constants.RES_WARNING_SOURCE, Constants.$_ID_WARNING_SOURCE)
                                    .append(Constants.PARAM_INDUSTRY_CODE_FIELD, Constants.$_ID_INDUSTRY_CODE)
                                    .append("today_num", "$today_num")
                                    .append(Constants.YEAR_STR, Constants.$_ID_YEAR_STR)
                                    .append(Constants.MONTH_STR, Constants.$_ID_MONTH_STR)
                                    .append(Constants.DAY_STR, Constants.$_ID_DAY_STR)
                    )
            );

            // 执行聚合查询
            List<Document> results = collection.aggregate(pipeline).into(new ArrayList<>());
            for (Document doc : results) {
                Map map = new HashMap();
                map.put(Constants.PARAM_PROVINCE_ID,doc.get(Constants.PARAM_PROVINCE_ID));
                map.put(Constants.PARAM_CITY_ID,doc.get(Constants.PARAM_CITY_ID));
                map.put(Constants.PARAM_COUNTY_ID,doc.get(Constants.PARAM_COUNTY_ID));
                map.put(Constants.PARAM_PROVINCE_NAME_FIELD,doc.get(Constants.PARAM_PROVINCE_NAME_FIELD));
                map.put(Constants.PARAM_CITY_NAME_FIELD,doc.get(Constants.PARAM_CITY_NAME_FIELD));
                map.put(Constants.PARAM_COUNTY_NAME_FIELD,doc.get(Constants.PARAM_COUNTY_NAME_FIELD));
                map.put(Constants.PARAM_TOWN_CODE_FILD,doc.get(Constants.PARAM_TOWN_CODE_FILD));
                map.put(Constants.PARAM_TOWN_NAME_FIELD,doc.get(Constants.PARAM_TOWN_NAME_FIELD));
                map.put(Constants.PARAM_ALARM_TIME_FIELD,doc.get(Constants.PARAM_ALARM_TIME_FIELD));
                map.put(Constants.RES_MODIFIER_DATE,doc.get(Constants.RES_MODIFIER_DATE));
                map.put(Constants.RES_TENANT_ID,doc.get(Constants.RES_TENANT_ID));
                map.put(Constants.RES_DEVICE_CODE,doc.get(Constants.RES_DEVICE_CODE));
                map.put(Constants.RES_WARNING_SOURCE,doc.get(Constants.RES_WARNING_SOURCE));
                map.put(Constants.PARAM_INDUSTRY_CODE_FIELD,doc.get(Constants.PARAM_INDUSTRY_CODE_FIELD));
                map.put(Constants.PARAM_TODAY_NUM,doc.get(Constants.PARAM_TODAY_NUM));
                map.put(Constants.YEAR_STR,doc.get(Constants.YEAR_STR));
                map.put(Constants.MONTH_STR,doc.get(Constants.MONTH_STR));
                map.put(Constants.DAY_STR,doc.get(Constants.DAY_STR));
                resultList.add(map);
            }
        }else{
            resultList = statisticsDAO.getInterceptNum(dateVO);
        }
        return resultList;
    }

    @Override
    public List<Map> getInterceptNumNew(DateVO dateVO,Result<List<SysDictDataResponse>> suppressDictData) {
        List<Map> resultList = new ArrayList<>();
        String flag = Constants.ONE;
        if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
            flag= suppressDictData.getData().get(0).getDictValue();
        }
        if(!Constants.ONE.equals(flag)) {
            Date date = DateUtils.stringToDateYMDHMS(dateVO.getHandleDate());
            String yyyyMM = DateUtils.date2Str(date, Constants.YY_MM);
            String tableName = Constants.WARN_WARNING_INTERCEPT + "_" + yyyyMM;
            MongoDatabase database = mongoTemplate.getDb();
            MongoCollection<Document> collection = database.getCollection(tableName);

            // 构建聚合管道
            List<Document> pipeline = Arrays.asList(
                    // Match 阶段：筛选符合条件的数据
                    new Document(Constants.$_MATCH,
                            new Document()
                                    .append(Constants.PARAM_DELETE_FLAG_FIELD, "0")
                                    .append(Constants.RES_JUDGE_RESULT, null)
                                    .append(Constants.PARAM_ALARM_TIME_FIELD, new Document(Constants.$_REGEX, "^" + dateVO.getHandleDate()))
                    ),
                    // Group 阶段：按字段分组并统计事件数量
                    new Document(Constants.$_GROUP,
                            new Document("_id",
                                    new Document(Constants.PARAM_PROVINCE_ID, Constants.$_PROVINCE_CODE)
                                            .append(Constants.PARAM_CITY_ID, Constants.$_CITY_CODE)
                                            .append(Constants.PARAM_COUNTY_ID, Constants.$_COUNTY_CODE)
                                            .append(Constants.PARAM_PROVINCE_NAME_FIELD, "$province_name")
                                            .append(Constants.PARAM_CITY_NAME_FIELD, "$city_name")
                                            .append(Constants.PARAM_COUNTY_NAME_FIELD, "$county_name")
                                            .append(Constants.PARAM_WARNING_TYPE_ID_FIELD, Constants.$_WARNING_TYPE_ID)
                                            .append(Constants.RES_TENANT_ID, Constants.$_TENANT_ID)
                                            .append(Constants.RES_DEVICE_CODE, Constants.$_DEVICE_CODE)
                                            .append(Constants.RES_WARNING_SOURCE, Constants.$_WARNING_SOURCE)
                                            .append(Constants.PARAM_INDUSTRY_CODE_FIELD, Constants.$_INDUSTRY_CODE)
                                            .append(Constants.YEAR_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 0, 4)))
                                            .append(Constants.MONTH_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 5, 2)))
                                            .append(Constants.DAY_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 8, 2)))
                            )
                                    .append(Constants.PARAM_TODAY_NUM, new Document("$sum", 1))
                    ),
                    // Project 阶段：格式化输出结果字段
                    new Document(Constants.$_PROJECT,
                            new Document("_id", 0)
                                    .append(Constants.PARAM_PROVINCE_ID, "$_id.province_id")
                                    .append(Constants.PARAM_CITY_ID, "$_id.city_id")
                                    .append(Constants.PARAM_COUNTY_ID, "$_id.county_id")
                                    .append(Constants.PARAM_PROVINCE_NAME_FIELD, "$_id.province_name")
                                    .append(Constants.PARAM_CITY_NAME_FIELD, "$_id.city_name")
                                    .append(Constants.PARAM_COUNTY_NAME_FIELD, "$_id.county_name")
                                    .append(Constants.PARAM_WARNING_TYPE_ID_FIELD, "$_id.warning_type_id")
                                    .append(Constants.PARAM_ALARM_TIME_FIELD, "$_id.alarm_time")
                                    .append(Constants.RES_MODIFIER_DATE, "$_id.modifier_date")
                                    .append(Constants.RES_TENANT_ID, Constants.$_ID_TENANT_ID)
                                    .append(Constants.RES_DEVICE_CODE, Constants.$_ID_DEVICE_CODE)
                                    .append(Constants.RES_WARNING_SOURCE, Constants.$_ID_WARNING_SOURCE)
                                    .append(Constants.PARAM_INDUSTRY_CODE_FIELD, Constants.$_ID_INDUSTRY_CODE)
                                    .append(Constants.PARAM_TODAY_NUM, "$today_num")
                                    .append(Constants.YEAR_STR, Constants.$_ID_YEAR_STR)
                                    .append(Constants.MONTH_STR, Constants.$_ID_MONTH_STR)
                                    .append(Constants.DAY_STR, Constants.$_ID_DAY_STR)
                    )
            );

            // 执行聚合查询
            List<Document> results = collection.aggregate(pipeline).into(new ArrayList<>());
            for (Document doc : results) {
                Map map = new HashMap();
                map.put(Constants.PARAM_PROVINCE_ID,doc.get(Constants.PARAM_PROVINCE_ID));
                map.put(Constants.PARAM_CITY_ID,doc.get(Constants.PARAM_CITY_ID));
                map.put(Constants.PARAM_COUNTY_ID,doc.get(Constants.PARAM_COUNTY_ID));
                map.put(Constants.PARAM_PROVINCE_NAME_FIELD,doc.get(Constants.PARAM_PROVINCE_NAME_FIELD));
                map.put(Constants.PARAM_CITY_NAME_FIELD,doc.get(Constants.PARAM_CITY_NAME_FIELD));
                map.put(Constants.PARAM_COUNTY_NAME_FIELD,doc.get(Constants.PARAM_COUNTY_NAME_FIELD));
                map.put(Constants.PARAM_WARNING_TYPE_ID_FIELD,doc.get(Constants.PARAM_WARNING_TYPE_ID_FIELD));
                map.put(Constants.PARAM_ALARM_TIME_FIELD,doc.get(Constants.PARAM_ALARM_TIME_FIELD));
                map.put(Constants.RES_MODIFIER_DATE,doc.get(Constants.RES_MODIFIER_DATE));
                map.put(Constants.RES_TENANT_ID,doc.get(Constants.RES_TENANT_ID));
                map.put(Constants.RES_DEVICE_CODE,doc.get(Constants.RES_DEVICE_CODE));
                map.put(Constants.RES_WARNING_SOURCE,doc.get(Constants.RES_WARNING_SOURCE));
                map.put(Constants.PARAM_INDUSTRY_CODE_FIELD,doc.get(Constants.PARAM_INDUSTRY_CODE_FIELD));
                map.put(Constants.PARAM_TODAY_NUM,doc.get(Constants.PARAM_TODAY_NUM));
                map.put(Constants.YEAR_STR,doc.get(Constants.YEAR_STR));
                map.put(Constants.MONTH_STR,doc.get(Constants.MONTH_STR));
                map.put(Constants.DAY_STR,doc.get(Constants.DAY_STR));
                resultList.add(map);
            }
        }else{
            resultList = statisticsDAO.getInterceptNumNew(dateVO);
        }
        return resultList;
    }

    @Override
    public List<Map> getEventNumListByInterceptHour2(DateVO dateVO,Result<List<SysDictDataResponse>> suppressDictData) {
        List<Map> resultList = new ArrayList<>();
        String flag = Constants.ONE;
        if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
            flag= suppressDictData.getData().get(0).getDictValue();
        }
        if(!Constants.ONE.equals(flag)){
            Date date = DateUtils.stringToDateYMDHMS(dateVO.getHandleDate());
            String yyyyMM = DateUtils.date2Str(date,Constants.YY_MM);
            String tableName = Constants.WARN_WARNING_INTERCEPT+"_"+yyyyMM;
            MongoDatabase database = mongoTemplate.getDb();
            MongoCollection<Document> collection = database.getCollection(tableName);
            // 构建聚合管道
            List<Document> pipeline = Arrays.asList(
                    // Match 阶段：筛选符合条件的数据
                    new Document(Constants.$_MATCH,
                            new Document()
                                    .append(Constants.PARAM_DELETE_FLAG_FIELD, "0")
                                    .append(Constants.RES_JUDGE_RESULT, null)
                                    .append(Constants.PARAM_ALARM_TIME_FIELD, new Document(Constants.$_REGEX, "^" + dateVO.getHandleDate()))
                    ),
                    // Group 阶段：按字段分组并统计事件数量
                    new Document(Constants.$_GROUP,
                            new Document("_id",
                                    new Document(Constants.RES_TENANT_ID, Constants.$_TENANT_ID)
                                            .append(Constants.PARAM_PRO_CODE, Constants.$_PROVINCE_CODE)
                                            .append(Constants.PARAM_CITY_CODE, Constants.$_CITY_CODE)
                                            .append(Constants.PARAM_COUNTY_CODE, Constants.$_COUNTY_CODE)
                                            .append(Constants.PARAM_TOWN_CODE_FILD, Constants.$_TOWN_CODE)
                                            .append(Constants.RES_DEVICE_CODE, Constants.$_DEVICE_CODE)
                                            .append(Constants.HOUR_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 11, 2)))
                                            .append(Constants.YEAR_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 0, 4)))
                                            .append(Constants.MONTH_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 5, 2)))
                                            .append(Constants.DAY_STR, new Document(Constants.$_SUBSTR, Arrays.asList(Constants.$_ALARM_TIME, 8, 2)))
                            )
                                    .append(Constants.EVENT_NUM, new Document("$sum", 1))
                    ),
                    // Project 阶段：格式化输出结果字段
                    new Document(Constants.$_PROJECT,
                            new Document("_id", 0)
                                    .append(Constants.RES_TENANT_ID, Constants.$_ID_TENANT_ID)
                                    .append(Constants.PARAM_PRO_CODE, Constants.$_ID_PROVINCE_CODE)
                                    .append(Constants.PARAM_CITY_CODE, Constants.$_ID_CITY_CODE)
                                    .append(Constants.PARAM_COUNTY_CODE, Constants.$_ID_COUNTY_CODE)
                                    .append(Constants.PARAM_TOWN_CODE_FILD, Constants.$_ID_TOWN_CODE)
                                    .append(Constants.RES_DEVICE_CODE, Constants.$_ID_DEVICE_CODE)
                                    .append(Constants.HOUR_STR, Constants.$_ID_HOUR_STR)
                                    .append(Constants.YEAR_STR, Constants.$_ID_YEAR_STR)
                                    .append(Constants.MONTH_STR, Constants.$_ID_MONTH_STR)
                                    .append(Constants.DAY_STR, Constants.$_ID_DAY_STR)
                                    .append(Constants.EVENT_NUM, Constants.$_EVENT_NUM)
                    )
            );

            // 执行聚合查询
            List<Document> results = collection.aggregate(pipeline).into(new ArrayList<>());
            for (Document doc:results) {
                Map map = new HashMap();
                map.put(Constants.RES_TENANT_ID,doc.get(Constants.RES_TENANT_ID));
                map.put(Constants.PARAM_PRO_CODE,doc.get(Constants.PARAM_PRO_CODE));
                map.put(Constants.PARAM_CITY_CODE,doc.get(Constants.PARAM_CITY_CODE));
                map.put(Constants.PARAM_COUNTY_CODE,doc.get(Constants.PARAM_COUNTY_CODE));
                map.put(Constants.PARAM_TOWN_CODE_FILD,doc.get(Constants.PARAM_TOWN_CODE_FILD));
                map.put(Constants.PARAM_ALARM_TIME_FIELD,doc.get(Constants.PARAM_ALARM_TIME_FIELD));
                map.put(Constants.RES_DEVICE_CODE,doc.get(Constants.RES_DEVICE_CODE));
                map.put(Constants.HOUR_STR,doc.get(Constants.HOUR_STR));
                map.put(Constants.YEAR_STR,doc.get(Constants.YEAR_STR));
                map.put(Constants.MONTH_STR,doc.get(Constants.MONTH_STR));
                map.put(Constants.DAY_STR,doc.get(Constants.DAY_STR));
                map.put(Constants.HANDLE_DATE_TIME,doc.get(Constants.HANDLE_DATE_TIME));
                resultList.add(map);
            }
        }else{
            resultList= towerStatisticsDAO.getEventNumListByInterceptHour(dateVO);
        }
        return resultList;
    }

    /**
     * 按小时获取事件数量列表（新）。
     * 判断从 MongoDB 聚合查询或从统计数据库查询，并返回符合条件的事件数据。
     *
     * @param dateVO 包含日期信息的对象，用于查询事件数据
     * @return 返回事件数量列表，每个事件以 Map 的形式存储
     */
    @Override
    public List<Map> getEventNumListByInterceptHourNew2(DateVO dateVO) {
        List<Map> resultList = new ArrayList<>();
        String flag = Constants.ONE;
        Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.TASK_SELECT_INTERCEPT_POWER_FLAG);
        if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
            flag= suppressDictData.getData().get(0).getDictValue();
        }
        if(!Constants.ONE.equals(flag)){
            Date date = DateUtils.stringToDateYMDHMS(dateVO.getHandleDate());
            String yyyyMM = DateUtils.date2Str(date,Constants.YY_MM);
            String tableName = Constants.WARN_WARNING_INTERCEPT+"_"+yyyyMM;
            MongoDatabase database = mongoTemplate.getDb();
            MongoCollection<Document> collection = database.getCollection(tableName);
            // 构建查询条件
            Document query = new Document()
                    .append(Constants.PARAM_DELETE_FLAG_FIELD, "0")
                    .append(Constants.RES_JUDGE_RESULT, null)
                    .append(Constants.PARAM_ALARM_TIME_FIELD, new Document(Constants.$_REGEX, "^" + dateVO.getHandleDate()));

            // 构建聚合管道
            List<Bson> pipeline = Arrays.asList(
                    // Match 阶段：筛选符合条件的数据
                    Aggregates.match(Filters.and(Filters.eq(Constants.PARAM_DELETE_FLAG_FIELD, "0"), Filters.eq(Constants.RES_JUDGE_RESULT, null))),
                    Aggregates.match(Filters.regex(Constants.PARAM_ALARM_TIME_FIELD, "^" + dateVO.getHandleDate())),
                    // Group 阶段：按字段分组并统计事件数量
                    Aggregates.group(
                            new Document(Constants.RES_TENANT_ID, Constants.$_TENANT_ID)
                                    .append(Constants.PARAM_PRO_CODE, Constants.$_PROVINCE_CODE)
                                    .append(Constants.PARAM_CITY_CODE, Constants.$_CITY_CODE)
                                    .append(Constants.RES_WARNING_SOURCE, Constants.$_WARNING_SOURCE)
                                    .append(Constants.PARAM_WARNING_TYPE_ID_FIELD, Constants.$_WARNING_TYPE_ID)
                                    .append(Constants.HOUR_STR, new Document("$hour", Constants.$_ALARM_TIME))
                                    .append(Constants.YEAR_STR, new Document("$year", Constants.$_ALARM_TIME))
                                    .append(Constants.MONTH_STR, new Document("$month", Constants.$_ALARM_TIME))
                                    .append(Constants.DAY_STR, new Document("$dayOfMonth", Constants.$_ALARM_TIME)),
                            Accumulators.sum(Constants.EVENT_NUM, 1)
                    ),
                    // Project 阶段：格式化输出结果字段
                    Aggregates.project(
                            Projections.fields(
                                    Projections.excludeId(),
                                    Projections.include(Constants.RES_TENANT_ID),
                                    Projections.include(Constants.PARAM_PRO_CODE),
                                    Projections.include(Constants.PARAM_CITY_CODE),
                                    Projections.include(Constants.RES_WARNING_SOURCE),
                                    Projections.include(Constants.PARAM_WARNING_TYPE_ID_FIELD),
                                    Projections.include(Constants.HOUR_STR),
                                    Projections.computed(Constants.EVENT_NUM, Constants.$_EVENT_NUM),
                                    Projections.include(Constants.YEAR_STR),
                                    Projections.include(Constants.MONTH_STR),
                                    Projections.include(Constants.DAY_STR),
                                    Projections.computed(Constants.HANDLE_DATE_TIME, dateVO.getHandleDateTime())
                            )
                    ),
                    // Sort 阶段：按字段排序
                    Aggregates.sort(Sorts.ascending(Constants.RES_TENANT_ID, Constants.PARAM_PRO_CODE, Constants.PARAM_CITY_CODE, Constants.RES_WARNING_SOURCE, Constants.PARAM_WARNING_TYPE_ID_FIELD, Constants.HOUR_STR, Constants.YEAR_STR, Constants.MONTH_STR, Constants.DAY_STR))
            );

            List<Document> results = collection.aggregate(pipeline).into(new ArrayList<>());
            // 解析查询结果，将 Document 转换为 Map 并加入结果列表
            for (Document doc:results) {
                Map map = new HashMap();
                map.put(Constants.RES_TENANT_ID,doc.get(Constants.RES_TENANT_ID));
                map.put(Constants.PARAM_PRO_CODE,doc.get(Constants.PARAM_PRO_CODE));
                map.put(Constants.PARAM_CITY_CODE,doc.get(Constants.PARAM_CITY_CODE));
                map.put(Constants.PARAM_COUNTY_CODE,doc.get(Constants.PARAM_COUNTY_CODE));
                map.put(Constants.RES_WARNING_SOURCE,doc.get(Constants.RES_WARNING_SOURCE));
                map.put(Constants.PARAM_WARNING_TYPE_ID_FIELD,doc.get(Constants.PARAM_WARNING_TYPE_ID_FIELD));
                map.put(Constants.PARAM_TOWN_CODE_FILD,doc.get(Constants.PARAM_TOWN_CODE_FILD));
                map.put(Constants.PARAM_ALARM_TIME_FIELD,doc.get(Constants.PARAM_ALARM_TIME_FIELD));
                map.put(Constants.RES_DEVICE_CODE,doc.get(Constants.RES_DEVICE_CODE));
                map.put(Constants.HOUR_STR,doc.get(Constants.HOUR_STR));
                map.put(Constants.YEAR_STR,doc.get(Constants.YEAR_STR));
                map.put(Constants.MONTH_STR,doc.get(Constants.MONTH_STR));
                map.put(Constants.DAY_STR,doc.get(Constants.DAY_STR));
                map.put(Constants.HANDLE_DATE_TIME,doc.get(Constants.HANDLE_DATE_TIME));
                resultList.add(map);
            }
        }else{
            resultList= towerStatisticsDAO.getEventNumListByInterceptHourNew(dateVO);
        }
        return resultList;
    }
}
