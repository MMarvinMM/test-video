package com.chinatower.receive.support.rule.impl;

import cn.hutool.core.util.StrUtil;
import com.chinatower.event.util.DateUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.receive.support.rule.enums.OperationJudgeRuleEnum;
import com.chinatower.receive.support.rule.interfaces.OperationJudgeSimpleRule;
import com.chinatower.receive.support.rule.model.RuleContext;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 每个账号每天进入该页面次数规则
 */
@Slf4j
@Component
public class PageOpenCountThresholdRule implements OperationJudgeSimpleRule {

    @Value("${operation.judge.page.count.threshold:3}")
    private int threshold;

    private static final String operate_page_access = "operate_page_access:";

    @Override
    public OperationJudgeRuleEnum getCode() {
        return OperationJudgeRuleEnum.PAGE_OPEN_COUNT_THRESHOLD;
    }

    @Override
    public boolean execute(RuleContext context) {
        // 调用接口查询该用户当天进入该页面的次数
        SysUser sysUser = context.getSysUser();
        String today = DateUtils.dateToStringYMD(new Date());
        String redisKey = operate_page_access + sysUser.getUserId() + ":" + today;
        String currentCountStr = RedisUtil.get(redisKey, RedisDBType.PRIMARY);
        int currentCount = 0;
        if (StrUtil.isNotBlank(currentCountStr)) {
            currentCount = Integer.parseInt(currentCountStr);
        }
        return currentCount <= threshold;
    }
}
