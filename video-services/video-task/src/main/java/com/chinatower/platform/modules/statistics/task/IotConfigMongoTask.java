package com.chinatower.platform.modules.statistics.task;

import com.chinatower.platform.modules.statistics.service.IotConfigMongoTaskService;
import com.chinatower.platform.modules.statistics.service.UserStatisticsService;
import com.chinatower.platform.utils.DataUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;


@Component
@Slf4j
public class IotConfigMongoTask {

    @Resource
    private IotConfigMongoTaskService iotConfigMongoTaskService;


    /**
     * 物联配置累计值MongoDB定时删
     */
    @XxlJob("IotConfigMongoFactoryDeleteHandler")
    public ReturnT<String> IotConfigMongoFactoryDeleteHandler(String param) {
        String now = LocalDateTime.now().toString();
        log.info("物联配置累计值MongoDB定时删->{}开始", now);
        long begin = System.currentTimeMillis();
        boolean success = iotConfigMongoTaskService.iotConfigMongoFactoryDelete();
        long end = System.currentTimeMillis();
        XxlJobLogger.log("物联配置累计值MongoDB定时删->{},执行时间：{}，耗时：{}ms", (success ? "成功" : "失败"), now, (end - begin));
        log.info("物联配置累计值MongoDB定时删->{}结束", now);
        return DataUtil.returnString(success);
    }
}
