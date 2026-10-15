package com.chinatower.receive.support.rule.impl;

import com.chinatower.receive.service.IOperationTaskService;
import com.chinatower.receive.support.rule.enums.OperationJudgeRuleEnum;
import com.chinatower.receive.support.rule.interfaces.OperationJudgeSimpleRule;
import com.chinatower.receive.support.rule.model.RuleContext;
import com.chinatower.receive.vo.dto.OperationTaskListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 自动研判任务是否开启规则
 */
@Slf4j
@Component
public class AutoJudgeTaskDisabledRule implements OperationJudgeSimpleRule {

    @Autowired
    private IOperationTaskService operationTaskService;

    @Override
    public OperationJudgeRuleEnum getCode() {
        return OperationJudgeRuleEnum.AUTO_JUDGE_TASK_DISABLED;
    }

    @Override
    public boolean execute(RuleContext context) {
        try {
            // 从上下文获取用户ID列表，如果没有则使用默认用户ID列表
            String userId = context.getSysUser().getUserId();

            // 调用任务接口查询研判任务是否开启,未开启返回true
            OperationTaskListResponse taskInfo = operationTaskService.getUserTaskInfo(userId);
            if (taskInfo != null) {
                // 注意：未开启返回true，用于小塔通知
                boolean isTaskDisabled = taskInfo.getTaskStatus() == 0;
                log.info("自动研判任务状态检查结果: 任务开启状态={}, 用户ID={}", isTaskDisabled, userId);
                context.setAiTaskId(taskInfo.getTaskId());
                return !isTaskDisabled;
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("检查自动研判任务状态异常", e);
            return true;
        }
    }

}
