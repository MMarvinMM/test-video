package com.chinatower.platform.modules.alarm.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.alarm.dao.AlarmRecycleDAO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

/**
 * 铁塔视角告警回收定时清理
 */
@Component
@Slf4j
public class AlarmRecycleService {
    @Resource
    private AlarmRecycleDAO alarmRecycleDAO;

    /**
     * 回收站每个月1号00:00:00对前两个月之前的数据进行清理。
     * 例如当前5月1日，清除3月1日之前的数据。
     * @param param
     * @return
     */
    @XxlJob("eventAlarmRecycleHandler")
    @DS(EbikeDataBaseConstant.PLATFORM)
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> eventAlarmRecycleHandler(String param) {
        log.info("铁塔视角告警回收定时清理===start==={}", DateUtils.getTime());
        Calendar calc =Calendar.getInstance();
        calc.set(calc.get(Calendar.YEAR),calc.get(Calendar.MONTH)-2,calc.get(Calendar.DAY_OF_MONTH),0,0,0);
        Date recycleDate = calc.getTime();
        log.info("清理回收站时间节点==={}",DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS,recycleDate));
        int num = alarmRecycleDAO.countRecycleNum(recycleDate);
        if (num > 0) {
            //备份历史表
            alarmRecycleDAO.addHisRecycleAlarm(recycleDate);
            //删除数据
            alarmRecycleDAO.delRecycleAlarm(recycleDate);
        }
        return ReturnT.SUCCESS;
    }
}
