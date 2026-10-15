package com.chinatower.platform.modules.alarm.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.platform.modules.alarm.service.AiJudgeDispatchService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 告警分级 AI 研判定时任务：拉取未研判 → 研判中 → 投递 Kafka
 * XXL 参数可选 JSON：{"limit":50}
 */
@Slf4j
@Component
public class AiJudgeDispatchTask {

    @Autowired
    private AiJudgeDispatchService aiJudgeDispatchService;

    @XxlJob("aiJudgeDispatchHandler")
    public ReturnT<String> aiJudgeDispatch(String param) {
        long start = System.currentTimeMillis();
        XxlJobLogger.log("aiJudgeDispatchHandler开始, param={}", param);
        log.info("aiJudgeDispatchHandler开始, param={}", param);
        int limit = 50;
        if (StringUtils.isNotBlank(param)) {
            try {
                JSONObject json = JSON.parseObject(param);
                if (json != null && json.getInteger("limit") != null) {
                    limit = json.getInteger("limit");
                }
            } catch (Exception e) {
                log.warn("解析XXL参数失败，使用默认limit=50, param={}", param);
            }
        }
        aiJudgeDispatchService.dispatchPending(limit);
        long cost = System.currentTimeMillis() - start;
        XxlJobLogger.log("aiJudgeDispatchHandler结束, 耗时={}ms", cost);
        log.info("aiJudgeDispatchHandler结束, 耗时={}ms", cost);
        return ReturnT.SUCCESS;
    }
}
