package com.chinatower.platform.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.enums.ClientType;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.platform.dto.LogStatisticsQuery;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.mongo.SysLogininfoMongoMapper;
import com.chinatower.platform.res.*;
import com.chinatower.platform.service.LogStatisticsService;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LogStatisticsServiceImpl implements LogStatisticsService {
    @Resource
    SysLogininfoMongoMapper logininforMapper;
    String[] hourTimes = new String[]{
            "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
            "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "24:00"
    };
    //表名称
    private static final String LOG_LOGININFO = "log_logininfo";
    //筛选条件 登录时间
    private static final String loginTime = "loginTime";
    //分组 统计 登录端
    private static final String clientType = "clientType";
    //分组 统计 登录名称
    private static final String userName = "userName";
    //分组 统计 系统
    private static final String os = "os";
    //分组 统计 浏览器
    private static final String browser = "browser";
    //手机型号
    private static final String phoneModel = "phoneModel";
    //版本号
    private static final String appVersion = "appVersion";
    //状态
    private static final String status = "status";
    //原因
    private static final String msg = "msg";
    //租户
    private static final String tenantId = "tenantId";
    //租户名称
    private static final String tenantName = "tenantName";
    //行业
    private static final String industryCode = "industryCode";
    private static final String COUNT = "count";

    /**
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 时间条件
     */
    private Criteria getTimeCriteria(String beginTime, String endTime) {
        return Criteria.where(loginTime).gte(parseData(beginTime)).lte(parseData(endTime));
    }

    private Criteria getTimeCriteria(LogStatisticsQuery dto) {
        String beginTime = dto.getBeginTime();
        if (StringUtils.isEmpty(beginTime)) {
            beginTime = getBeginTimeStr(1);
        }else {
            beginTime = " 00:00:00";
        }
        String endTime = dto.getEndTime();
        if (StringUtils.isEmpty(endTime)) {
            endTime = getEndTimeStr();
        }else {
            endTime = " 23:59:59";
        }
        return getTimeCriteria(beginTime, endTime);
    }

    /**
     * 判断是否加入租户搜索
     *
     * @param operations
     */
    private void setUserTenantId(List<AggregationOperation> operations, LogStatisticsQuery dto) {
        if (StringUtils.isNotEmpty(dto.getTenantKey())) {
            operations.add(Aggregation.match(Criteria.where(tenantId).is(dto.getTenantKey()).orOperator(Criteria.where(tenantName).regex("^.*" + dto.getTenantKey() + ".*$"))));
        }
//        SysUser user = SecurityUtils.getLoginUser().getUser();
//        //判断是否租户
//        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(user.getUserType())) {
//            operations.add(Aggregation.match(Criteria.where(tenantId).is(user.getTenantId())));
//            operations.add(Aggregation.match(Criteria.where(industryCode).is(user.getIndustryCode())));
//        }
    }

    @Override
    public Map<String, List<ClientTypeRes>> clientTypeRes(LogStatisticsQuery dto) {
        AggregationExpression dateExpression = DateOperators.DateToString
                .dateOf(loginTime)
                .toString("%Y-%m-%d")
                .withTimezone(
                        DateOperators.Timezone.valueOf("+08")
                );
        //登录端
        List<String> clientTypes = Arrays.stream(ClientType.values()).map(ClientType::getClientType).collect(Collectors.toList());
        Criteria criteria1 = Criteria.where(clientType).in(clientTypes);

        Criteria timeCriteria = getTimeCriteria(dto);
        //搜索条件
        List<AggregationOperation> operations = new ArrayList<>();
        setUserTenantId(operations, dto);
        operations.add(Aggregation.match(criteria1));
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.project(clientType).and(dateExpression).as("date"));
        operations.add(Aggregation.group("date", clientType)
                .first("date").as("date")
                .first(clientType).as(clientType).count().as(COUNT));
        operations.add(Aggregation.sort(Sort.Direction.DESC, "date"));

        Aggregation aggregation = Aggregation.newAggregation(operations);

        MongoTemplate mongoTemplate = logininforMapper.getTemplate(MongoDBType.PRIMARY);
        List<ClientTypeRes> list = mongoTemplate.aggregate(aggregation, LOG_LOGININFO, ClientTypeRes.class).getMappedResults();
        Map<String, List<ClientTypeRes>> listMap = list.stream().collect(Collectors.groupingBy(ClientTypeRes::getDate));
        log.info(JSON.toJSONString(listMap));
        return listMap;
    }

    @Override
    public TableDataInfo userLoginRes(LogStatisticsQuery dto) {
        Criteria timeCriteria = getTimeCriteria(dto);
        //搜索条件
        List<AggregationOperation> operations = new ArrayList<>();
        setUserTenantId(operations, dto);
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.group(userName)
                .first(userName).as(userName).count().as(COUNT));
        operations.add(Aggregation.sort(Sort.Direction.DESC, COUNT));
        operations.add(Aggregation.skip(dto.getPageNum() == null ? 0L : (dto.getPageNum() - 1)));
        operations.add(Aggregation.limit(dto.getPageSize() == null ? 5 : dto.getPageSize()));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        MongoTemplate mongoTemplate = logininforMapper.getTemplate(MongoDBType.PRIMARY);
        List<UserLoginRes> list = mongoTemplate.aggregate(aggregation, LOG_LOGININFO, UserLoginRes.class).getMappedResults();
        log.info("登录人统计数据 {}", JSON.toJSONString(list));
        //查询数量
        List<AggregationOperation> countOperations = new ArrayList<>();
        setUserTenantId(countOperations, dto);
        countOperations.add(Aggregation.match(timeCriteria));
        countOperations.add(Aggregation.group(userName));
        countOperations.add(Aggregation.count().as(COUNT));
        Aggregation aggregationCount = Aggregation.newAggregation(countOperations);
        List<JSONObject> counts = mongoTemplate.aggregate(aggregationCount, LOG_LOGININFO, JSONObject.class).getMappedResults();
        Long count = counts.get(0).getLong(COUNT);

        log.info("登录人数量 {}", count);
        TableDataInfo info = new TableDataInfo();
        info.setRows(list);
        info.setTotal(count);
        info.setCode(200);
        return info;
    }

    @Override
    public TableDataInfo tenantLoginCountRes(LogStatisticsQuery dto) {
        Criteria timeCriteria = getTimeCriteria(dto);
        //搜索条件
        List<AggregationOperation> operations = new ArrayList<>();
        setUserTenantId(operations, dto);
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.match(Criteria.where(tenantId).exists(true)));
        operations.add(Aggregation.group(tenantId).first(tenantId).as(tenantId).count().as(COUNT));
        operations.add(Aggregation.sort(Sort.Direction.DESC, COUNT));
        operations.add(Aggregation.skip(dto.getPageNum() == null ? 0L : (dto.getPageNum() - 1)));
        operations.add(Aggregation.limit(dto.getPageSize() == null ? 5 : dto.getPageSize()));

        Aggregation aggregation = Aggregation.newAggregation(operations);


        MongoTemplate mongoTemplate = logininforMapper.getTemplate(MongoDBType.PRIMARY);
        List<TenantLoginCountRes> list = mongoTemplate.aggregate(aggregation, LOG_LOGININFO, TenantLoginCountRes.class).getMappedResults();
        log.info("租户登录次数数据 {}", JSON.toJSONString(list));

        List<AggregationOperation> countOperations = new ArrayList<>();
        setUserTenantId(countOperations, dto);
        countOperations.add(Aggregation.match(timeCriteria));
        countOperations.add(Aggregation.match(Criteria.where(tenantId).exists(true)));
        countOperations.add(Aggregation.group(tenantId));
        countOperations.add(Aggregation.count().as(COUNT));
        Aggregation aggregationCount = Aggregation.newAggregation(countOperations);
        List<JSONObject> counts = mongoTemplate.aggregate(aggregationCount, LOG_LOGININFO, JSONObject.class).getMappedResults();
        Long count = counts.get(0).getLong(COUNT);
        log.info("租户登录次数数据数量 {}", count);
        TableDataInfo info = new TableDataInfo();
        info.setRows(list);
        info.setTotal(count);
        info.setCode(200);
        return info;
    }

    @Override
    public List<HoursRes> hoursRes(LogStatisticsQuery dto) {
        AggregationExpression dateExpression = DateOperators.DateToString
                .dateOf(loginTime)
                .toString("%H:00")
                .withTimezone(
                        DateOperators.Timezone.valueOf("+08")
                );
        Criteria timeCriteria = getTimeCriteria(dto);

        List<AggregationOperation> operations = new ArrayList<>();
        setUserTenantId(operations, dto);
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.project(clientType).and(dateExpression).as("date"));
        operations.add(Aggregation.group("date")
                .first("date").as("date").count().as(COUNT));
        operations.add(Aggregation.sort(Sort.Direction.ASC, "date"));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        MongoTemplate mongoTemplate = logininforMapper.getTemplate(MongoDBType.PRIMARY);
        List<HoursRes> list = mongoTemplate.aggregate(aggregation, LOG_LOGININFO, HoursRes.class).getMappedResults();
        Map<String, List<HoursRes>> listMap = list.stream().collect(Collectors.groupingBy(HoursRes::getDate));

        List<HoursRes> arrayList = new ArrayList<>();
        for (int i = 0; i < hourTimes.length - 1; i++) {
            List<HoursRes> res = listMap.get(hourTimes[i]);
            HoursRes hoursRes;
            if (res == null || res.isEmpty()) {
                hoursRes = new HoursRes();
                hoursRes.setCount(0L);
            } else {
                hoursRes = res.get(0);
            }
            hoursRes.setDate(hourTimes[i] + "-" + hourTimes[i + 1]);
            arrayList.add(hoursRes);
        }
        log.info("24小时分布 {}", JSON.toJSONString(arrayList));
        return arrayList;
    }

    @Override
    public List<PcOsRes> pcOsRes(LogStatisticsQuery dto) {
        Criteria timeCriteria = getTimeCriteria(dto);

        List<AggregationOperation> operations = new ArrayList<>();
        setUserTenantId(operations, dto);
        operations.add(Aggregation.match(Criteria.where(clientType).is(ClientType.WEB_MANAGE.getClientType())));
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.group(os)
                .first(os).as(os).count().as(COUNT));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        MongoTemplate mongoTemplate = logininforMapper.getTemplate(MongoDBType.PRIMARY);
        List<PcOsRes> list = mongoTemplate.aggregate(aggregation, LOG_LOGININFO, PcOsRes.class).getMappedResults();
        log.info("pc系统统计数据 {}", JSON.toJSONString(list));
        return list;
    }

    @Override
    public TableDataInfo pcBrowserRes(LogStatisticsQuery dto) {
        Criteria timeCriteria = getTimeCriteria(dto);

        List<AggregationOperation> operations = new ArrayList<>();
        setUserTenantId(operations, dto);
        operations.add(Aggregation.match(Criteria.where(clientType).is(ClientType.WEB_MANAGE.getClientType())));
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.group(browser).first(browser).as(browser).count().as(COUNT));
        operations.add(Aggregation.sort(Sort.Direction.DESC, COUNT));
        operations.add(Aggregation.skip(dto.getPageNum() == null ? 0L : (dto.getPageNum() - 1)));
        operations.add(Aggregation.limit(dto.getPageSize() == null ? 5 : dto.getPageSize()));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        MongoTemplate mongoTemplate = logininforMapper.getTemplate(MongoDBType.PRIMARY);
        List<PcBrowserRes> list = mongoTemplate.aggregate(aggregation, LOG_LOGININFO, PcBrowserRes.class).getMappedResults();
        log.info("pc浏览器统计数据 {}", JSON.toJSONString(list));

        //查询数量
        List<AggregationOperation> countOperations = new ArrayList<>();
        setUserTenantId(countOperations, dto);
        countOperations.add(Aggregation.match(Criteria.where(clientType).is(ClientType.WEB_MANAGE.getClientType())));
        countOperations.add(Aggregation.match(timeCriteria));
        countOperations.add(Aggregation.group(browser));
        countOperations.add(Aggregation.count().as(COUNT));

        Aggregation aggregationCount = Aggregation.newAggregation(countOperations);
        List<JSONObject> counts = mongoTemplate.aggregate(aggregationCount, LOG_LOGININFO, JSONObject.class).getMappedResults();
        Long count = counts.get(0).getLong(COUNT);
        log.info("pc浏览器统计数量 {}", count);
        TableDataInfo info = new TableDataInfo();
        info.setRows(list);
        info.setTotal(count);
        info.setCode(200);
        return info;
    }

    @Override
    public List<PcOsRes> appOsRes(LogStatisticsQuery dto) {
        Criteria timeCriteria = getTimeCriteria(dto);

        List<AggregationOperation> operations = new ArrayList<>();
        setUserTenantId(operations, dto);
        operations.add(Aggregation.match(Criteria.where(clientType).is(ClientType.APP_MANAGE.getClientType())));
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.group(os).first(os).as(os).count().as(COUNT));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        MongoTemplate mongoTemplate = logininforMapper.getTemplate(MongoDBType.PRIMARY);
        List<PcOsRes> list = mongoTemplate.aggregate(aggregation, LOG_LOGININFO, PcOsRes.class).getMappedResults();
        log.info("app系统统计数据 {}", JSON.toJSONString(list));
        return list;
    }

    @Override
    public TableDataInfo appModelRes(LogStatisticsQuery dto) {
        Criteria timeCriteria = getTimeCriteria(dto);

        List<AggregationOperation> operations = new ArrayList<>();
        setUserTenantId(operations, dto);
        operations.add(Aggregation.match(Criteria.where(clientType).is(ClientType.APP_MANAGE.getClientType())));
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.group(phoneModel).first(phoneModel).as(phoneModel).count().as(COUNT));
        operations.add(Aggregation.sort(Sort.Direction.DESC, COUNT));
        operations.add(Aggregation.skip(dto.getPageNum() == null ? 0L : (dto.getPageNum() - 1)));
        operations.add(Aggregation.limit(dto.getPageSize() == null ? 5 : dto.getPageSize()));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        MongoTemplate mongoTemplate = logininforMapper.getTemplate(MongoDBType.PRIMARY);
        List<PcBrowserRes> list = mongoTemplate.aggregate(aggregation, LOG_LOGININFO, PcBrowserRes.class).getMappedResults();
        log.info("app型号统计数据 {}", JSON.toJSONString(list));

        List<AggregationOperation> countOperations = new ArrayList<>();
        setUserTenantId(countOperations, dto);
        countOperations.add(Aggregation.match(Criteria.where(clientType).is(ClientType.APP_MANAGE.getClientType())));
        countOperations.add(Aggregation.match(timeCriteria));
        countOperations.add(Aggregation.group(phoneModel));
        countOperations.add(Aggregation.count().as(COUNT));

        Aggregation aggregationCount = Aggregation.newAggregation(countOperations);
        List<JSONObject> counts = mongoTemplate.aggregate(aggregationCount, LOG_LOGININFO, JSONObject.class).getMappedResults();
        Long count = counts.get(0).getLong(COUNT);
        log.info("app型号统计数量 {}", count);
        TableDataInfo info = new TableDataInfo();
        info.setRows(list);
        info.setTotal(count);
        info.setCode(200);
        return info;
    }

    @Override
    public TableDataInfo appVersionRes(LogStatisticsQuery dto) {
        Criteria timeCriteria = getTimeCriteria(dto);

        List<AggregationOperation> operations = new ArrayList<>();
        setUserTenantId(operations, dto);
        operations.add(Aggregation.match(Criteria.where(clientType).is(ClientType.APP_MANAGE.getClientType())));
        operations.add(Aggregation.match(Criteria.where(appVersion).exists(true)));
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.group(appVersion).first(appVersion).as(appVersion).count().as(COUNT));
        operations.add(Aggregation.sort(Sort.Direction.DESC, COUNT));
        operations.add(Aggregation.skip(dto.getPageNum() == null ? 0L : (dto.getPageNum() - 1)));
        operations.add(Aggregation.limit(dto.getPageSize() == null ? 5 : dto.getPageSize()));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        MongoTemplate mongoTemplate = logininforMapper.getTemplate(MongoDBType.PRIMARY);
        List<PcBrowserRes> list = mongoTemplate.aggregate(aggregation, LOG_LOGININFO, PcBrowserRes.class).getMappedResults();
        log.info("app版本统计数据 {}", JSON.toJSONString(list));

        List<AggregationOperation> countOperations = new ArrayList<>();
        setUserTenantId(countOperations, dto);
        countOperations.add(Aggregation.match(Criteria.where(clientType).is(ClientType.APP_MANAGE.getClientType())));
        countOperations.add(Aggregation.match(Criteria.where(appVersion).exists(true)));
        countOperations.add(Aggregation.match(timeCriteria));
        countOperations.add(Aggregation.group(appVersion));
        countOperations.add(Aggregation.count().as(COUNT));
        Aggregation aggregationCount = Aggregation.newAggregation(countOperations);
        List<JSONObject> counts = mongoTemplate.aggregate(aggregationCount, LOG_LOGININFO, JSONObject.class).getMappedResults();
        Long count = counts.get(0).getLong(COUNT);
        log.info("app版本统计数量 {}", count);
        TableDataInfo info = new TableDataInfo();
        info.setRows(list);
        info.setTotal(count);
        info.setCode(200);
        return info;
    }

    @Override
    public List<LoginStatusRes> loginStatusRes(LogStatisticsQuery dto) {
        Criteria timeCriteria = getTimeCriteria(dto);

        List<AggregationOperation> operations = new ArrayList<>();
        setUserTenantId(operations, dto);
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.group(status).first(status).as(status).count().as(COUNT));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        MongoTemplate mongoTemplate = logininforMapper.getTemplate(MongoDBType.PRIMARY);
        List<LoginStatusRes> list = mongoTemplate.aggregate(aggregation, LOG_LOGININFO, LoginStatusRes.class).getMappedResults();
        log.info("登录状态统计数据 {}", JSON.toJSONString(list));
        return list;
    }

    @Override
    public List<FailureReasonRes> failureReasonRes(LogStatisticsQuery dto) {
        Criteria timeCriteria = getTimeCriteria(dto);

        List<AggregationOperation> operations = new ArrayList<>();
        setUserTenantId(operations, dto);
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.match(Criteria.where(status).is("1")));
        operations.add(Aggregation.group(msg).first(msg).as(msg).count().as(COUNT));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        MongoTemplate mongoTemplate = logininforMapper.getTemplate(MongoDBType.PRIMARY);
        List<FailureReasonRes> list = mongoTemplate.aggregate(aggregation, LOG_LOGININFO, FailureReasonRes.class).getMappedResults();
        log.info("失败原因统计数据 {}", JSON.toJSONString(list));
        return list;
    }


    @Override
    public TableDataInfo tenantSuccessRatioRes(LogStatisticsQuery dto) {
        Criteria timeCriteria = getTimeCriteria(dto);

        List<AggregationOperation> operations = new ArrayList<>();
        setUserTenantId(operations, dto);
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.match(Criteria.where(tenantId).exists(true)));
        operations.add(Aggregation.group(tenantId)
                .count().as("total")
                .sum(ConditionalOperators.when(Criteria.where(status).is("0")).then(1).otherwise(0)).as("success"));
        operations.add(Aggregation.project(tenantId)
                .and("_id").as(tenantId)
                .and(ArithmeticOperators.Divide.valueOf("success").divideBy("total")).as("scale"));
        operations.add(Aggregation.sort(Sort.Direction.DESC, "scale"));
        operations.add(Aggregation.skip(dto.getPageNum() == null ? 0L : (dto.getPageNum() - 1)));
        operations.add(Aggregation.limit(dto.getPageSize() == null ? 5 : dto.getPageSize()));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        MongoTemplate mongoTemplate = logininforMapper.getTemplate(MongoDBType.PRIMARY);
        List<TenantSuccessRatioRes> list = mongoTemplate.aggregate(aggregation, LOG_LOGININFO, TenantSuccessRatioRes.class).getMappedResults();
        log.info("登录成功率统计 {}", JSON.toJSONString(list));

        List<AggregationOperation> countOperations = new ArrayList<>();
        setUserTenantId(countOperations, dto);
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.match(Criteria.where(tenantId).exists(true)));
        operations.add(Aggregation.group(tenantId));
        operations.add(Aggregation.count().as(COUNT));

        Aggregation aggregationCount = Aggregation.newAggregation(countOperations);
        List<JSONObject> counts = mongoTemplate.aggregate(aggregationCount, LOG_LOGININFO, JSONObject.class).getMappedResults();
        Long count = counts.get(0).getLong(COUNT);
        TableDataInfo info = new TableDataInfo();
        info.setRows(list);
        info.setTotal(count);
        info.setCode(200);
        return info;
    }

    /**
     * @param type 1-昨天 2本周 3本月 4全年
     * @return
     */
    public static String getBeginTimeStr(int type) {
        Date date = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        switch (type) {
            case 1:
                return DateUtil.format(date, sdf);
            case 2:
                return DateUtil.format(DateUtil.beginOfWeek(date), sdf);
            case 3:
                return DateUtil.format(DateUtil.beginOfMonth(date), sdf);
            default:
                return DateUtil.format(DateUtil.beginOfYear(date), sdf);
        }
    }

    /**
     * @param format yyyy-MM-dd HH:mm:ss
     * @return data
     */
    public static Date parseData(String format) {
        log.info("解析日期 {}", format);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(format);
        } catch (Exception e) {
            log.error("时间解析错误", e);
        }
        return new Date();
    }

    /**
     * @return format
     */
    public String getEndTimeStr() {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        return DateUtil.format(new Date(), sdf);
    }
}
