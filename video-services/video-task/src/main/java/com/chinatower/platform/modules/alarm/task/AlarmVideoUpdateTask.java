package com.chinatower.platform.modules.alarm.task;

import com.chinatower.platform.modules.alarm.service.AlarmVideoUpdateService;
import com.chinatower.platform.modules.statistics.service.IotConfigMongoTaskService;
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
public class AlarmVideoUpdateTask {

    @Resource
    private AlarmVideoUpdateService alarmVideoUpdateService;


    /**
     * 抓视频 进度100% 定时更新video_url
     */
    @XxlJob("AlarmVideoUpdateHandler")
    public ReturnT<String> AlarmVideoUpdateHandler(String param) {
        String now = LocalDateTime.now().toString();
        log.info("抓视频定时更新视频路径->{}开始", now);
        long begin = System.currentTimeMillis();
        boolean success = alarmVideoUpdateService.alarmVideoUpdate();
        long end = System.currentTimeMillis();
        XxlJobLogger.log("抓视频定时更新视频路径->{},执行时间：{}，耗时：{}ms", (success ? "成功" : "失败"), now, (end - begin));
        log.info("抓视频定时更新视频路径->{}结束", now);
        return DataUtil.returnString(success);
    }


    /**
     * taskBatchId查询定时更新二次识别结果
     */
    @XxlJob("SecondResultHandler")
    public ReturnT<String> SecondResultHandler(String param) {
        String now = LocalDateTime.now().toString();
        log.info("taskBatchId查询定时更新二次识别结果->{}开始", now);
        long begin = System.currentTimeMillis();
        boolean success = alarmVideoUpdateService.secondResultHandler();
        long end = System.currentTimeMillis();
        XxlJobLogger.log("taskBatchId查询定时更新二次识别结果->{},执行时间：{}，耗时：{}ms", (success ? "成功" : "失败"), now, (end - begin));
        log.info("taskBatchId查询结果定时更新二次识别结果->{}结束", now);
        return DataUtil.returnString(success);
    }
}
