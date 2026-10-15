package com.chinatower.platform.modules.alarm.task;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.alarm.service.AlarmStudyAndJudgeSmsNoticeService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @className: AlarmStudyAndJudgeSmsNoticeService
 * @description: 研判环节告警短信通知定时任务
 * @author: peng.haibo
 * @date: 2022/6/24
 * @requestID 需求单号及需求名称
 */
@Component
@Slf4j
public class AlarmStudyAndJudgeSmsNoticeTask {

    @Resource
    private AlarmStudyAndJudgeSmsNoticeService alarmStudyAndJudgeSmsNoticeService;

    /**
     * 研判环节告警短信统计下发
     */
    @XxlJob("eventAlarmStudyAndJudgeSmsNoticeHandler")
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> eventAlarmStudyAndJudgeSmsNoticeHandler(String param) {
        log.info("====任务入参===={}", param);
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("研判告警统计短信任务eventAlarmStudyAndJudgeSmsNoticeHandler开始执行,开始时间为:{}", startTime);

        if (StringUtils.isEmpty(param)) {
            XxlJobLogger.log("入参为空暂不执行");
            return ReturnT.SUCCESS;
        }
        String[] warningTypeIds = param.split(Constants.COMMA);
        for (String warningTypeId : warningTypeIds) {
            if (StringUtils.isEmpty(warningTypeId)) {
                continue;
            }
            // 具体处理逻辑
            // warningTypeId = "1212102"; // 测试用
            alarmStudyAndJudgeSmsNoticeService.dealEventJudgeAlarmSmsNotice(warningTypeId);
        }

        long endTime = System.currentTimeMillis();
        XxlJobLogger.log("研判告警统计短信任务eventAlarmStudyAndJudgeSmsNoticeHandler执行完成,耗时:{}", endTime - startTime);
        return ReturnT.SUCCESS;
    }
}
