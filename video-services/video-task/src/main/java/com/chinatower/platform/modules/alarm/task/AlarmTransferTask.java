package com.chinatower.platform.modules.alarm.task;


import com.chinatower.platform.modules.alarm.service.AlarmTransferService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 告警转移定时任务
 */
@Component
@Slf4j
public class AlarmTransferTask {

    @Autowired
    private AlarmTransferService alarmTransferService;

    /**
     * 告警转移定时任务
     * @param param
     * @return
     */
    @XxlJob("alarmTransferHandler")
    public ReturnT<String> alarmTransfer(String param) {
        long startTime = System.currentTimeMillis();
        log.info("alarmTransferHandler-开始时间:{}",startTime);
        XxlJobLogger.log("告警转移定时任务alarmTransferHandler开始执行,开始时间为:{}", startTime);

        alarmTransferService.alarmTransfer();

        long endTime = System.currentTimeMillis();
        log.info("alarmTransferHandler-结束时间:{}",endTime - startTime);
        XxlJobLogger.log("告警转移定时任务alarmTransferHandler执行完成,耗时:{}", endTime - startTime);
        return ReturnT.SUCCESS;
    }
}
