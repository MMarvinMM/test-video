package com.chinatower.receive.support.rule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 运营研判通知规则枚举
 */
@AllArgsConstructor
@Getter
public enum OperationJudgeRuleEnum {
    
    /**
     * 自动研判任务是否开启规则
     */
    AUTO_JUDGE_TASK_DISABLED("AUTO_JUDGE_TASK_DISABLED", "自动研判任务是否开启规则"),
    /**
     * 待研判数量阈值规则
     */
    PENDING_JUDGE_COUNT_THRESHOLD("PENDING_JUDGE_COUNT_THRESHOLD", "待研判数量阈值规则"),
    /**
     * 待研判数量阈值规则(带缓存)
     */
    PENDING_JUDGE_COUNT_THRESHOLD_WITH_CACHE("PENDING_JUDGE_COUNT_THRESHOLD_WITH_CACHE", "待研判数量阈值规则(带缓存)"),
    /**
     * 每个账号每天进入该页面次数规则
     */
    PAGE_OPEN_COUNT_THRESHOLD("PAGE_OPEN_COUNT_THRESHOLD", "每个账号每天进入该页面次数规则"),
    /**
     * 运营人员进入系统之后，若系统已经执行完了并且人工未处理的时候需要及时通知规则
     */
    PENDING_MANUAL_REVIEW("PENDING_MANUAL_REVIEW", "运营人员进入系统之后，若系统已经执行完了并且人工未处理的时候需要及时通知规则"),

    ;
    
    private final String code;
    private final String name;
    
    /**
     * 根据编码获取枚举
     */
    public static OperationJudgeRuleEnum getByCode(String code) {
        for (OperationJudgeRuleEnum rule : values()) {
            if (rule.getCode().equals(code)) {
                return rule;
            }
        }
        return null;
    }
}
