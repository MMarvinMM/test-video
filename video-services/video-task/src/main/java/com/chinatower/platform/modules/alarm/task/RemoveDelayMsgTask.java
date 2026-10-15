package com.chinatower.platform.modules.alarm.task;

import com.chinatower.platform.modules.alarm.service.RemoveDelayMsgService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @className: RemoveDelayMsgTask
 * @description: 对稍后推送记录进行定期清除
 */
@Component
@Slf4j
public class RemoveDelayMsgTask {

    @Autowired
    private RemoveDelayMsgService removeDelayMsgService;

    /**
     * 对稍后推送记录进行定期清除
     */
    @XxlJob("removeDelayMsgHandler")
    public ReturnT<String> removeDelayMsg(String param) {
        long startTime = System.currentTimeMillis();
        log.info("removeDelayMsg-开始时间:{}",startTime);
        XxlJobLogger.log("对稍后推送记录进行定期清除removeDelayMsgHandler开始执行,开始时间为:{}", startTime);

        removeDelayMsgService.removeDelayMsg();
        long endTime = System.currentTimeMillis();
        log.info("removeDelayMsg-结束时间:{}",endTime - startTime);
        XxlJobLogger.log("对稍后推送记录进行定期清除removeDelayMsgHandler执行完成,耗时:{}", endTime - startTime);
        return ReturnT.SUCCESS;
    }

}
