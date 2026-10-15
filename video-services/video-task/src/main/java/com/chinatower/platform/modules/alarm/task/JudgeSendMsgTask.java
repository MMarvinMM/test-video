package com.chinatower.platform.modules.alarm.task;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.modules.alarm.service.JudgeSendMsgService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @className: JudgeSendMsgTask
 * @description: 研判统计定时任务推送
 * @author: yongqi.han
 * @date: 2022/9/4
 * @requestID 需求单号及需求名称
 */
@Component
@Slf4j
public class JudgeSendMsgTask {

    @Resource
    private JudgeSendMsgService judgeSendMsgService;

    /**
     * 研判环节告警短信统计下发
     * 20221202版本改动，研判短信推送和环节类型realLinkType为研判类型无关，
     * 所有短信数据推送和研判环节linkId直接关联    业务实现逻辑已经和客户确认
     */
    @XxlJob("judgeSendMsgHandler")
    public ReturnT<String> judgeSendMsgHandler(String param) {
        log.info("==== 任务入参 ===={}", param);
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("研判告警统计短信任务judgeSendMsgHandler开始执行,开始时间为:{}", startTime);

        if(StringUtils.isEmpty(param)){
            param = "10";
        }
        //设置统计短信研判开关配置
        boolean switchFlag = judgeSendMsgService.getSendMsgSwitch();
        if(switchFlag){
            XxlJobLogger.log("研判告警统计短信任务开关未开启");
            return ReturnT.SUCCESS;
        }

        judgeSendMsgService.judgeSendMsg(param);

        long endTime = System.currentTimeMillis();
        XxlJobLogger.log("研判告警统计短信任务eventAlarmStudyAndJudgeSmsNoticeHandler执行完成,耗时:{}", endTime - startTime);
        return ReturnT.SUCCESS;
    }

}
