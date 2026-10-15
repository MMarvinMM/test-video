package com.chinatower.platform.modules.alarm.task;


import com.chinatower.platform.modules.alarm.service.DelaySendMsgService;
import com.chinatower.platform.modules.alarm.service.OverdueRemindSendService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 延迟消息推送定时任务
 */
@Component
@Slf4j
public class OverdueRemindSendTask {

    @Autowired
    private OverdueRemindSendService overdueRemindSendService;

    /**
     * 环节及受理超期消息延时推送
     * @param param
     * @return
     */
    @XxlJob("overdueRemindMsgHandler")
    public ReturnT<String> overdueRemindMsgHandler(String param) {
        log.info("==== 任务入参 ===={}", param);
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("环节及受理超期开始执行开始时间为:{}", startTime);

        overdueRemindSendService.overdueRemindSendMsg();
        long endTime = System.currentTimeMillis();
        XxlJobLogger.log("环节及受理超期开始执行执行完成耗时:{}", endTime - startTime);
        return ReturnT.SUCCESS;
    }
}
