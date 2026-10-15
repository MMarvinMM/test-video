package com.chinatower.platform.modules.alarm.task;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.alarm.service.FlowTimeOutTaskService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时更新事件环节超期状态
 */
@Component
@Slf4j
public class FlowTimeOutTask {

    @Resource
    private FlowTimeOutTaskService service;

    /**
     * 告警回收站历史数据清除任务
     */
    @XxlJob("deleteAlarmHistory")
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    public ReturnT<String> deleteAlarmHistory(String param){
        log.info("====任务入参===={}", param);
        XxlJobLogger.log("告警回收站历史数据清除任务开始");
        int i = service.deleteAlarmHistory();
        XxlJobLogger.log("告警回收站历史数据清除任务结束: 已删除 "+ i + " 条数据");
        return ReturnT.SUCCESS;
    }

    /**
     * 运营研判历史数据清除任务
     */
    @XxlJob("deleteAlarmOperateHistory")
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    public ReturnT<String> deleteAlarmOperateHistory(String param){
        log.info("====任务入参===={}", param);
        XxlJobLogger.log("运营研判历史数据清除任务开始");
        int i = service.deleteAlarmOperateHistory();
        XxlJobLogger.log("运营研判历史数据清除任务结束: 已删除 " + i + " 条数据");
        return ReturnT.SUCCESS;
    }

}
