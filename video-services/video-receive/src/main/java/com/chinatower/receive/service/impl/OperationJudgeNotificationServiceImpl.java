package com.chinatower.receive.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.message.api.OperationJudgeMsgAPI;
import com.chinatower.message.api.model.OperationJudgeMsg;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.service.IOperationJudgeNotificationService;
import com.chinatower.receive.support.rule.OperationJudgeRuleExecutor;
import com.chinatower.receive.support.rule.enums.OperationJudgeRuleEnum;
import com.chinatower.receive.support.rule.model.RuleContext;
import com.chinatower.receive.vo.entity.WakeUpTowerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * AI研判通知服务实现
 */
@Slf4j
@Service
public class OperationJudgeNotificationServiceImpl implements IOperationJudgeNotificationService {

    @Autowired
    private OperationJudgeRuleExecutor operationJudgeRuleExecutor;
    @Autowired
    private OperationJudgeMsgAPI operationJudgeMsgAPI;

    @Override
    @Async("taskExecutor")
    public void handleOperationJudgeNotification(ReceiveAlarmVO item, List<SysUser> needNotifyUsers) {
        try {
            log.info("开始处理运营研判通知，告警编码：{}", item.getWarningOrderId());
            needNotifyUsers.forEach(user -> {
                RuleContext ruleContext = RuleContext.builder().sysUser(user).gtFiveFlag(Boolean.TRUE).build();
                boolean passed = executeOperationJudgeRules(ruleContext);
                // 通过websocket通知小塔打开弹窗
                if (passed) {
                    processOperationJudgeNotificationAsync(user);
                }
            });
        } catch (Exception e) {
            log.error("处理运营研判通知异常，告警编码：{}", item.getWarningOrderId(), e);
        }
    }

    @Override
    public WakeUpTowerDTO wakeUpTowerByRule(List<String> ruleCodes) {
        log.info("收到唤起小塔请求，规则编码列表：{}", ruleCodes);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        RuleContext ruleContext = RuleContext.builder().sysUser(user).build();
        boolean wakeUpResult = operationJudgeRuleExecutor.executeRules(ruleCodes, ruleContext);
        return WakeUpTowerDTO.builder().wakeUpResult(wakeUpResult).extendInfo(ruleContext.getExtendInfo()).build();
    }

    /**
     * 通知小塔打开弹窗通知
     */
    private void processOperationJudgeNotificationAsync(SysUser user) {
        try {
            OperationJudgeMsg operationJudgeMsgReq = new OperationJudgeMsg();
            operationJudgeMsgReq.setUserId(user.getUserId());
            operationJudgeMsgAPI.sendNotice(operationJudgeMsgReq);
            log.info("通知小塔打开弹窗通知，用户：{}", JSONUtil.toJsonStr(user));
        } catch (Exception e) {
            log.error("通知小塔打开弹窗通知异常", e);
        }
    }


    /**
     * 执行运营研判规则
     *
     * @return 是否需要通知
     */
    private boolean executeOperationJudgeRules(RuleContext ruleContext) {
        try {
            log.info("开始执行运营研判规则，ruleContext：{}", JSONUtil.toJsonStr(ruleContext));
            // 定义要执行的规则列表
            List<String> ruleCodes = Arrays.asList(
                    OperationJudgeRuleEnum.AUTO_JUDGE_TASK_DISABLED.getCode(),      // 自动研判任务未开启规则
                    OperationJudgeRuleEnum.PENDING_JUDGE_COUNT_THRESHOLD.getCode() // 待研判数量阈值规则
            );
            // 执行规则
            boolean passed = operationJudgeRuleExecutor.executeRules(ruleCodes, ruleContext);

            if (passed) {
                log.info("全部规则通过");
                return true;
            } else {
                log.info("部分规则未通过");
                return false;
            }

        } catch (Exception e) {
            log.error("执行运营研判规则异常，", e);
            return false;
        }
    }

}
