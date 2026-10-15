package com.chinatower.platform.modules.alarm.config;

import com.chinatower.common.core.util.AiJudgeColumnSupportUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 将 Nacos 配置 ai.judge.column-since-month 注入告警分级字段兼容工具
 */
@Component
public class AiJudgeColumnSupportConfigurer {

    @Value("${ai.judge.column-since-month:202608}")
    public void configureColumnSinceMonth(String columnSinceMonth) {
        AiJudgeColumnSupportUtil.setColumnSinceMonth(columnSinceMonth);
    }
}
