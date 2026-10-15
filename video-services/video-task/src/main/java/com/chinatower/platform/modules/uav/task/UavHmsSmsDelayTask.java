package com.chinatower.platform.modules.uav.task;

import com.chinatower.platform.modules.uav.service.UavHmsSmsDelayService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * HMS 飞行告警短信压制：延迟合并发送任务
 */
@Component
@Slf4j
public class UavHmsSmsDelayTask {

    @Resource
    private UavHmsSmsDelayService uavHmsSmsDelayService;

    @XxlJob("uavHmsSmsDelayHandler")
    public ReturnT<String> uavHmsSmsDelayHandler(String param) {
        long start = System.currentTimeMillis();
        XxlJobLogger.log("HMS短信压制延迟任务开始 param={}, start={}", param, start);
        try {
            uavHmsSmsDelayService.scanAndHandleDue();
            XxlJobLogger.log("HMS短信压制延迟任务结束costMs={}", System.currentTimeMillis() - start);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error("HMS短信压制延迟任务异常 param={}", param, e);
            return ReturnT.FAIL;
        }
    }
}

