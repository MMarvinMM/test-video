package com.chinatower.platform.modules.statistics.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.enums.ClientType;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.model.dto.AuthConfig;
import com.chinatower.platform.modules.statistics.dao.UserStatisticsDAO;
import com.chinatower.platform.modules.statistics.entity.StatisticsUserOnlineTime;
import com.chinatower.platform.modules.statistics.service.UserStatisticsService;
import com.chinatower.system.api.feign.client.WxFeignClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计分析-用户统计
 *
 * @version v1.5.80
 * @date 2023/04/07
 */
@Component
@Slf4j
public class UserStatisticsServiceImpl implements UserStatisticsService {

    @Autowired
    private UserStatisticsDAO userStatisticsDAO;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private WxFeignClient wxFeignClient;

    /**
     * redis scan size
     */
    @Value("${statistic.redis.scan:1000}")
    private Integer redisScanSize;


    /**
     * 用户登录24小时分布
     * 沉淀表：vm_statistics_custom_hour
     *
     * @param dateVO
     * @return
     */
    @Override
    public List<Map> getEventUserStatistics(DateVO dateVO) {
        Criteria timeCriteria;
        if (!StringUtils.isEmpty(dateVO.getDay())) {
            timeCriteria = getTimeCriteria();
        } else {
            timeCriteria = getMonthCriteria(Integer.parseInt(dateVO.getYear()), Integer.parseInt(dateVO.getMonth()));
        }
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.project(Constants.LOGIN_COUNT)
                .andExpression("hour(loginTime + 8 * 60 * 60 * 1000)").as(Constants.HOUR_STR)
                .andExpression("year(loginTime + 8 * 60 * 60 * 1000)").as(Constants.YEAR_STR)
                .andExpression("month(loginTime + 8 * 60 * 60 * 1000)").as(Constants.MONTH_STR)
                .andExpression("dayOfMonth(loginTime + 8 * 60 * 60 * 1000)").as(Constants.DAY_STR)
                .and(DateOperators.DateToString.dateOf(Constants.LOGIN_TIME).toString(Constants.FORMAT_DATE).withTimezone(DateOperators.Timezone.valueOf("+08"))).as(Constants.LOGIN_TIME)
                .and(Constants.USER_NAME).as(Constants.USER_NAME).andExclude(Constants._ID));
        operations.add(Aggregation.group(Constants.HOUR_STR, Constants.YEAR_STR, Constants.MONTH_STR, Constants.DAY_STR, Constants.USER_NAME, Constants.LOGIN_TIME)
                .count().as(Constants.LOGIN_COUNT)
                .first(Constants.HOUR_STR).as(Constants.HOUR_STR)
                .first(Constants.YEAR_STR).as(Constants.YEAR_STR)
                .first(Constants.MONTH_STR).as(Constants.MONTH_STR)
                .first(Constants.DAY_STR).as(Constants.DAY_STR)
                .first(Constants.USER_NAME).as(Constants.USER_NAME)
                .first(Constants.LOGIN_TIME).as(Constants.LOGIN_TIME));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Map> userCount = mongoTemplate.aggregate(aggregation, Constants.LOG_LOGININFO, Map.class).getMappedResults();
        log.info(userCount.toString());
        return userCount;
    }

    @Override
    public Boolean insertEventUserStatistics(DateVO dateVO, List<Map> list) {
        try {
            userStatisticsDAO.delUserStatistics(dateVO);
            List<Map> mapList = getUserInfoMaps(list);
            if (!CollectionUtils.isEmpty(mapList)) {
                List<List<Map>> partitionList = Lists.partition(mapList, Constants.TWO_THOUSAND);
                partitionList.forEach(item -> userStatisticsDAO.addUserStatistics(item));
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 用户登录时间统计
     * 沉淀表：vm_statistics_custom_type
     *
     * @param dateVO
     * @return
     */
    @Override
    public List<Map> getEventCustomListForType(DateVO dateVO) {
        //获取时间筛选条件
        Criteria timeCriteria;
        if (!StringUtils.isEmpty(dateVO.getDay())) {
            timeCriteria = getTimeCriteria();
        } else {
            timeCriteria = getMonthCriteria(Integer.parseInt(dateVO.getYear()), Integer.parseInt(dateVO.getMonth()));
        }
        //组合条件
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.project(Constants.LOGIN_COUNT)
                .and(Constants.CLIENT_TYPE).as(Constants.CLIENT_TYPE)
                .and(DateOperators.DateToString.dateOf(Constants.LOGIN_TIME).toString(Constants.FORMAT_DATE).withTimezone(DateOperators.Timezone.valueOf("+08"))).as(Constants.DATE)
                .and(Constants.USER_NAME).as(Constants.USER_NAME).andExclude(Constants._ID));
        operations.add(Aggregation.group(Constants.CLIENT_TYPE, Constants.USER_NAME, Constants.DATE)
                .count().as(Constants.LOGIN_COUNT)
                .first(Constants.USER_NAME).as(Constants.USER_NAME)
                .first(Constants.DATE).as(Constants.DATE)
                .first(Constants.CLIENT_TYPE).as(Constants.CLIENT_TYPE));
        operations.add(Aggregation.sort(Sort.Direction.DESC, Constants.DATE));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Map> clientTypeCount = mongoTemplate.aggregate(aggregation, Constants.LOG_LOGININFO, Map.class).getMappedResults();
        log.info(clientTypeCount.toString());
        return clientTypeCount;
    }

    @Override
    public Boolean addEventCustomListForType(DateVO dateVO, List<Map> list) {
        try {
            userStatisticsDAO.delCustomForType(dateVO);
            List<Map> mapList = getUserInfoMaps(list);
            if (!CollectionUtils.isEmpty(mapList)) {
                List<List<Map>> partitionList = Lists.partition(mapList, Constants.TWO_THOUSAND);
                partitionList.forEach(item -> userStatisticsDAO.addCustomForType(item));
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 用户登录次数统计
     * 沉淀表：vm_statistics_custom_duration
     *
     * @param dateVO
     * @return
     */
    @Override
    public List<Map> getEventCustomLoginCounts(DateVO dateVO) {
        Criteria timeCriteria;
        if (!StringUtils.isEmpty(dateVO.getDay())) {
            timeCriteria = getTimeCriteria();
        } else {
            timeCriteria = getMonthCriteria(Integer.parseInt(dateVO.getYear()), Integer.parseInt(dateVO.getMonth()));
        }
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.project(Constants.LOGIN_COUNT)
                .and(Constants.USER_NAME).as(Constants.USER_NAME)
                .and(DateOperators.DateToString.dateOf(Constants.LOGIN_TIME).toString(Constants.FORMAT_DATE).withTimezone(DateOperators.Timezone.valueOf("+08"))).as(Constants.LOGIN_TIME)
                .andExclude(Constants._ID));
        operations.add(Aggregation.group(Constants.USER_NAME, Constants.LOGIN_TIME)
                .count().as(Constants.LOGIN_COUNT)
                .first(Constants.USER_NAME).as(Constants.USER_NAME)
                .first(Constants.LOGIN_TIME).as(Constants.LOGIN_TIME));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Map> loginCounts = mongoTemplate.aggregate(aggregation, Constants.LOG_LOGININFO, Map.class).getMappedResults();
        log.info(loginCounts.toString());
        return loginCounts;
    }

    @Override
    public Boolean addEventCustomLoginCounts(DateVO dateVO, List<Map> list) {
        try {
            userStatisticsDAO.delCustomForDuration(dateVO);
            List<Map> mapList = getUserInfoMaps(list);
            if (!CollectionUtils.isEmpty(mapList)) {
                List<List<Map>> partitionList = Lists.partition(mapList, Constants.TWO_THOUSAND);
                partitionList.forEach(item -> userStatisticsDAO.addCustomForDuration(item));
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * PC浏览器排行
     * 沉淀表：vm_statistics_custom_pc_browser
     *
     * @param dateVO
     * @return
     */
    @Override
    public List<Map> getPcBrowserCounts(DateVO dateVO) {

        Criteria timeCriteria;
        if (!StringUtils.isEmpty(dateVO.getDay())) {
            timeCriteria = getTimeCriteria();
        } else {
            timeCriteria = getMonthCriteria(Integer.parseInt(dateVO.getYear()), Integer.parseInt(dateVO.getMonth()));
        }
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.match(Criteria.where(Constants.CLIENT_TYPE).is("web_manage")));
        operations.add(Aggregation.project(Constants.LOGIN_COUNT)
                .and("browser").as(Constants.BROWSER_NAME)
                .and(Constants.USER_NAME).as(Constants.USER_NAME)
                .and(DateOperators.DateToString.dateOf(Constants.LOGIN_TIME).toString(Constants.FORMAT_DATE).withTimezone(DateOperators.Timezone.valueOf("+08"))).as(Constants.LOGIN_TIME)
                .andExclude(Constants._ID));
        operations.add(Aggregation.group(Constants.BROWSER_NAME, Constants.USER_NAME, Constants.LOGIN_TIME)
                .count().as(Constants.LOGIN_COUNT)
                .first(Constants.BROWSER_NAME).as(Constants.BROWSER_NAME)
                .first(Constants.USER_NAME).as(Constants.USER_NAME)
                .first(Constants.LOGIN_TIME).as(Constants.LOGIN_TIME));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Map> browserCounts = mongoTemplate.aggregate(aggregation, Constants.LOG_LOGININFO, Map.class).getMappedResults();
        log.info(browserCounts.toString());
        return browserCounts;
    }

    @Override
    public Boolean addPcBrowserCounts(DateVO dateVO, List<Map> list) {
        try {
            userStatisticsDAO.delCustomForPcBrowser(dateVO);
            List<Map> mapList = getUserInfoMaps(list);
            if (!CollectionUtils.isEmpty(mapList)) {
                List<List<Map>> partitionList = Lists.partition(mapList, Constants.TWO_THOUSAND);
                partitionList.forEach(item -> userStatisticsDAO.addCustomForPcBrowser(item));
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * APP手机型号
     * 沉淀表：vm_statistics_custom_app_model
     *
     * @param dateVO
     * @return
     */
    @Override
    public List<Map> getIphoneModelCounts(DateVO dateVO) {
        Criteria timeCriteria;
        if (!StringUtils.isEmpty(dateVO.getDay())) {
            timeCriteria = getTimeCriteria();
        } else {
            timeCriteria = getMonthCriteria(Integer.parseInt(dateVO.getYear()), Integer.parseInt(dateVO.getMonth()));
        }
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.match(Criteria.where(Constants.CLIENT_TYPE).is(Constants.APP_MANAGE)));
        operations.add(Aggregation.project(Constants.LOGIN_COUNT)
                .and("phoneModel").as(Constants.PHONE_MODEL_NAME)
                .and(Constants.USER_NAME).as(Constants.USER_NAME)
                .and(DateOperators.DateToString.dateOf(Constants.LOGIN_TIME).toString(Constants.FORMAT_DATE).withTimezone(DateOperators.Timezone.valueOf("+08"))).as(Constants.LOGIN_TIME)
                .andExclude(Constants._ID));
        operations.add(Aggregation.group(Constants.PHONE_MODEL_NAME, Constants.USER_NAME, Constants.LOGIN_TIME)
                .count().as(Constants.LOGIN_COUNT)
                .first(Constants.PHONE_MODEL_NAME).as(Constants.PHONE_MODEL_NAME)
                .first(Constants.USER_NAME).as(Constants.USER_NAME)
                .first(Constants.LOGIN_TIME).as(Constants.LOGIN_TIME));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Map> phoneModelCounts = mongoTemplate.aggregate(aggregation, Constants.LOG_LOGININFO, Map.class).getMappedResults();
        log.info(phoneModelCounts.toString());
        return phoneModelCounts;
    }

    @Override
    public Boolean addIphoneModelCounts(DateVO dateVO, List<Map> list) {
        try {
            userStatisticsDAO.delCustomForIphoneModel(dateVO);
            List<Map> mapList = getUserInfoMaps(list);
            if (!CollectionUtils.isEmpty(mapList)) {
                List<List<Map>> partitionList = Lists.partition(mapList, Constants.TWO_THOUSAND);
                partitionList.forEach(item -> userStatisticsDAO.addCustomForIphoneModel(item));
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * APP版本排行
     * 沉淀表：vm_statistics_custom_app_version
     *
     * @param dateVO
     * @return
     */
    @Override
    public List<Map> getAppVersionCounts(DateVO dateVO) {

        Criteria timeCriteria;
        if (!StringUtils.isEmpty(dateVO.getDay())) {
            timeCriteria = getTimeCriteria();
        } else {
            timeCriteria = getMonthCriteria(Integer.parseInt(dateVO.getYear()), Integer.parseInt(dateVO.getMonth()));
        }
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.match(Criteria.where(Constants.CLIENT_TYPE).is(Constants.APP_MANAGE)));
        operations.add(Aggregation.project(Constants.LOGIN_COUNT)
                .and("appVersion").as(Constants.APP_VERSION_NAME)
                .and(Constants.USER_NAME).as(Constants.USER_NAME)
                .and(DateOperators.DateToString.dateOf(Constants.LOGIN_TIME).toString(Constants.FORMAT_DATE).withTimezone(DateOperators.Timezone.valueOf("+08"))).as(Constants.LOGIN_TIME)
                .andExclude(Constants._ID));
        operations.add(Aggregation.group(Constants.APP_VERSION_NAME, Constants.USER_NAME, Constants.LOGIN_TIME)
                .count().as(Constants.LOGIN_COUNT)
                .first(Constants.APP_VERSION_NAME).as(Constants.APP_VERSION_NAME)
                .first(Constants.USER_NAME).as(Constants.USER_NAME)
                .first(Constants.LOGIN_TIME).as(Constants.LOGIN_TIME));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Map> appVersionCounts = mongoTemplate.aggregate(aggregation, Constants.LOG_LOGININFO, Map.class).getMappedResults();
        log.info(appVersionCounts.toString());
        return appVersionCounts;
    }

    @Override
    public Boolean addAppVersionCounts(DateVO dateVO, List<Map> list) {
        try {
            userStatisticsDAO.delCustomForAppVersion(dateVO);
            List<Map> mapList = getUserInfoMaps(list);
            if (!CollectionUtils.isEmpty(mapList)) {
                List<List<Map>> partitionList = Lists.partition(mapList, Constants.TWO_THOUSAND);
                partitionList.forEach(item -> userStatisticsDAO.addCustomForAppVersion(item));
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 用户登录成功率分析
     * 沉淀表：vm_statistics_custom_success_rate
     *
     * @param dateVO
     * @return
     */
    @Override
    public List<Map> getSuccessRateCounts(DateVO dateVO) {

        Criteria timeCriteria;
        if (!StringUtils.isEmpty(dateVO.getDay())) {
            timeCriteria = getTimeCriteria();
        } else {
            timeCriteria = getMonthCriteria(Integer.parseInt(dateVO.getYear()), Integer.parseInt(dateVO.getMonth()));
        }
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.project(Constants.LOGIN_COUNT)
                .and(Constants.STATUS).as(Constants.STATUS)
                .and(Constants.USER_NAME).as(Constants.USER_NAME)
                .and(DateOperators.DateToString.dateOf(Constants.LOGIN_TIME).toString(Constants.FORMAT_DATE).withTimezone(DateOperators.Timezone.valueOf("+08"))).as(Constants.LOGIN_TIME)
                .andExclude(Constants._ID));
        operations.add(Aggregation.group(Constants.STATUS, Constants.USER_NAME, Constants.LOGIN_TIME)
                .count().as(Constants.LOGIN_COUNT)
                .first(Constants.STATUS).as(Constants.STATUS)
                .first(Constants.USER_NAME).as(Constants.USER_NAME)
                .first(Constants.LOGIN_TIME).as(Constants.LOGIN_TIME));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Map> successRateCounts = mongoTemplate.aggregate(aggregation, Constants.LOG_LOGININFO, Map.class).getMappedResults();
        log.info(successRateCounts.toString());
        return successRateCounts;
    }

    @Override
    public Boolean addSuccessRateCounts(DateVO dateVO, List<Map> list) {
        try {
            userStatisticsDAO.delCustomForSuccessRate(dateVO);
            List<Map> mapList = getUserInfoMaps(list);
            if (!CollectionUtils.isEmpty(mapList)) {
                List<List<Map>> partitionList = Lists.partition(mapList, Constants.TWO_THOUSAND);
                partitionList.forEach(item -> userStatisticsDAO.addCustomForSuccessRate(item));
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 用户登录失败率分析
     * 沉淀表：vm_statistics_custom_fail_cause
     *
     * @param dateVO
     * @return
     */
    @Override
    public List<Map> getFailCauseCounts(DateVO dateVO) {
        List<AggregationOperation> operations = new ArrayList<>();
        Criteria timeCriteria;
        if (!StringUtils.isEmpty(dateVO.getDay())) {
            timeCriteria = getTimeCriteria();
        } else {
            timeCriteria = getMonthCriteria(Integer.parseInt(dateVO.getYear()), Integer.parseInt(dateVO.getMonth()));
        }
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.match(Criteria.where(Constants.STATUS).is("1")));
        operations.add(Aggregation.project(Constants.LOGIN_COUNT)
                .and("msg").as(Constants.LOGIN_CAUSE)
                .and(Constants.USER_NAME).as(Constants.USER_NAME)
                .and(DateOperators.DateToString.dateOf(Constants.LOGIN_TIME).toString(Constants.FORMAT_DATE).withTimezone(DateOperators.Timezone.valueOf("+08"))).as(Constants.LOGIN_TIME)
                .andExclude(Constants._ID));
        operations.add(Aggregation.group(Constants.LOGIN_CAUSE, Constants.USER_NAME, Constants.LOGIN_TIME)
                .count().as(Constants.LOGIN_COUNT)
                .first(Constants.LOGIN_CAUSE).as(Constants.LOGIN_CAUSE)
                .first(Constants.USER_NAME).as(Constants.USER_NAME)
                .first(Constants.LOGIN_TIME).as(Constants.LOGIN_TIME));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Map> failCauseCounts = mongoTemplate.aggregate(aggregation, Constants.LOG_LOGININFO, Map.class).getMappedResults();
        log.info(failCauseCounts.toString());
        return failCauseCounts;
    }

    @Override
    public Boolean addFailCauseCounts(DateVO dateVO, List<Map> list) {
        try {
            userStatisticsDAO.delCustomForFailCause(dateVO);
            List<Map> mapList = getUserInfoMaps(list);
            if (!CollectionUtils.isEmpty(mapList)) {
                List<List<Map>> partitionList = Lists.partition(mapList, Constants.TWO_THOUSAND);
                partitionList.forEach(item -> userStatisticsDAO.addCustomForFailCause(item));
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * PC操作系统
     * 沉淀表：vm_statistics_custom_pc_system
     *
     * @param dateVO
     * @return
     */
    @Override
    public List<Map> getPcSystemCounts(DateVO dateVO) {
        List<AggregationOperation> operations = new ArrayList<>();
        Criteria timeCriteria;
        if (!StringUtils.isEmpty(dateVO.getDay())) {
            timeCriteria = getTimeCriteria();
        } else {
            timeCriteria = getMonthCriteria(Integer.parseInt(dateVO.getYear()), Integer.parseInt(dateVO.getMonth()));
        }
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.match(Criteria.where(Constants.CLIENT_TYPE).is("web_manage")));
        operations.add(Aggregation.project(Constants.LOGIN_COUNT)
                .and("os").as(Constants.PC_SYSTEM_NAME)
                .and(Constants.USER_NAME).as(Constants.USER_NAME)
                .and(DateOperators.DateToString.dateOf(Constants.LOGIN_TIME).toString(Constants.FORMAT_DATE).withTimezone(DateOperators.Timezone.valueOf("+08"))).as(Constants.LOGIN_TIME)
                .andExclude(Constants._ID));
        operations.add(Aggregation.group(Constants.PC_SYSTEM_NAME, Constants.USER_NAME, Constants.LOGIN_TIME)
                .count().as(Constants.LOGIN_COUNT)
                .first(Constants.PC_SYSTEM_NAME).as(Constants.PC_SYSTEM_NAME)
                .first(Constants.USER_NAME).as(Constants.USER_NAME)
                .first(Constants.LOGIN_TIME).as(Constants.LOGIN_TIME));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Map> pcCounts = mongoTemplate.aggregate(aggregation, Constants.LOG_LOGININFO, Map.class).getMappedResults();
        log.info(pcCounts.toString());
        return pcCounts;
    }

    @Override
    public Boolean addPcSystemCounts(DateVO dateVO, List<Map> list) {
        try {
            userStatisticsDAO.delCustomForPcSystem(dateVO);
            List<Map> mapList = getUserInfoMaps(list);
            if (!CollectionUtils.isEmpty(mapList)) {
                List<List<Map>> partitionList = Lists.partition(mapList, Constants.TWO_THOUSAND);
                partitionList.forEach(item -> userStatisticsDAO.addCustomForPcSystem(item));
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * APP操作系统
     * 沉淀表：vm_statistics_custom_app_system
     *
     * @param dateVO
     * @return
     */
    @Override
    public List<Map> getAppSystemCounts(DateVO dateVO) {
        List<AggregationOperation> operations = new ArrayList<>();
        Criteria timeCriteria;
        if (!StringUtils.isEmpty(dateVO.getDay())) {
            timeCriteria = getTimeCriteria();
        } else {
            timeCriteria = getMonthCriteria(Integer.parseInt(dateVO.getYear()), Integer.parseInt(dateVO.getMonth()));
        }
        operations.add(Aggregation.match(timeCriteria));
        operations.add(Aggregation.match(Criteria.where(Constants.CLIENT_TYPE).is(Constants.APP_MANAGE)));
        operations.add(Aggregation.project(Constants.LOGIN_COUNT)
                .and("os").as(Constants.APP_SYSTEM_NAME)
                .and(Constants.USER_NAME).as(Constants.USER_NAME)
                .and(DateOperators.DateToString.dateOf(Constants.LOGIN_TIME).toString(Constants.FORMAT_DATE).withTimezone(DateOperators.Timezone.valueOf("+08"))).as(Constants.LOGIN_TIME)
                .andExclude("_id"));
        operations.add(Aggregation.group(Constants.APP_SYSTEM_NAME, Constants.USER_NAME, Constants.LOGIN_TIME)
                .count().as(Constants.LOGIN_COUNT)
                .first(Constants.APP_SYSTEM_NAME).as(Constants.APP_SYSTEM_NAME)
                .first(Constants.USER_NAME).as(Constants.USER_NAME)
                .first(Constants.LOGIN_TIME).as(Constants.LOGIN_TIME));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Map> appCounts = mongoTemplate.aggregate(aggregation, Constants.LOG_LOGININFO, Map.class).getMappedResults();
        log.info(appCounts.toString());
        return appCounts;
    }

    @Override
    public Boolean addAppSystemCounts(DateVO dateVO, List<Map> list) {
        try {
            userStatisticsDAO.delCustomForAppSystem(dateVO);
            List<Map> mapList = getUserInfoMaps(list);
            if (!CollectionUtils.isEmpty(mapList)) {
                List<List<Map>> partitionList = Lists.partition(mapList, Constants.TWO_THOUSAND);
                partitionList.forEach(item -> userStatisticsDAO.addCustomForAppSystem(item));
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 获取当前时间，拼接筛选条件
     *
     * @return
     */
    private Criteria getTimeCriteria() {
        Calendar calc = Calendar.getInstance();
        calc.set(calc.get(Calendar.YEAR), calc.get(Calendar.MONTH), calc.get(Calendar.DATE), 0, 0, 0);
        Date startDate = calc.getTime();
        calc.set(calc.get(Calendar.YEAR), calc.get(Calendar.MONTH), calc.get(Calendar.DATE), 23, 59, 59);
        Date endDate = calc.getTime();
        return Criteria.where(Constants.LOGIN_TIME).gte(startDate).lte(endDate);
    }

    /**
     * 获取当前月份的第一天和最后一天
     *
     * @return
     */
    private Criteria getMonthCriteria(int year, int month) {
        Calendar calc = Calendar.getInstance();
        calc.set(year, month - 1, 1, 0, 0, 0);
        Date startDate = calc.getTime();
        calc.set(year, month, 1, 0, 0, 0);
        calc.add(Calendar.SECOND, -1);
        Date endDate = calc.getTime();
        return Criteria.where(Constants.LOGIN_TIME).gte(startDate).lte(endDate);
    }

    /**
     * 根据用户名称获取用户信息
     *
     * @param list
     * @return
     */
    private List<Map> getUserInfoMaps(List<Map> list) {
        if (!CollectionUtils.isEmpty(list)) {
            List<String> names = list.stream().map(map -> (String) map.get(Constants.USER_NAME)).collect(Collectors.toList());
            List<Map<String, String>> userInfo = userStatisticsDAO.getUserInfos(names);
            //过滤掉租户ID为空的用户信息
            Map<String, Map<String, String>> userInfoVOMap = userInfo.stream().filter(temp -> !StringUtils.isEmpty(MapUtils.getString(temp, Constants.PARAM_TENANTID))).collect(Collectors.toMap(m -> MapUtils.getString(m, Constants.USER_NAME), m -> m));
            if (!CollectionUtils.isEmpty(userInfo)) {
                list = list.stream().filter(m -> userInfoVOMap.containsKey(MapUtils.getString(m, Constants.USER_NAME))).collect(Collectors.toList());
                list.forEach(m -> {
                    if (!StringUtils.isEmpty(MapUtils.getString(m, Constants.USER_NAME))) {
                        m.putAll(userInfoVOMap.get(MapUtils.getString(m, Constants.USER_NAME)));
                    }
                });
            }
        }
        return list;
    }

    /**
     * 用户登录时长统计
     * @return
     */
    @Override
    public boolean statisticUserLoginDurationByRedis() {
        try {
            long s1 = System.currentTimeMillis();
            //key 用户id value 各端当前在线时长（key 登录端 value 在线时长）
            Map<String, Map<ClientType, Long>> userLoginDuration = scanDurationFromRedis();
            log.info("统计用户登录时长任务->计算登录时长结果userLoginDuration:{}", JSON.toJSONString(userLoginDuration));
            long s2 = System.currentTimeMillis();
            log.info("统计用户登录时长任务->从redis获取用户在线时长耗时{}ms", (s2 - s1));
            Set<String> userIds = userLoginDuration.keySet();
            if (org.springframework.util.CollectionUtils.isEmpty(userIds)) {
                log.info("统计用户登录时长任务->无人在线,统计时间{}", LocalDateTime.now());
                return true;
            }
            List<String> userIdList = Lists.newArrayList(userIds);
            int userSize = userIdList.size();
            log.info("统计用户登录时长任务->在线人数：{},统计时间{}", userSize, LocalDateTime.now());
            String loginTime = LocalDate.now().toString();
            //分批处理
            int batchSize = 1000;
            int userBatch = (int) Math.ceil(userSize * 1.0 / batchSize);
            for (int i = 0; i < userBatch; i++) {
                int indexStart = batchSize * i;
                int indexEnd = i == userBatch - 1 ? userSize : batchSize * (i + 1);
                List<String> ids = userIdList.subList(indexStart, indexEnd);
                Set<String> idsForSelect = Sets.newHashSet(ids);
                List<StatisticsUserOnlineTime> userList = userStatisticsDAO.selectUserLoginInfo(idsForSelect);
                List<StatisticsUserOnlineTime> historyList = userStatisticsDAO.selectUserOnlineTime(idsForSelect, loginTime);
                HashMap<String, StatisticsUserOnlineTime> historyMap = historyList.stream().collect(Collectors.toMap(StatisticsUserOnlineTime::getUserId, x -> x, (r1, r2) -> r2, HashMap::new));
                List<StatisticsUserOnlineTime> addList = new ArrayList<>();
                List<StatisticsUserOnlineTime> updateList = new ArrayList<>();
                for (StatisticsUserOnlineTime statisticsUserOnlineTime : userList) {
                    String userId = statisticsUserOnlineTime.getUserId();
                    if (historyMap.containsKey(userId)) {
                        statisticsUserOnlineTime.updateFromHistory(historyMap.get(userId));
                    }
                    statisticsUserOnlineTime.addDuration(userLoginDuration.get(userId));
                    statisticsUserOnlineTime.setLoginTime(loginTime);

                    if (statisticsUserOnlineTime.addOrNot()) {
                        addList.add(statisticsUserOnlineTime);
                    } else {
                        updateList.add(statisticsUserOnlineTime);
                    }
                }

                if (addList.size() > 0) {
                    userStatisticsDAO.addUserOnlineTime(addList);
                }

                if (updateList.size() > 0) {
                    userStatisticsDAO.updateUserOnlineTime(updateList);
                }
            }
            return true;
        } catch (Exception e) {
            log.error("统计用户登录时长任务->执行任务失败:{}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * 统计用户登录时长任务-计算登录时长
     * @return
     */
    private Map<String, Map<ClientType, Long>> scanDurationFromRedis() {
        Map<String, Map<ClientType, Long>> result = new HashMap<>();
        int nowMaxTtl = LocalTime.now().toSecondOfDay();
        RedisSerializer<?> keySerializer = redisTemplate.getKeySerializer();
        AuthConfig authConfig = new AuthConfig();
        try {
            Result<AuthConfig> configResult = wxFeignClient.getConfig();
            if (configResult.getCode() != ResultCode.SUCCESS.getCode()) {
                log.error("统计用户登录时长任务->从auth服务获取配置信息失败");
            } else {
                authConfig = configResult.getData();
            }
        } catch (Exception e) {
            log.error("统计用户登录时长任务->从auth服务获取配置信息失败", e);
        }
        int app = authConfig.getAppTokenValiditySeconds();
        int web = authConfig.getWebTokenValiditySeconds();
        log.info("统计用户登录时长任务->统计配置：webTokenValiditySeconds={}，appTokenValiditySeconds={},redisScanSize={}", web, app, redisScanSize);
        Map<ClientType, Integer> clientTokenExpires = new EnumMap<>(ClientType.class);
        //clientTokenExpires.put(ClientType.WEB_MANAGE, web);
        clientTokenExpires.put(ClientType.APP_MANAGE, app);
        clientTokenExpires.put(ClientType.WX_MANAGE, app);
        ClientType[] values = ClientType.values();
        for (ClientType value : values) {
            if (!ClientType.WEB_MANAGE.getClientType().equals(value.getClientType())){
                //默认web token有效期
                Long tokenExpires = clientTokenExpires.getOrDefault(value, web).longValue();
                String prefix = CacheConstants.USER_INDUSTRY_CODE + value.getClientType() + ":";
                int prefixIndex = prefix.length();
                ScanOptions scanOptions = ScanOptions.scanOptions().match(prefix + "*").count(redisScanSize).build();
                redisTemplate.execute((RedisConnection connection) -> {
                    try (Cursor<byte[]> cursor = connection.scan(scanOptions)) {
                        while (cursor.hasNext()) {
                            byte[] next = cursor.next();
                            try {
                                Object key = keySerializer.deserialize(next);
                                Long ttlSeconds = connection.ttl(next);
                                if (Objects.nonNull(key) && Objects.nonNull(ttlSeconds)) {
                                    String userId = key.toString().substring(prefixIndex);
                                    //
                                    long durationSeconds = Math.min(nowMaxTtl, Math.max(0, tokenExpires - ttlSeconds));
                                    Map<ClientType, Long> clientDuration = result.getOrDefault(userId, new EnumMap<>(ClientType.class));
                                    clientDuration.put(value, durationSeconds);
                                    result.put(userId, clientDuration);
                                }
                            } catch (Exception e) {
                                log.error("统计用户登录时长任务->计算登录时长失败:{}", e.getMessage(), e);
                            }
                        }
                    } catch (Exception e) {
                        log.error("统计用户登录时长任务->redis scan error:{}", e.getMessage(), e);
                    }
                    return null;
                });
            }
        }
        return result;
    }


}
