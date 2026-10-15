package com.chinatower.receive.support.rule;

import com.chinatower.receive.support.rule.enums.OperationJudgeRuleEnum;
import com.chinatower.receive.support.rule.interfaces.OperationJudgeSimpleRule;
import com.chinatower.receive.support.rule.model.RuleContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营研判规则执行器
 */
@Slf4j
@Component
public class OperationJudgeRuleExecutor {

    @Autowired
    private List<OperationJudgeSimpleRule> ruleBeans;

    private final Map<String, OperationJudgeSimpleRule> codeToRule = new HashMap<>();

    @PostConstruct
    public void init() {
        for (OperationJudgeSimpleRule rule : ruleBeans) {
            codeToRule.put(rule.getCode().getCode(), rule);
        }
    }

    /**
     * 执行规则列表,全部通过时才通过
     *
     * @param ruleCodes 规则编码列表
     * @return 是否通过规则
     */
    public boolean executeRules(List<String> ruleCodes, RuleContext ruleContext) {
        log.info("开始执行规则，规则列表：{}", ruleCodes);

        for (String ruleCode : ruleCodes) {
            OperationJudgeRuleEnum ruleEnum = OperationJudgeRuleEnum.getByCode(ruleCode);
            if (ruleEnum == null) {
                log.warn("未找到规则：{}", ruleCode);
                continue;
            }
            OperationJudgeSimpleRule rule = codeToRule.get(ruleEnum.getCode());
            if (rule == null) {
                log.warn("规则未注册：{}", ruleEnum.getCode());
                continue;
            }
            boolean passed = rule.execute(ruleContext);

            if (!passed) {
                log.info("规则 {} 不通过", ruleCode);
                return false;
            }
        }
        log.info("所有规则通过");
        return true;
    }
}
