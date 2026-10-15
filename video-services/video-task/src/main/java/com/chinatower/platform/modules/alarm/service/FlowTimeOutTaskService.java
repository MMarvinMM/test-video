package com.chinatower.platform.modules.alarm.service;

import com.chinatower.platform.modules.alarm.dao.FlowTimeOutDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * @className: FlowTimeOutTaskService
 * @description: 环节处理超期
 */
@Slf4j
@Component
public class FlowTimeOutTaskService {

    @Resource
    private FlowTimeOutDAO timeOutDAO;

    /**
     * 删除历史告警
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteAlarmHistory(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String dayStr = "-01 00:00:00";
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(new Date());
        rightNow.add(Calendar.MONTH,-2);
        Date twoMonthBefore=rightNow.getTime();
        String date = sdf.format(twoMonthBefore) + dayStr;
        log.info("Date: " + date);
        return timeOutDAO.deleteHistoryList(date);
    }

    /**
     * 备份数据至运营研判历史表
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteAlarmOperateHistory(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String dayStr = "-01 00:00:00";
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(new Date());
        rightNow.add(Calendar.MONTH,-3);
        Date twoMonthBefore=rightNow.getTime();
        String threeMonthsAgo = sdf.format(twoMonthBefore) + dayStr;
        Map<String,String> params = new HashMap<>();
        params.put("threeMonthsAgo", threeMonthsAgo);
        log.info("Date: "+ threeMonthsAgo);
        // 备份对应日期内的数据到历史表
        int bak = timeOutDAO.bakOperate3MonthsAgo(threeMonthsAgo);
        log.info("已备份数据至运营研判历史表 " + bak + " 条");
        // 删除原表对应日期内的数据
        return timeOutDAO.deleteOperate3MonthsAgo(threeMonthsAgo);
    }
}
