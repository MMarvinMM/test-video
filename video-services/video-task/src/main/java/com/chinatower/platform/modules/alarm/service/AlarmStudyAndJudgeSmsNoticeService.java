package com.chinatower.platform.modules.alarm.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @className: AlarmStudyAndJudgeSmsNoticeService
 * @description: 研判环节告警短信统计通知服务
 * @author: peng.haibo
 * @date: 2022/6/24
 */
public interface AlarmStudyAndJudgeSmsNoticeService {

    /**
     * @description: 处理研判告警统计短信通知
     * @author: peng.haibo
     * @date: 2022/6/24
     * @param warningTypeId 告警类型ID
     */
    void dealEventJudgeAlarmSmsNotice(String warningTypeId);

}
