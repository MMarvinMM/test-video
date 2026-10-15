package com.chinatower.platform.modules.alarm.task;


import com.chinatower.platform.modules.alarm.service.DelaySendMsgService;
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
public class DelaySendMsgTask {

    @Autowired
    private DelaySendMsgService delaySendMsgService;

    /**
     * 延迟消息推送
     * @param param
     * @return
     */
    @XxlJob("delaySendMsgHandler")
    public ReturnT<String> delaySendMsgHandler(String param) {
        log.info("==== 任务入参 ===={}", param);
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("延迟消息推送delaySendMsgHandler开始执行,开始时间为:{}", startTime);

        delaySendMsgService.sendDelaySendMsg(param);

        long endTime = System.currentTimeMillis();
        XxlJobLogger.log("延迟消息推送delaySendMsgHandler执行完成,耗时:{}", endTime - startTime);
        return ReturnT.SUCCESS;
    }
}
