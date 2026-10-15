package com.chinatower.platform.modules.alarm.task;


import com.chinatower.platform.modules.alarm.service.CreateTableService;
import com.chinatower.platform.modules.alarm.service.DelaySendMsgService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 建表定时任务
 */
@Component
@Slf4j
public class CreateTableTask {

    @Autowired
    private CreateTableService createTableService;

    /**
     * 建表定时
     * @param param
     * @return
     */
    @XxlJob("createTableHandler")
    public ReturnT<String> createTableHandler(String param) {
        log.info("==== 任务入参 ===={}", param);
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("建表定时任务开始执行,开始时间为:{}", startTime);

        createTableService.createNewTable(param);

        long endTime = System.currentTimeMillis();
        XxlJobLogger.log("建表定时任务执行完成,耗时:{}", endTime - startTime);
        return ReturnT.SUCCESS;
    }
}
