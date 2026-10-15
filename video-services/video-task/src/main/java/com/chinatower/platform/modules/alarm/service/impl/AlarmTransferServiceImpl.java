package com.chinatower.platform.modules.alarm.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.TableUtil;
import com.chinatower.event.util.DateUtils;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.entity.request.JgPushRequest;
import com.chinatower.platform.modules.alarm.dao.AlarmTransferDAO;
import com.chinatower.platform.modules.alarm.entity.AlarmTransferConfigDto;
import com.chinatower.platform.modules.alarm.entity.AlarmTransferResultVo;
import com.chinatower.platform.modules.alarm.service.AlarmTransferService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AlarmTransferServiceImpl implements AlarmTransferService {

    @Autowired
    private AlarmTransferDAO alarmTransferDAO;

    /**
     * 获取当前月份的最后一天
     * @return
     */
    public Integer getLastDay() {
        // 获取当前年月
        YearMonth yearMonth = YearMonth.now();

        // 获取当前月份的最后一天
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        // 获取最后一天的日期
        Integer dayOfMonth = lastDayOfMonth.getDayOfMonth();
        log.info("当前月份的最后一天是:" + dayOfMonth);
        return dayOfMonth;
    }

    /**
     * 获取最大的一天
     * @param list
     * @return
     */
    public Integer getMaxDay(List<AlarmTransferConfigDto> list) {
        int maxAge = Integer.MIN_VALUE;

        for (AlarmTransferConfigDto dto : list) {
            Integer age = dto.getTransferInterval();
            if (age > maxAge) {
                maxAge = age;
            }
        }

        return maxAge;
    }

    /**
     * 告警转移定时
     * 根据告警转移配置，从当前月份开始向前扫描多个月的告警表，匹配符合条件的告警工单。
     * 对符合条件的告警工单进行转移操作，并更新相关表的状态标记。
     */
    @Override
    public void alarmTransfer() {
        // 获取当前日期和表名（当前月份的告警表）
        Date date = new Date();
        String tableName = TableUtil.getTableName(date, Constants.WARN_ORDER_TEBLE);
        // 查询所有告警转移配置
        List<AlarmTransferConfigDto> configList = alarmTransferDAO.getAlarmTansferConfigList();
        log.info("告警转移配置集合-alarmTransfer:{}", JSON.toJSONString(configList));
        List<AlarmTransferConfigDto> realConfigList = new ArrayList<>();
        for (AlarmTransferConfigDto dto : configList) {
            if (Constants.ONE.equals(dto.getAlarmTransferTime())) {
                // 如果配置为按月执行，检查当前日是否与配置的日期相匹配
                String day = DateUtils.date2Str(new Date(), "d"); // 当前日期的“日”部分
                if (dto.getTransferDate().equals(day)) {
                    realConfigList.add(dto);
                    continue;
                }
                // 如果配置的日期大于本月最后一天，按照本月最后一天的时间与当天日进行比较
                Integer lastDay = this.getLastDay();
                if (Integer.valueOf(dto.getTransferDate()) > lastDay) {
                    if (String.valueOf(lastDay).equals(day)) {
                        realConfigList.add(dto);
                        continue;
                    }
                }
            } else {
                // 如果配置为实时执行，则直接加入结果列表
                realConfigList.add(dto);
            }
        }

        // 获取配置中间隔时间最长的天数（用于确定需要扫描的月数）
        Integer maxDay = this.getMaxDay(realConfigList);
        // 将配置按“是否未处理过”分组
        // 从未处理得配置
        List<AlarmTransferConfigDto> neverProcessList = realConfigList.stream().filter(a -> a.getNeverProcessFlag().equals(Constants.ONE)).collect(Collectors.toList());
        // 可处理配置
        List<AlarmTransferConfigDto> noNeverProcessList = realConfigList.stream().filter(a -> a.getNeverProcessFlag().equals(Constants.PARAM_ZERO)).collect(Collectors.toList());

        //需要扫描的月数（按照最大间隔时间计算）
        int month = maxDay/30+7;
        int count = 1;
        // 遍历每个月的表，处理告警转移
        while (count <= month){
            try{
                log.info("告警转移查询表名:"+tableName);
                // 查询符合配置的工单（未处理和可处理的配置分别查询）
                List<AlarmTransferResultVo> resultList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(neverProcessList)) {
                    resultList = alarmTransferDAO.getNeverOrderIdList(neverProcessList, tableName);
                }
                List<AlarmTransferResultVo> resultListTwo = new ArrayList<>();
                if (!CollectionUtils.isEmpty(noNeverProcessList)) {
                    resultListTwo = alarmTransferDAO.getNoNeverOrderIdList(noNeverProcessList, tableName);
                }
                // 合并两次查询的结果
                List<AlarmTransferResultVo> allList = new ArrayList<>();
                allList.addAll(resultList);
                allList.addAll(resultListTwo);
                log.info("符合配置的工单:{}", allList.size());
                // 匹配配置和工单，筛选出需要转移的工单 ID
                List<String> orderIdList = new ArrayList<>();
                for (AlarmTransferConfigDto configDto : configList) {
                    for (AlarmTransferResultVo vo : allList) {
                        // 匹配工单和转移配置
                        if (vo.getTenantId().equals(configDto.getTenantId()) && vo.getIndustryCode().equals(configDto.getIndustryCode()) &&
                                vo.getWarningSource().equals(configDto.getWarningSource()) && vo.getWarningTypeId().equals(configDto.getAlarmType())) {

                            // 判断工单是否已超过转移间隔时间
                            Date alarmTime = DateUtils.string2Date(vo.getAlarmTime());
                            Date overTime = DateUtils.getDateAfter(alarmTime, Integer.valueOf(configDto.getTransferInterval()));
                            if (overTime.getTime() <= new Date().getTime()) {
                                // 可转移
                                orderIdList.add(vo.getWarningOrderId());
                            }

                        }
                    }
                }
                log.info("需要转移的工单数:{}", orderIdList.size());
                log.info("需要转移的工单:{}", JSON.toJSONString(orderIdList));
                // 对符合条件的工单进行转移操作
                if (!CollectionUtils.isEmpty(orderIdList)) {
                    // 将工单 ID 按每 5000 条分批处理
                    List<List<String>> lists = Lists.partition(orderIdList, 5000);
                    for (List<String> list : lists) {
                        alarmTransferDAO.updateWaringOrderDeleteFlag(list, tableName);
                        //v1.6.60 同步将被转移的告警从领导批示删除
                        alarmTransferDAO.updateLearderAuditDeleteFlag(list);
                    }
                }
            }catch (Exception e){
                log.error("转移告警出现异常:"+JSON.toJSONString(tableName));
            }finally {
                // 更新为上个月的表名，继续扫描
                String lastMonth = DateUtils.getlastMonth(DateUtils.dateToStringYM(date));
                tableName = Constants.WARN_ORDER_TEBLE+lastMonth;
                date = DateUtils.stringToDateYM(lastMonth);
                count++;// 增加扫描计数
            }
        }

    }
}
