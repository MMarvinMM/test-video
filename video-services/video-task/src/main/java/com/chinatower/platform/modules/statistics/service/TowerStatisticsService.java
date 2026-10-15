package com.chinatower.platform.modules.statistics.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.modules.alarm.service.InterceptService;
import com.chinatower.platform.modules.statistics.dao.StatisticsDAO;
import com.chinatower.platform.modules.statistics.dao.TowerStatisticsDAO;
import com.chinatower.platform.modules.statistics.entity.DurationVO;
import com.chinatower.platform.utils.DataUtil;
import com.chinatower.common.core.utils.TableUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.google.common.collect.Lists;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @className: TowerStatisticsService
 * @description: 铁塔视角告警统计
 * @author: lyy
 * @date: 2022/6/20
 * @requestID 需求单号及需求名称
 */
@Component
@Slf4j
public class TowerStatisticsService {
    @Resource
    private TowerStatisticsDAO towerStatisticsDAO;

    @Autowired
    private TowerStatisticsService self;
    @Resource
    private StatisticsDAO statisticsDAO;

    @Autowired
    private InterceptService interceptService;

    @Autowired
    private SystemFeignClient systemFeignClient;

    /**
     * 设备通道数量统计表-铁塔视角
     * @param param
     * @return
     */
    @XxlJob("deviceStatisticForTowerHandler")
    public ReturnT<String> deviceStatisticHandler(String param) {
        log.info("设备通道数量统计表-铁塔视角");
        XxlJobLogger.log("设备通道数量统计表-铁塔视角");
        DateVO dateVO = DataUtil.setDateVO();
        List<Map> list = getDeviceStatistic();
        if(!CollectionUtils.isEmpty(list)){
            return DataUtil.returnString(self.insertDeviceStatistic(dateVO,list));
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getDeviceStatistic(){
        List<Map> chanList = towerStatisticsDAO.getChannelStatistic();
        List<Map> devList = towerStatisticsDAO.getDevStatistic();
        List<Map> resultMap = new ArrayList<>();
        resultMap.addAll(chanList);
        resultMap.addAll(devList);
//        List<Map> reslutList = chanList.stream().map(item -> {
//            devList.stream().filter(dev -> Objects.equals((String)item.get(PROVINCE_ID)+item.get(CITY_ID)+item.get(COUNTY_ID),(String)dev.get(PROVINCE_ID)+dev.get(CITY_ID)+dev.get(COUNTY_ID))
//                    )
//                    .forEach(dev-> {
//                        item.put(DEV_ONLINE_NUM,dev.get(DEV_ONLINE_NUM));
//                        item.put(DEV_OFFLINE_NUM,dev.get(DEV_OFFLINE_NUM));
//                        item.put(DEVICE_NUM, dev.get(DEVICE_NUM));
//                    });
//            return item;
//        }).collect(Collectors.toList());
//        resultMap.addAll(reslutList);
//        Map itemTemp = new HashMap();
//        for(Map item:reslutList){
//            String key = (String) item.get(PROVINCE_ID)+item.get(CITY_ID)+item.get(COUNTY_ID);
//            itemTemp.put(key,item);
//        }
//        devList.stream().forEach(item->{
//            String key = (String) item.get(PROVINCE_ID)+item.get(CITY_ID)+item.get(COUNTY_ID);
//            Map devMap = MapUtils.getMap(itemTemp,key);
//            if(devMap!=null){
//                resultMap.add(item);
//            }
//        });
        return resultMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertDeviceStatistic(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.deleteDeviceStatistic(dateVO);
            towerStatisticsDAO.insertDeviceStatistic(list);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 工作台事件数量统计表-铁塔视角
     */
    @XxlJob("eventNumForTowerHandler")
    public ReturnT<String> eventNumForTowerHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                // 检查日期格式是否正确（长度大于等于7，表示至少为 yyyy-MM 格式）
                if (dates[i] != null && dates[i].length()>=7) {
                    // 提取年份和月份
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("更新工作台事件数量统计表-租户用-"+dates[i]);
                    XxlJobLogger.log("更新工作台事件数量统计表-租户用-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    //将两个list中，相同租户、区县、设备的数据数量相加
                    List<DurationVO> durationRes = getEventNumForTowers(dateVO);
                    if (!CollectionUtils.isEmpty(durationRes)) {
                        DataUtil.returnString(self.insertEventNumByTowers(dateVO, durationRes));
                    }
                }
            }
        } else{
            log.info("工作台事件数量统计表-铁塔视角 getEventNumForTower");
            //根据当前时间，获取告警事件分月表名
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            dateVO.setStartDate(DateUtil.format(DateUtil.beginOfDay(DateUtil.yesterday()),Constants.FORMAT_TIME));
            dateVO.setEndDate(DateUtil.format(DateUtil.date(),Constants.FORMAT_TIME));
            //将两个list中，相同租户、区县、设备的数据数量相加
            List<DurationVO> durationRes = getEventNumForTower(dateVO);
            if (!CollectionUtils.isEmpty(durationRes)) {
                return DataUtil.returnString(self.insertEventNumByTower(dateVO,durationRes));
            }
        }

        return ReturnT.SUCCESS;
    }

    /**
     * 获取告警事件的全量统计数据。
     *
     * 对指定日期的塔类告警事件进行统计，包括当日数据和历史数据。
     *
     * @param dateVO 日期对象，包含统计条件（如租户、区县等）。
     * @return 合并后的统计结果列表。
     */
    private List<DurationVO> getEventNumForTower(DateVO dateVO) {
        String dateEnd = DateUtils.dateTimeNow(TableUtil.getMinTableDate()); //日期转化为字符串，只有年月
        //对全量统计字段进行查询、累加、更新操作
        String dateStr = DateUtils.dateTimeNow(DateUtils.DATE_TIME_YM_FORMAT); //日期转化为字符串，只有年月
        List<DurationVO> resList = new ArrayList<>();
        //今日告警数量统计
        List<DurationVO> todayNumList = towerStatisticsDAO.getEventTodayNumTower(dateVO);
        log.info("todayNumList = {}", JSON.toJSONString(todayNumList));
        resList.addAll(todayNumList);

        boolean dateFlag = true;
        while(dateFlag) {
            // 根据当前年月动态生成表名
            dateVO.setTableName(TableUtil.getTableName(DateUtils.dateTime(DateUtils.DATE_TIME_YM_FORMAT,dateStr),Constants.WARN_ORDER_TEBLE));
            // 查询当前月份的统计数据
            List<DurationVO> eventNumList = towerStatisticsDAO.getEventNumTower(dateVO);
            resList.addAll(eventNumList);
            // 判断是否到达最早的统计月份
            if (dateEnd.equals(dateStr)) {
                dateFlag = false;
            } else {
                // 获取上一个月的年月字符串
                dateStr = DateUtils.getlastMonth(dateStr);
            }
        }

        //将两个list中，相同租户、区县、设备的数据数量相加
        List<DurationVO> durationRes = new ArrayList<>();
        resList.stream().collect(Collectors.groupingBy(item ->
                // 分组依据：租户ID + 省市区县 + 镇ID
                (item.getTenantId() + item.getProvinceId() + item.getCityId() + item.getCountyId() +item.getTownId()), Collectors.toList())).forEach(
                (id,transfer) -> {transfer.stream().reduce((a,b) ->
                        new DurationVO(a.getHandleDateTime(),a.getOvertime()+b.getOvertime(),a.getTenantId(),a.getProvinceId(),a.getCityId(),
                                a.getCountyId(),a.getTownId(),a.getYesterdayAll()+b.getYesterdayAll(),
                                a.getTodayAll()+b.getTodayAll(),
                                a.getFalseAll()+b.getFalseAll(),
                                a.getTodayProcess()+b.getTodayProcess(),
                                a.getTodayComplete()+b.getTodayComplete(),
                                a.getPenddingReceive()+b.getPenddingReceive(),
                                a.getPenddingJudged()+b.getPenddingJudged(),
                                a.getPendingDispatched()+b.getPendingDispatched(),
                                a.getPeddingDisposal()+b.getPeddingDisposal(),
                                a.getPendingApprove()+b.getPendingApprove()
                        )).ifPresent(durationRes::add);
                });
        return durationRes;
    }

    /**
     * 获取告警事件的当日统计数据。
     *
     * @param dateVO 日期对象，包含统计条件（如租户、区县等）。
     * @return 当日统计结果列表。
     */
    private List<DurationVO> getEventNumForTowers(DateVO dateVO) {
        List<DurationVO> resList = new ArrayList<>();
        // 查询当日的告警数量统计数据
        List<DurationVO> todayNumList = towerStatisticsDAO.getEventTodayNumTowers(dateVO);
        resList.addAll(todayNumList);
        return resList;
    }

    /**
     * 插入塔类告警事件的全量统计数据。
     *
     * @param dateVO 日期对象，包含统计条件（如租户、区县等）。
     * @param list   统计结果列表。
     * @return 插入是否成功。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumByTower(DateVO dateVO,List<DurationVO> list){
        try {
            towerStatisticsDAO.deleteEventNum(dateVO);
            // 将数据按 5000 条分批插入
            List<List<DurationVO>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    towerStatisticsDAO.insertEventNum(item)
            );
        }catch (Exception e){
            // 捕获异常并回滚事务
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 插入塔类告警事件的当日统计数据。
     *
     * @param dateVO 日期对象，包含统计条件（如租户、区县等）。
     * @param list   统计结果列表。
     * @return 插入是否成功。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumByTowers(DateVO dateVO,List<DurationVO> list){
        try {
            // 删除已有的当日统计数据
            towerStatisticsDAO.deleteEventNums(dateVO);
            // 将数据按 5000 条分批插入
            List<List<DurationVO>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    towerStatisticsDAO.insertEventNums(item)
            );
        }catch (Exception e){
            // 捕获异常并回滚事务
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }
    /**
     * 告警发生区域统计表-铁塔视角
     * @param param 例："2022-09,2022-08-01,2022-07,2022-06" （yyyy-MM/yyyy-MM-dd）
     *              多个日期逗号间隔，支持执行多个月份、多天，或者一天、一月，也可以不传，不传就代表实时执行
     * @return
     */
    @XxlJob("eventAreaForTowerHandler")
    public ReturnT<String> eventAreaHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                // 检查日期格式是否正确（长度大于等于7，表示至少为 yyyy-MM 格式）
                if (dates[i] != null && dates[i].length()>=7) {
                    // 提取年份和月份
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警发生区域统计表-铁塔视角-"+dates[i]);
                    XxlJobLogger.log("告警发生区域统计表-铁塔视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventArea(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertEventAreaParam(dateVO, list));
                    }
                }
            }
        } else {
            log.info("告警发生区域统计表-铁塔视角");
            XxlJobLogger.log("告警发生区域统计表-铁塔视角");
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventArea(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventArea(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventArea(DateVO dateVO){
        return towerStatisticsDAO.getEventArea(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventArea(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.deleteEventArea(dateVO);
            towerStatisticsDAO.insertEventArea(list);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventAreaParam(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.deleteEventArea(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    towerStatisticsDAO.insertEventAreaParam(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 办结事件处理时长统计-铁塔视角
     * @param param 例："2022-09,2022-08-01,2022-07,2022-06" （yyyy-MM/yyyy-MM-dd）
     *              多个日期逗号间隔，支持执行多个月份、多天，或者一天、一月，也可以不传，不传就代表实时执行
     * @return
     */
    @XxlJob("eventDurationForTowerHandler")
    public ReturnT<String> eventDurationHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                // 检查日期格式是否正确（长度大于等于7，表示至少为 yyyy-MM 格式）
                if (dates[i] != null && dates[i].length()>=7) {
                    // 提取年份和月份
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("办结事件处理时长统计-铁塔视角-"+dates[i]);
                    XxlJobLogger.log("办结事件处理时长统计-铁塔视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);

                    //将两个list中，相同租户、区县、设备的数据数量相加
                    List<DurationVO> durationRes = getEventDurationTower(dateVO);

                    if (!CollectionUtils.isEmpty(durationRes)) {
                        DataUtil.returnString(self.addEventDurationTowerParam(durationRes, dateVO));
                    }
                }
            }
        } else {
            log.info("办结事件处理时长统计-铁塔视角");
            XxlJobLogger.log("办结事件处理时长统计-铁塔视角");
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<DurationVO> list = getEventDurationTower(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.addEventDurationTower(list, dateVO));
            }
        }
        return ReturnT.SUCCESS;
    }



    private List<DurationVO> getEventDurationTower(DateVO dateVO){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        String lastMonth = String.valueOf(year)+String.valueOf(month);
        List<DurationVO> durationList = new ArrayList<>();
        // 循环近六个月的表名，查询近六个月表中今日完结事件数量
        for (int i = 0;i<5;i++) {
            List<DurationVO> voList = new ArrayList<>();
            dateVO.setTableName(TableUtil.getTableName(DateUtils.dateTime(DateUtils.DATE_TIME_YM_FORMAT, lastMonth),
                    Constants.WARN_ORDER_TEBLE));
            voList = towerStatisticsDAO.getEventDurationTower(dateVO);
            durationList.addAll(voList);
            lastMonth = DateUtils.getlastMonth(lastMonth);
        }


        // 将两个list中，相同租户、区县、设备的数据数量相加
        List<DurationVO> durationRes = new ArrayList<>();
        durationList.stream()
                .collect(Collectors.groupingBy(
                        item -> (item.getProvinceId() + item.getCityId() + item.getCountyId() + item.getDeviceCode() + item.getWarningSource() + item.getIndustryCode() + item.getYearStr()+item.getMonthStr()+item.getDayStr()+item.getAlarmTime()),
                        Collectors.toList()))
                .forEach((id, transfer) -> {
                    transfer.stream()
                            .reduce((a, b) -> new DurationVO(a.getTenantId(), a.getProvinceId(), a.getCityId(), a.getCountyId(), a.getDeviceCode(), a.getWarningSource(),
                                    a.getLessOne() + b.getLessOne(), a.getOneFive() + b.getOneFive(),
                                    a.getFiveTen() + b.getFiveTen(), a.getMoreTen() + b.getMoreTen(), a.getIndustryCode(), a.getModifierDate(),a.getAlarmTime(),a.getYearStr(),a.getMonthStr(),a.getDayStr()))
                            .ifPresent(durationRes::add);
                });
        return durationList;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventDurationTower(List<DurationVO> list, DateVO dateVO){
        try {
            towerStatisticsDAO.delEventDurationTower(dateVO);
            towerStatisticsDAO.addEventDurationTower(list);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean addEventDurationTowerParam(List<DurationVO> list, DateVO dateVO){
        try {
            towerStatisticsDAO.delEventDurationTower(dateVO);
            towerStatisticsDAO.addEventDurationTowerParam(list);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 告警区域处理时长维度统计-铁塔视角
     */
    @XxlJob("eventAreaHandleForTowerHandler")
    public ReturnT<String> eventAreaHandleHandler(String param) throws ParseException {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                // 检查日期格式是否正确（长度大于等于7，表示至少为 yyyy-MM 格式）
                if (dates[i] != null && dates[i].length()>=7) {
                    // 提取年份和月份
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警区域处理时长维度统计-铁塔视角-"+dates[i]);
                    XxlJobLogger.log("告警区域处理时长维度统计-铁塔视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<DurationVO> list = getEventAreaHandle(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertEventAreaHandle(dateVO, list));
                    }
                }
            }
        } else {
            log.info("告警区域处理时长维度统计-铁塔视角");
            XxlJobLogger.log("告警区域处理时长维度统计-铁塔视角");
            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH) + 1;
            int day = now.get(Calendar.DAY_OF_MONTH);
            DateVO dateVO = new DateVO();
            dateVO.setYear(String.valueOf(year));
            dateVO.setMonth(String.valueOf(month));
            dateVO.setDay(String.valueOf(day));
            dateVO.setHandleDate(DateUtils.getDate());
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));

            List<DurationVO> list = getEventAreaHandle(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventAreaHandle(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<DurationVO> getEventAreaHandle(DateVO dateVO){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        String lastMonth = String.valueOf(year)+String.valueOf(month);
        List<DurationVO> durationList = new ArrayList<>();
        // 循环近六个月的表名，查询近六个月表中今日完结事件数量
        for (int i = 0;i<5;i++) {
            List<DurationVO> voList = new ArrayList<>();
            dateVO.setTableName(TableUtil.getTableName(DateUtils.dateTime(DateUtils.DATE_TIME_YM_FORMAT, lastMonth),
                    Constants.WARN_ORDER_TEBLE));
            voList = towerStatisticsDAO.getEventAreaHandle(dateVO);
            durationList.addAll(voList);
            lastMonth = DateUtils.getlastMonth(lastMonth);
        }


        // 将两个list中，相同租户、区县、设备的数据数量相加
        List<DurationVO> durationRes = new ArrayList<>();
        durationList.stream()
                .collect(Collectors.groupingBy(
                        item -> (item.getProvinceCode() + item.getCityCode() + item.getCountyCode() +  item.getYearStr()+item.getMonthStr()+item.getDayStr()+item.getAlarmTime()),
                        Collectors.toList()))
                .forEach((id, transfer) -> {
                    transfer.stream()
                            .reduce((a, b) -> new DurationVO(a.getTenantId(), a.getProvinceCode(), a.getCityCode(), a.getCountyCode(),
                                    a.getModifierDate(),a.getAlarmTime(),a.getYearStr(),a.getMonthStr(),a.getDayStr(),a.getRegionName(),
                                    a.getHandleDuration()+b.getHandleDuration(),
                                    a.getDurationNum()+b.getDurationNum()))
                            .ifPresent(durationRes::add);
                });
        return durationList;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventAreaHandle(DateVO dateVO,List<DurationVO> list){
        try {
            towerStatisticsDAO.deleteEventAreaHandle(dateVO);
            towerStatisticsDAO.insertEventAreaHandle(list);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 按照小时沉淀数据-铁塔视角
     * @param param
     * @return
     */
    @XxlJob("eventByHourForTowerHandler")
        public ReturnT<String> eventByHourHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                // 检查日期格式是否正确（长度大于等于7，表示至少为 yyyy-MM 格式）
                if (dates[i] != null && dates[i].length()>=7) {
                    // 提取年份和月份
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info(Constants.EVENT_DAY_TOWER+dates[i]);
                    XxlJobLogger.log(Constants.EVENT_DAY_TOWER+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventByHourNew(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                         DataUtil.returnString(self.insertEventByHour(dateVO, list));
                    }
                }
            }
        }else{
            log.info(Constants.EVENT_DAY_TOWER);
            XxlJobLogger.log(Constants.EVENT_DAY_TOWER);
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventByHour(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventByHour(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventByHour(DateVO dateVO){
        return towerStatisticsDAO.getEventByHour(dateVO);
    }

    private List<Map> getEventByHourNew(DateVO dateVO){
        return towerStatisticsDAO.getEventByHourNew(dateVO);
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventByHour(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.deleteEventByHour(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    towerStatisticsDAO.insertEventByHour(item)
            );

        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 按照小时沉淀数据-铁塔视角
     * @param param
     * @return
     */
    @XxlJob("eventByHourForTowerHandlerNew")
    public ReturnT<String> eventByHourHandlerNews(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                // 检查日期格式是否正确（长度大于等于7，表示至少为 yyyy-MM 格式）
                if (dates[i] != null && dates[i].length()>=7) {
                    // 提取年份和月份
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info(Constants.EVENT_DAY_TOWER+dates[i]);
                    XxlJobLogger.log(Constants.EVENT_DAY_TOWER+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventByHourNewNews(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertEventByHourNews(dateVO, list));
                    }
                }
            }
        }else{
            log.info(Constants.EVENT_DAY_TOWER);
            XxlJobLogger.log(Constants.EVENT_DAY_TOWER);
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventByHourNews(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventByHourNews(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventByHourNews(DateVO dateVO){
        return towerStatisticsDAO.getEventByHourNews(dateVO);
    }

    private List<Map> getEventByHourNewNews(DateVO dateVO){
        return towerStatisticsDAO.getEventByHourNewNews(dateVO);
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventByHourNews(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.deleteEventByHourNews(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    towerStatisticsDAO.insertEventByHourNews(item)
            );

        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 按照小时沉淀压制数据
     */
    @XxlJob("eventByInterceptHourForTowerHandler")
    public ReturnT<String> eventByInterceptHourForTowerHandler(String param) {
        Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(com.chinatower.event.constants.Constants.TASK_SELECT_INTERCEPT_POWER_FLAG);
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                // 检查日期格式是否正确（长度大于等于7，表示至少为 yyyy-MM 格式）
                if (dates[i] != null && dates[i].length()>=7) {
                    log.info(Constants.EVENT_INTERCEPT_HOUR_TOWER+dates[i]);
                    XxlJobLogger.log(Constants.EVENT_INTERCEPT_HOUR_TOWER+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    List<Map> list = getEventNumListByInterceptHour(dateVO,suppressDictData);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertEventNumByInterceptHour(dateVO, list));
                    }
                }
            }
        }else {
            log.info(Constants.EVENT_INTERCEPT_HOUR_TOWER);
            XxlJobLogger.log(Constants.EVENT_INTERCEPT_HOUR_TOWER);
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            List<Map> list = getEventNumListByInterceptHour(dateVO,suppressDictData);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventNumByInterceptHour(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventNumListByInterceptHour(DateVO dateVO,Result<List<SysDictDataResponse>> suppressDictData){
        return interceptService.getEventNumListByInterceptHour2(dateVO,suppressDictData);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumByInterceptHour(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.deleteEventNumByInterceptHour(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    towerStatisticsDAO.insertEventNumByInterceptHour(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 按照小时沉淀压制数据
     */
    @XxlJob("eventByInterceptHourForTowerHandlerNew")
    public ReturnT<String> eventByInterceptHourForTowerHandlerNew(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                // 检查日期格式是否正确（长度大于等于7，表示至少为 yyyy-MM 格式）
                if (dates[i] != null && dates[i].length()>=7) {
                    log.info(Constants.EVENT_INTERCEPT_HOUR_TOWER+dates[i]);
                    XxlJobLogger.log(Constants.EVENT_INTERCEPT_HOUR_TOWER+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    List<Map> list = getEventNumListByInterceptHourNew(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertEventNumByInterceptHourNew(dateVO, list));
                    }
                }
            }
        }else {
            log.info(Constants.EVENT_INTERCEPT_HOUR_TOWER);
            XxlJobLogger.log(Constants.EVENT_INTERCEPT_HOUR_TOWER);
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            List<Map> list = getEventNumListByInterceptHourNew(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventNumByInterceptHourNew(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventNumListByInterceptHourNew(DateVO dateVO){
        return towerStatisticsDAO.getEventNumListByInterceptHourNew(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumByInterceptHourNew(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.deleteEventNumByInterceptHourNew(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    towerStatisticsDAO.insertEventNumByInterceptHourNew(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 告警等级统计表-铁塔视角
     */
    @XxlJob("eventLevelForTowerHandler")
    public ReturnT<String> eventLevelHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                // 检查日期格式是否正确（长度大于等于7，表示至少为 yyyy-MM 格式）
                if (dates[i] != null && dates[i].length()>=7) {
                    // 提取年份和月份
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警等级统计表-铁塔视角-"+dates[i]);
                    XxlJobLogger.log("告警等级统计表-铁塔视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventLevel(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertEventLevel(dateVO, list));
                    }
                }
            }
        } else {
            log.info("告警等级统计表-铁塔视角");
            XxlJobLogger.log("告警等级统计表-铁塔视角");
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventLevel(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventLevel(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventLevel(DateVO dateVO){
        return towerStatisticsDAO.getEventLevel(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventLevel(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.deleteEventLevel(dateVO);
            towerStatisticsDAO.insertEventLevel(list);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 告警每日维度统计-铁塔视角
     */
    @XxlJob("eventNumDayForTowerHandler")
    public ReturnT<String> eventNumDayHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                // 检查日期格式是否正确（长度大于等于7，表示至少为 yyyy-MM 格式）
                if (dates[i] != null && dates[i].length()>=7) {
                    // 提取年份和月份
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("eventNumDayForTowerHandler-"+dates[i]);
                    XxlJobLogger.log("告警每日维度统计-铁塔视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventNumDays(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertEventNumDays(dateVO, list));
                    }
                }
            }
        } else {
            log.info("告警每日维度统计-铁塔视角 getEventNumDay ");
            XxlJobLogger.log("告警每日维度统计-铁塔视角");
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            dateVO.setStartDate(DateUtil.format(DateUtil.beginOfDay(new Date()),"yyyy-MM-dd HH:mm:ss"));
            dateVO.setEndDate(DateUtil.format(DateUtil.date(),"yyyy-MM-dd HH:mm:ss"));
            List<Map> list = getEventNumDay(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventNumDay(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventNumDay(DateVO dateVO){
        List<Map> eventNumDays = towerStatisticsDAO.getEventNumDay(dateVO);
        //租户名称
        List<Map<String,String>> tenants = towerStatisticsDAO.getTenants();
        Map<String, String> tenantMap = tenants.stream().collect( Collectors.toMap( t -> t.get("tenantId"), t -> t.get(Constants.PARAM_TENANT_NAME), (o, n) -> n, HashMap::new));
        for (Map map : eventNumDays) {
            map.put(Constants.PARAM_TENANT_NAME, MapUtil.getStr(tenantMap,map.get("tenant_id")));
        }
        return eventNumDays;
    }

    private List<Map> getEventNumDays(DateVO dateVO){
        List<Map> eventNumDays = towerStatisticsDAO.getEventNumDays(dateVO);
        //租户名称
        List<Map<String,String>> tenants = towerStatisticsDAO.getTenants();
        Map<String, String> tenantMap = tenants.stream().collect( Collectors.toMap( t -> t.get("tenantId"), t -> t.get(Constants.PARAM_TENANT_NAME), (o, n) -> n, HashMap::new));
        for (Map map : eventNumDays) {
            map.put(Constants.PARAM_TENANT_NAME, MapUtil.getStr(tenantMap,map.get("tenant_id")));
        }
        return eventNumDays;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumDays(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.delEventNumDay(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    towerStatisticsDAO.insertEventNumDays(item)

            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumDay(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.delEventNumDay(dateVO);
            towerStatisticsDAO.insertEventNumDay(list);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 告警月度维度统计-铁塔视角
     */
    @XxlJob("eventNumMonthForTowerHandler")
    public ReturnT<String> eventNumMonthHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                // 检查日期格式是否正确（长度大于等于7，表示至少为 yyyy-MM 格式）
                if (dates[i] != null && dates[i].length()>=7) {
                    // 提取年份和月份
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警月度维度统计-铁塔视角-"+dates[i]);
                    XxlJobLogger.log("告警月度维度统计-铁塔视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVOS(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventNumMonth(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertEventNumMonths(dateVO, list));
                    }
                }
            }
        } else {
            log.info("告警月度维度统计-铁塔视角");
            XxlJobLogger.log("告警月度维度统计-铁塔视角");
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventNumMonth(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertEventNumMonth(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventNumMonth(DateVO dateVO){
        return towerStatisticsDAO.getEventNumMonth(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumMonth(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.delEventNumMonth(dateVO);
            towerStatisticsDAO.insertEventNumMonth(list);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventNumMonths(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.delEventNumMonth(dateVO);
            towerStatisticsDAO.insertEventNumMonths(list);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 告警年度维度统计-铁塔视角
     */
    @XxlJob("eventNumYearForTowerHandler")
    public ReturnT<String> eventNumYearHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            log.info(Constants.ALARM_YEAR_STATISTICS_TOWER_MSG);
            XxlJobLogger.log(Constants.ALARM_YEAR_STATISTICS_TOWER_MSG);
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            List<DurationVO> durationRes = getEventNumYears(param);
            if (!CollectionUtils.isEmpty(durationRes)) {
                return DataUtil.returnString(self.addEventNumYear(dateVO, durationRes));
            }
        }else{
            log.info(Constants.ALARM_YEAR_STATISTICS_TOWER_MSG);
            XxlJobLogger.log(Constants.ALARM_YEAR_STATISTICS_TOWER_MSG);
            //初始化执行时间
            DateVO dateVO = DataUtil.setDateVO();
            List<DurationVO> durationRes = getEventNumYear(dateVO);
            if (!CollectionUtils.isEmpty(durationRes)) {
                return DataUtil.returnString(self.addEventNumYear(dateVO, durationRes));
            }
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 根据传入的日期参数，统计告警年度维度数据（租户）并合并相同租户、区县、设备的数据数量。
     *
     * @param param 日期参数，多个日期以逗号分隔（格式：yyyy-MM）。
     * @return 统计结果列表，包含每个租户、区县、设备的告警数据总和。
     */
    private List<DurationVO> getEventNumYears(String param){
        try {
            List<DurationVO> yearList = new ArrayList<>();
                String[] dates = param.split(",");
                for (int i = 0; i < dates.length; i++) {
                    // 检查日期格式是否正确（长度大于等于7，表示至少为 yyyy-MM 格式）
                    if (dates[i] != null && dates[i].length()>=7) {
                        // 提取年份和月份
                        String year = dates[i].substring(0, 4);
                        String month = dates[i].substring(5, 7);
                        log.info("告警月度维度统计(租户)沉淀数据定时-" + dates[i]);
                        XxlJobLogger.log("告警月度维度统计(租户)沉淀数据定时-" + dates[i]);
                        //初始化执行时间
                        DateVO dateVO = DataUtil.setDateVOS(dates[i]);
                        dateVO.setTableName(Constants.WARN_ORDER_TEBLE + year + month);
                        // 查询当前年份的统计数据
                        List<DurationVO> eventList = towerStatisticsDAO.getEventNumYear(dateVO);
                        // 如果查询结果不为空，则将其加入 yearList
                        if (!eventList.isEmpty()){
                            yearList.addAll(eventList);
                        }
                        // 删除当前年份的统计数据
                        towerStatisticsDAO.delEventNumYear(dateVO);
                    }
                }
            //将两个list中，相同租户、区县、设备的数据数量相加
            List<DurationVO> durationRes = new ArrayList<>();
            yearList.stream().collect(Collectors.groupingBy(item ->
                    (item.getProvinceId() + item.getCityId() + item.getCountyId())+item.getHandleYear(), Collectors.toList())).forEach(
                    (id,transfer) -> {transfer.stream().reduce((a,b) ->
                            new DurationVO(a.getTodayAll()+b.getTodayAll(),a.getProvinceId(),a.getCityId(),
                                    a.getCountyId(),a.getHandleYear())).ifPresent(durationRes::add);
                    });
            return durationRes;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 根据指定日期对象，获取告警事件的全年统计数据。
     *
     * @param dateVO 日期对象，包含统计条件（如租户、区县等）。
     * @return 统计结果列表，包含每个租户、区县、设备的告警数据总和。
     */
    private List<DurationVO> getEventNumYear(DateVO dateVO){
        try {
            //根据当前时间，获取告警事件分月表名
            String dateStr = DateUtils.dateToStringYM(Constants.ym.parse(dateVO.getHandleMonth())); //日期转化为字符串，只有年月
            boolean dateFlag = true;
            List<DurationVO> yearList = new ArrayList<>();
            while (dateFlag) {
                dateVO.setTableName(Constants.WARN_ORDER_TEBLE + dateStr);
                List<DurationVO> eventList = towerStatisticsDAO.getEventNumYear(dateVO);
                yearList.addAll(eventList);
                // 判断是否到达最早的统计月份或年份的最后一个月
                if (Constants.MIN_TABLE_DATE.equals(dateStr) || Constants.STR_01.equals(dateStr.substring(4))) {
                    dateFlag = false;
                } else {
                    // 获取上一个月的日期字符串
                    dateStr = DateUtils.getlastMonth(dateStr);
                }
            }

            //将两个list中，相同租户、区县、设备的数据数量相加
            List<DurationVO> durationRes = new ArrayList<>();
            yearList.stream().collect(Collectors.groupingBy(item ->
                    (item.getProvinceId() + item.getCityId() + item.getCountyId()+item.getHandleYear()), Collectors.toList())).forEach(
                    (id,transfer) -> {transfer.stream().reduce((a,b) ->
                            new DurationVO(a.getTodayAll()+b.getTodayAll(),a.getProvinceId(),a.getCityId(),
                                    a.getCountyId(),a.getHandleYear())).ifPresent(durationRes::add);
                    });
            return durationRes;
        }catch (Exception e){
            return null;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventNumYear(DateVO dateVO,List<DurationVO> list){
        try {
            towerStatisticsDAO.delEventNumYear(dateVO);
            Map map = new HashMap<>();
            map.put(Constants.LIST,list);
            //map.put(Constants.HANDLEYEAR,dateVO.getHandleYear());
            map.put(Constants.HANDLEDATETIME,dateVO.getHandleDateTime());
            towerStatisticsDAO.addEventNumYear(map);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 告警类型维度统计-铁塔视角
     */
    @XxlJob("eventTypeForTowerHandler")
    public ReturnT<String> eventTypeHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警类型维度统计-铁塔视角-"+dates[i]);
                    XxlJobLogger.log("告警类型维度统计-铁塔视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventType(dateVO);
                    // 空结果仍 del+写，清掉历史「空 sub_scenes 回退场景」脏数据
                    DataUtil.returnString(self.insertEventType(dateVO,
                            list == null ? Collections.emptyList() : list));
                }
            }
        } else {
            log.info("告警类型维度统计-铁塔视角");
            XxlJobLogger.log("告警类型维度统计-铁塔视角");
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventType(dateVO);
            return DataUtil.returnString(self.insertEventType(dateVO,
                    list == null ? Collections.emptyList() : list));
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventType(DateVO dateVO){
        List<Map> eventType = towerStatisticsDAO.getEventType(dateVO);
        if (CollectionUtils.isEmpty(eventType)) {
            return eventType;
        }
        // 方案G：剔除纯场景；编码同时是下游 alarm_type 时保留（如 9212102）
        Set<String> sceneIds = loadSceneIdSet();
        if (!sceneIds.isEmpty()) {
            Set<String> downstreamIds = loadDownstreamAlarmTypeIdSet();
            eventType = eventType.stream()
                    .filter(map -> map != null
                            && keepWarningTypeId(String.valueOf(map.get("warning_type_id")),
                                    sceneIds, downstreamIds))
                    .collect(Collectors.toList());
        }
        // 方案G：拆行后 warning_type_id 多为下游任务编码，名称优先取 sys_scene_alarm_rel
        Map<String, String> typeMap = buildTowerWarningTypeNameMap();
        for (Map map : eventType) {
            map.put("typeName", MapUtil.getStr(typeMap, map.get("warning_type_id")));
        }
        return eventType;
    }

    private Set<String> loadSceneIdSet() {
        List<String> sceneIds = statisticsDAO.getSceneIds();
        if (CollectionUtils.isEmpty(sceneIds)) {
            return Collections.emptySet();
        }
        return sceneIds.stream()
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());
    }

    private Set<String> loadDownstreamAlarmTypeIdSet() {
        List<String> ids = statisticsDAO.getDownstreamAlarmTypeIds();
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptySet();
        }
        return ids.stream()
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());
    }

    /** 纯场景剔除；在 sys_scene_alarm_rel 中的编码一律按下游保留 */
    private boolean keepWarningTypeId(String warningTypeId, Set<String> sceneIds, Set<String> downstreamIds) {
        if (StringUtils.isEmpty(warningTypeId) || "null".equalsIgnoreCase(warningTypeId)) {
            return false;
        }
        if (downstreamIds.contains(warningTypeId)) {
            return true;
        }
        return !sceneIds.contains(warningTypeId);
    }

    /**
     * 铁塔告警类型/下游任务名称字典：先 sys_alarm_type，再用下游关系表覆盖（保证大模型展示下游任务名）。
     */
    private Map<String, String> buildTowerWarningTypeNameMap() {
        Map<String, String> typeMap = new HashMap<>();
        List<Map<String, String>> alarmTypeList = statisticsDAO.getAlarmType();
        if (!CollectionUtils.isEmpty(alarmTypeList)) {
            for (Map<String, String> t : alarmTypeList) {
                if (t == null || StringUtils.isEmpty(t.get("typeId"))) {
                    continue;
                }
                typeMap.put(t.get("typeId"), t.get("typeName"));
            }
        }
        List<Map<String, String>> subSceneNames = statisticsDAO.getSubSceneAlarmTypeNames();
        if (!CollectionUtils.isEmpty(subSceneNames)) {
            for (Map<String, String> t : subSceneNames) {
                if (t == null || StringUtils.isEmpty(t.get("typeId")) || StringUtils.isEmpty(t.get("typeName"))) {
                    continue;
                }
                // 下游任务名覆盖同编码的场景/类型名
                typeMap.put(t.get("typeId"), t.get("typeName"));
            }
        }
        return typeMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventType(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.delEventType(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    towerStatisticsDAO.addEventType(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 算法编码维度统计-铁塔视角（日更，仅昨天；保持利旧）
     */
    @XxlJob("eventAlgorithmForTowerHandler")
    public ReturnT<String> eventAlgorithmHandler(String param) {
        log.info("算法编码维度统计-铁塔视角");
        XxlJobLogger.log("算法编码维度统计-铁塔视角");
        DateVO dateVO = DataUtil.setDateVO();
        dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
        List<Map> list = getEventAlgorithm(dateVO);
        if (!CollectionUtils.isEmpty(list)) {
            return DataUtil.returnString(self.insertEventAlgorithm(list));
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 算法编码维度统计-铁塔视角（历史刷数）
     * <p>弥补 {@code eventAlgorithmForTowerHandler} 仅统计昨天、无法按入参补历史的缺口。</p>
     * <p>入参示例：{@code 2026-01,2026-02,2026-03-15}（yyyy-MM 按月 / yyyy-MM-dd 按日，逗号分隔）。</p>
     * <p>空参不执行；无数据时仍先删后写，避免脏数据残留。</p>
     */
    @XxlJob("eventAlgorithmHistoryForTowerHandler")
    public ReturnT<String> eventAlgorithmHistoryHandler(String param) {
        if (StringUtils.isEmpty(param)) {
            log.warn("算法编码维度统计-铁塔视角-历史刷数：入参为空，跳过（请传 yyyy-MM 或 yyyy-MM-dd）");
            XxlJobLogger.log("算法编码维度统计-铁塔视角-历史刷数：入参为空，跳过");
            return ReturnT.SUCCESS;
        }
        String[] dates = param.split(",");
        for (int i = 0; i < dates.length; i++) {
            if (dates[i] == null || dates[i].trim().length() < 7) {
                continue;
            }
            String dateStr = dates[i].trim();
            String year = dateStr.substring(0, 4);
            String month = dateStr.substring(5, 7);
            log.info("算法编码维度统计-铁塔视角-历史刷数-" + dateStr);
            XxlJobLogger.log("算法编码维度统计-铁塔视角-历史刷数-" + dateStr);
            DateVO dateVO = DataUtil.setDateVO(dateStr);
            dateVO.setTableName(Constants.WARN_ORDER_TEBLE + year + month);
            List<Map> list = getEventAlgorithmByDate(dateVO);
            // 即使无数据也 del+写，清掉该窗历史错误口径
            DataUtil.returnString(self.insertEventAlgorithmByDate(dateVO,
                    list == null ? Collections.emptyList() : list));
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getEventAlgorithm(DateVO dateVO) {
        return filterSceneWarningTypeRows(towerStatisticsDAO.getEventAlgorithm(dateVO));
    }

    private List<Map> getEventAlgorithmByDate(DateVO dateVO) {
        return filterSceneWarningTypeRows(towerStatisticsDAO.getEventAlgorithmByDate(dateVO));
    }

    /**
     * 算法统计大模型按下游任务维度：剔除纯场景；下游任务编码即使与场景重叠也保留。
     */
    private List<Map> filterSceneWarningTypeRows(List<Map> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return rows;
        }
        Set<String> sceneIds = loadSceneIdSet();
        if (sceneIds.isEmpty()) {
            return rows;
        }
        Set<String> downstreamIds = loadDownstreamAlarmTypeIdSet();
        return rows.stream()
                .filter(map -> map != null
                        && keepWarningTypeId(String.valueOf(map.get("warning_type_id")),
                                sceneIds, downstreamIds))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventAlgorithm(List<Map> list){
        try {
            towerStatisticsDAO.delEventAlgorithm();
            towerStatisticsDAO.insertEventAlgorithm(list);
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 按 DateVO 先删后写算法汇总（历史刷数）；list 为空仅删除对应窗。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insertEventAlgorithmByDate(DateVO dateVO, List<Map> list) {
        try {
            towerStatisticsDAO.delEventAlgorithmByDate(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                List<List<Map>> partitionList = Lists.partition(list, 5000);
                partitionList.forEach(item -> towerStatisticsDAO.insertEventAlgorithmByDate(item));
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION_INFO, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }


    /**
     * 告警压制事件统计表-铁塔视角
     * @return
     */
    @XxlJob("eventInterceptNumForTower")
    public ReturnT<String> eventInterceptNumForTower(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("告警压制事件统计表-铁塔视角-"+dates[i]);
                    XxlJobLogger.log("告警压制事件统计表-铁塔视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    List<Map> list = getInterceptNum(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertInterceptNum(dateVO, list));
                    }
                }
            }
        } else {
            log.info("告警压制事件统计表-铁塔视角");
            XxlJobLogger.log("告警压制事件统计表-铁塔视角");
            DateVO dateVO = DataUtil.setDateVO();
            List<Map> list = getInterceptNum(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertInterceptNum(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getInterceptNum(DateVO dateVO){
        return towerStatisticsDAO.getInterceptNum(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertInterceptNum(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.deleteInterceptNum(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    towerStatisticsDAO.insertInterceptNum(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 摄像机告警事件统计表-铁塔视角
     * @return
     */
    @XxlJob("eventDeviceNumForTower")
    public ReturnT<String> eventDeviceNumForTower(String param) {
        if (StringUtils.isNotEmpty(param)) {
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null && dates[i].length()>=7) {
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info("摄像机维度告警事件统计沉淀表-铁塔视角-"+dates[i]);
                    XxlJobLogger.log("摄像机维度告警事件统计沉淀表-铁塔视角-"+dates[i]);
                    //初始化执行时间
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getDeviceNum(dateVO);
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.insertDeviceNum(dateVO, list));
                    }
                }
            }
        } else {
            log.info("摄像机维度告警事件统计沉淀表-铁塔视角");
            XxlJobLogger.log("摄像机维度告警事件统计沉淀表-铁塔视角");
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getDeviceNum(dateVO);
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.insertDeviceNum(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }

    private List<Map> getDeviceNum(DateVO dateVO){
        return towerStatisticsDAO.getDeviceNum(dateVO);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean insertDeviceNum(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.deleteDeviceNum(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    towerStatisticsDAO.insertDeviceNum(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     *  昨日告警每日维度统计
     *
     *  @param param 任务执行参数，逗号分隔的日期字符串（格式：yyyy-MM-dd），如果为空则默认统计昨日的数据。
     *  @return 返回任务执行结果，成功返回 ReturnT.SUCCESS。
     */
    @XxlJob("eventYesterdayNumByDayForTowerHandler")
    public ReturnT<String> eventYesterdayNumByDayForTowerHandler(String param) {
        if (StringUtils.isNotEmpty(param)) {
            // 按逗号分隔日期字符串
            String[] dates = param.split(",");
            for (int i = 0; i < dates.length; i++) {
                // 检查日期格式是否符合要求（长度大于等于7，表示至少包含 yyyy-MM）
                if (dates[i] != null && dates[i].length()>=7) {
                    // 提取年份和月份
                    String year = dates[i].substring(0,4);
                    String month = dates[i].substring(5, 7);
                    log.info(Constants.YESTERDAY_DAY_TOWER+dates[i]);
                    XxlJobLogger.log(Constants.YESTERDAY_DAY_TOWER+dates[i]);
                    // 初始化执行时间并设置表名
                    DateVO dateVO = DataUtil.setDateVO(dates[i]);
                    dateVO.setTableName(Constants.WARN_ORDER_TEBLE+year+month);
                    List<Map> list = getEventNumYesterdayListForTower(dateVO);
                    // 如果列表不为空，则将统计结果写入数据库
                    if (!CollectionUtils.isEmpty(list)) {
                        DataUtil.returnString(self.addEventNumYesterdayTower(dateVO, list));
                    }
                }
            }
        } else {
            // 如果未传入日期参数，默认处理昨日的数据
            log.info(Constants.YESTERDAY_DAY_TOWER);
            XxlJobLogger.log(Constants.YESTERDAY_DAY_TOWER);
            // 初始化日期对象为昨日
            DateVO dateVO = DataUtil.setDateVO();
            dateVO.setTableName(TableUtil.getTableName(new Date(), Constants.WARN_ORDER_TEBLE));
            List<Map> list = getEventNumYesterdayListForTower(dateVO);
            // 如果列表不为空，则将统计结果写入数据库
            if (!CollectionUtils.isEmpty(list)) {
                return DataUtil.returnString(self.addEventNumYesterdayTower(dateVO, list));
            }
        }
        return ReturnT.SUCCESS;
    }


    private List<Map> getEventNumYesterdayListForTower(DateVO dateVO){
        List<Map> eventNumDays = towerStatisticsDAO.getEventNumYesterdayListForTower(dateVO);
        return eventNumDays;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean addEventNumYesterdayTower(DateVO dateVO,List<Map> list){
        try {
            towerStatisticsDAO.delEventNumYesterdayTower(dateVO);
            List<List<Map>> partitionList = Lists.partition(list, 5000);
            partitionList.forEach(item ->
                    towerStatisticsDAO.addEventNumYesterdayTower(item)
            );
        }catch (Exception e){
            log.error(Constants.EXCEPTION_INFO,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

}
