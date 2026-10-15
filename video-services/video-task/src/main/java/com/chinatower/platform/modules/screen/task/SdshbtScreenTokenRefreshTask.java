package com.chinatower.platform.modules.screen.task;

import com.chinatower.platform.modules.screen.service.ScreenTokenRefreshResult;
import com.chinatower.platform.modules.screen.service.SdshbtScreenTokenService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * sdshbt 第三方大屏 token 定时刷新（建议 Cron: 0 0/5 * * * ?）
 */
@Component
@Slf4j
public class SdshbtScreenTokenRefreshTask {

    @Resource
    private SdshbtScreenTokenService sdshbtScreenTokenService;

    @XxlJob("sdshbtScreenTokenRefreshHandler")
    public ReturnT<String> sdshbtScreenTokenRefreshHandler(String param) {
        log.info("sdshbt 大屏 token 刷新任务开始");
        XxlJobLogger.log("sdshbt 大屏 token 刷新任务开始");

        ScreenTokenRefreshResult result = sdshbtScreenTokenService.refreshAll();
        XxlJobLogger.log(result.getMessage());

        if (result.isSuccess()) {
            return new ReturnT<>(ReturnT.SUCCESS_CODE, result.getMessage());
        }
        return new ReturnT<>(ReturnT.FAIL_CODE, result.getMessage());
    }
}
