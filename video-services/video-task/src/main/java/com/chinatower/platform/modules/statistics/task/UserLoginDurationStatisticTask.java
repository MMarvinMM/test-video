package com.chinatower.platform.modules.statistics.task;

import com.chinatower.platform.modules.statistics.service.UserStatisticsService;
import com.chinatower.platform.utils.DataUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 用户登录时长统计
 *
 * @author qq on 2023/4/11
 */
@Component
@Slf4j
public class UserLoginDurationStatisticTask {

    @Resource
    private UserStatisticsService userStatisticsService;


    /**
     * 用户登录时长redis统计
     */
    @XxlJob("userLoginDurationByRedisHandler")
    public ReturnT<String> userLoginDurationByRedisHandler(String param) {
        String now = LocalDateTime.now().toString();
        log.info("统计用户登录时长任务->{}开始", now);
        long begin = System.currentTimeMillis();
        boolean success = userStatisticsService.statisticUserLoginDurationByRedis();
        long end = System.currentTimeMillis();
        XxlJobLogger.log("统计用户登录时长任务->{},执行时间：{}，耗时：{}ms", (success ? "成功" : "失败"), now, (end - begin));
        log.info("统计用户登录时长任务->{}结束", now);
        return DataUtil.returnString(success);
    }
}
