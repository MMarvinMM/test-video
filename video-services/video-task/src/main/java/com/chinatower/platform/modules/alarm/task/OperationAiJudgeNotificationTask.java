package com.chinatower.platform.modules.alarm.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.api.IAlarmOperateTaskService;
import com.chinatower.event.api.ITowerEventWarningService;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dataobject.AgentOperationTaskReminderDO;
import com.chinatower.event.entity.dto.AiJudgeResultDTO;
import com.chinatower.event.entity.dto.AlarmOperateTaskDTO;
import com.chinatower.event.entity.vo.AiJudgeAlarmVO;
import com.chinatower.event.entity.vo.OperationTaskVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.message.api.OperationJudgeMsgAPI;
import com.chinatower.message.api.model.OperationJudgeMsg;
import com.chinatower.message.api.model.WebSocketMsgType;
import com.chinatower.platform.common.OverdueNotifyConfig;
import com.chinatower.platform.modules.alarm.dao.AgentOperationTaskExecutionDAO;
import com.chinatower.platform.modules.alarm.dao.AgentOperationTaskReminderDAO;
import com.chinatower.platform.modules.alarm.entity.VmJudgeTaskExecutionDO;
import com.chinatower.platform.modules.alarm.service.OperationAiJudgeNotificationService;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @className: OperationAiJudgeNotificationTask
 * @description: AI研判结果通知
 * @author: hyq
 * @date: 20251023
 * @requestID 需求单号及需求名称
 */
@Component
@Slf4j
public class OperationAiJudgeNotificationTask {

    @Autowired
    private OperationAiJudgeNotificationService operationAiJudgeNotificationService;


    /**
     * 研判环节:自动研判执行结果通知
     */
    @XxlJob("automaticJudgeResultHandler")
    public ReturnT<String> automaticJudgeResultHandler(String param) {
        log.info("====automaticJudgeResultHandler任务入参===={}", param);
        long start = System.currentTimeMillis();
        XxlJobLogger.log("自动研判执行结果通知任务automaticJudgeResultHandler开始执行,开始时间为:{}", start);
        operationAiJudgeNotificationService.automaticJudgeResult(param);
        // 2、进入推送小塔研判结果 - 进入系统通知统一有 wake-up进行通知
        // 1、查询自动研判结果数据信息，封装通知消息
//        Map msgMap = new HashMap();
//        msgMap.put("status","0");   // 0 执行成功 1 执行失败
//        msgMap.put("msg","[X日至Y日]执行违建告警自动研判处理结果，本次共处理n条，其中真警n、假警n、未研判n，请及时确认结果哦~。其中X代表研判结果中处理日期最早的时间，Y代表最晚的，如果X和Y是一天，就只展示X日。");
//        // 2、批量推送小塔研判结果
//        WebSocketMsgType webSocketMsgType = WebSocketMsgType.AUTOMATIC_JUDGE_RESULT_NOTICE;
//        this.processOperationJudgeNotificationAsync(param,JSONObject.toJSONString(msgMap),webSocketMsgType);

        long end = System.currentTimeMillis();
        XxlJobLogger.log("自动研判执行结果通知任务automaticJudgeResultHandler执行完成,耗时:{}", end - start);
        return ReturnT.SUCCESS;
    }


    /**
     * 研判环节:超期研判执行结果通知
     */
    @XxlJob("timeoutJudgeResultHandler")
    public ReturnT<String> timeoutJudgeResultHandler(String param) {
        log.info("====超期研判执行结果通知任务入参===={}", param);
        long start = System.currentTimeMillis();
        XxlJobLogger.log("超期研判执行结果通知任务timeoutJudgeResultHandler开始执行,开始时间为:{}", start);
        operationAiJudgeNotificationService.timeoutJudgeResult(param);

        long end = System.currentTimeMillis();
        XxlJobLogger.log("超期研判执行结果通知任务timeoutJudgeResultHandler执行完成,耗时:{}", end - start);
        return ReturnT.SUCCESS;
    }

    /**
     * 检查通知是否接收，如果未接收发送通知
     * @param executionNotifyList
     * @param reminderDOList
     */
//    private void checkTaskNotify(List<VmJudgeTaskExecutionDO> executionNotifyList, List<AgentOperationTaskReminderDO> reminderDOList,List<AiJudgeAlarmVO> unJudgeAlarmList) {
//        Map<Long,List<AgentOperationTaskReminderDO>> sentReminderMap = null;
//        if(!CollectionUtils.isEmpty(reminderDOList)){
//            sentReminderMap = reminderDOList.stream().filter(reminder -> reminder.getExecutionId() != null).collect(Collectors.groupingBy(AgentOperationTaskReminderDO::getExecutionId));
//        }
//        //获取模板信息
//        String oneTemplate = generateDescriptionWithTemplate(Constants.ONE_JUDGE_RESULT_NOTICE,null);
//        String template = generateDescriptionWithTemplate(Constants.AUTO_JUDGE_RESULT_NOTICE,null);
//        //发送通知并记录
//        for(VmJudgeTaskExecutionDO latestResult:executionNotifyList){
//            List<AgentOperationTaskReminderDO> sentNotifyList = sentReminderMap.get(latestResult.getExecutionId());
//            // 计算未处理时长（分钟）
//            if(CollectionUtils.isEmpty(sentNotifyList)){
//                // 发送提醒并记录 todo-hyq
////                sendNotifyAndRecord(oneTemplate,template,latestResult);
//            }
//
//        }
//    }

    /**
     * 算法权重结果定时同步
     */
    @XxlJob("syncAlgorithmWeightHandler")
    public ReturnT<String> syncAlgorithmWeightHandler(String param) {
        log.info("====syncAlgorithmWeight任务入参===={}", param);
        long start = System.currentTimeMillis();
        XxlJobLogger.log("权重结果定时同步syncAlgorithmWeight开始执行,开始时间为:{}", start);
        operationAiJudgeNotificationService.syncAlgorithmWeight(param);

        long end = System.currentTimeMillis();
        XxlJobLogger.log("权重结果定时同步syncAlgorithmWeight执行完成,耗时:{}", end - start);
        return ReturnT.SUCCESS;
    }

}
