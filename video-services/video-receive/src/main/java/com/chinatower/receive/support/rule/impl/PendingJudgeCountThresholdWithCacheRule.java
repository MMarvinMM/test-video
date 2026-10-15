package com.chinatower.receive.support.rule.impl;

import cn.hutool.core.util.StrUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.AlarmOperateDTO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.receive.service.OperateService;
import com.chinatower.receive.support.rule.enums.OperationJudgeRuleEnum;
import com.chinatower.receive.support.rule.interfaces.OperationJudgeSimpleRule;
import com.chinatower.receive.support.rule.model.RuleContext;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 待研判数量阈值规则(带缓存),当违建告警待研判的数量＞5的时候触发询问（每天一次）
 */
@Slf4j
@Component
public class PendingJudgeCountThresholdWithCacheRule extends PendingJudgeCountThresholdRule implements OperationJudgeSimpleRule {


    private static final String operate_enter_system = "operate_enter_system:";

    @Override
    public OperationJudgeRuleEnum getCode() {
        return OperationJudgeRuleEnum.PENDING_JUDGE_COUNT_THRESHOLD_WITH_CACHE;
    }

    @Override
    public boolean execute(RuleContext ruleContext) {
        SysUser sysUser = ruleContext.getSysUser();
        String today = DateUtils.dateToStringYMD(new Date());
        String redisKey = operate_enter_system + sysUser.getUserId() + ":" + today;
        String cacheResult = RedisUtil.get(redisKey, RedisDBType.PRIMARY);
        if (StrUtil.isNotBlank(cacheResult)) {
            log.info("用户：{}，今天已触发违建告警待研判的数量阈值，不再重复计算待研判数量", sysUser.getUserId());
            return false;
        }
        boolean result = super.execute(ruleContext);
        if (result) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX); // 当天23:59:59.999999999
            long secondsUntilEndOfDay = ChronoUnit.SECONDS.between(now, endOfDay);
            RedisUtil.set(redisKey, "1", secondsUntilEndOfDay, RedisDBType.PRIMARY);
        }
        return result;
    }


}
