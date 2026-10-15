package com.chinatower.platform.task;

import com.chinatower.platform.service.SynchLocalizationAlarmService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @desription: 定时任务-属地化下沉节点数据同步
 * @data 2023/10/30 14:30
 * @author lyy
 * @version v1.6.30
 *
 */
@Component
@Slf4j
public class SynchLocalizationAlarmTask {

    @Autowired
    private SynchLocalizationAlarmService localizationAlarmService;

    @XxlJob("SynchSinkingNodeAlarmHandler")
    public ReturnT<String> SynchSinkingNodeAlarmHandler(String param) {
        long startTime = System.currentTimeMillis();
        log.info("SynchSinkingNodeAlarmHandler-开始时间:{}",startTime);
        XxlJobLogger.log("获取属地化下沉节点数据定时任务SynchSinkingNodeAlarmHandler开始执行,开始时间为:{}", startTime);

        localizationAlarmService.excuteHandler(param);

        long endTime = System.currentTimeMillis();
        log.info("SynchSinkingNodeAlarmHandler-结束时间:{}",endTime - startTime);
        XxlJobLogger.log("获取属地化下沉节点数据定时任务SynchSinkingNodeAlarmHandler执行完成,耗时:{}", endTime - startTime);
        return ReturnT.SUCCESS;
    }
}
