package com.chinatower.receive.support.rule.interfaces;

import com.chinatower.receive.support.rule.enums.OperationJudgeRuleEnum;
import com.chinatower.receive.support.rule.model.RuleContext;

/**
 * 简化版运营研判规则接口
 */
public interface OperationJudgeSimpleRule {

    /**
     * 规则对应的枚举编码
     */
    OperationJudgeRuleEnum getCode();

    /**
     * 执行规则
     */
    boolean execute(RuleContext ruleContext);
}


