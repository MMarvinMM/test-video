package com.chinatower.receive.support.rule.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.AlarmOperateDTO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.receive.service.OperateService;
import com.chinatower.receive.support.rule.enums.OperationJudgeRuleEnum;
import com.chinatower.receive.support.rule.interfaces.OperationJudgeSimpleRule;
import com.chinatower.receive.support.rule.model.RuleContext;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 待研判数量阈值规则
 */
@Slf4j
@Component
public class PendingJudgeCountThresholdRule implements OperationJudgeSimpleRule {

    @Autowired
    private OperateService operateService;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Value("${operation.judge.pending.count.threshold:5}")
    private int threshold;

    private static final String OPERATE_ALARM_COUNT_FIELD = "operateAlarmCount";

    @Override
    public OperationJudgeRuleEnum getCode() {
        return OperationJudgeRuleEnum.PENDING_JUDGE_COUNT_THRESHOLD;
    }

    @Override
    public boolean execute(RuleContext ruleContext) {
        log.info("开始执行待研判数量阈值规则，ruleContext = {}", JSON.toJSONString(ruleContext));
        SysUser sysUser = ruleContext.getSysUser();
        // 获取该用户待研判告警数量
        AlarmOperateDTO alarmOperateDTO = buildAlarmOperateDTO(sysUser);
        Integer operateAlarmCount = operateService.getOperateAlarmCount(alarmOperateDTO);
        if (operateAlarmCount <= threshold) {
            // 再多查一个月，防止月初没告警的情况
            alarmOperateDTO.setAlarmTimeStart(DateUtils.dateToStringNoMS(DateUtil.beginOfMonth(DateUtil.lastMonth())));
            alarmOperateDTO.setAlarmTimeEnd(DateUtils.dateToStringNoMS(DateUtil.endOfMonth(DateUtil.lastMonth())));
            Integer operateAlarmCountLastMonth = operateService.getOperateAlarmCount(alarmOperateDTO);
            operateAlarmCount += operateAlarmCountLastMonth;
        }
        // 满足大于5或者数量等于5、10、20时的规则 && (operateAlarmCount == 5 || operateAlarmCount == 10 || operateAlarmCount == 20)
        boolean result;
        if (Objects.nonNull(ruleContext.getGtFiveFlag()) && ruleContext.getGtFiveFlag()) {
            result = (operateAlarmCount == 5 || operateAlarmCount == 10 || operateAlarmCount == 20);
        } else {
            result = (operateAlarmCount >= threshold);
        }
        log.info("待研判数量阈值规则，用户：{}，待研判数量：{},规则通过结果：{}", sysUser.getUserId(), operateAlarmCount, result);
        ruleContext.putExtendInfo(OPERATE_ALARM_COUNT_FIELD, operateAlarmCount);
        return result;
    }

    /**
     * 构建告警操作DTO
     *
     * @return 告警操作DTO
     */
    private AlarmOperateDTO buildAlarmOperateDTO(SysUser user) {
        AlarmOperateDTO alarmOperateDTO = new AlarmOperateDTO();
        alarmOperateDTO.setAlarmTimeStart(DateUtils.dateToStringNoMS(DateUtils.getCurrentMonthFirstTime()));
        alarmOperateDTO.setAlarmTimeEnd(DateUtils.dateToStringNoMS(DateUtils.getCurrentMonthEndTime()));
        alarmOperateDTO.setLocalDept(user.getDeptId());
        // 从字典获取告警类型ID列表
        List<String> warningTypeIdList = getWarningTypeIdsFromDict();
        alarmOperateDTO.setWarningTypeIdList(warningTypeIdList);
        List<String> orderStatusList = new ArrayList<>();
        orderStatusList.add(Constants.JUDGE_RESULT_3);// 待研判
        alarmOperateDTO.setOrderStatusList(orderStatusList);
        return alarmOperateDTO;
    }

    /**
     * 从字典获取告警类型ID列表
     *
     * @return 告警类型ID列表
     */
    private List<String> getWarningTypeIdsFromDict() {
        try {
            // 违建告警类型
            String dictType = "ai_operate_alarm_type"; // 需要根据实际字典类型调整

            Result<List<SysDictDataResponse>> dictResult = systemFeignClient.dictDataType(dictType);
            List<String> warningTypeIdList = new ArrayList<>();

            if (dictResult.getCode() == 200 && dictResult.getData() != null && !dictResult.getData().isEmpty()) {
                for (SysDictDataResponse dictData : dictResult.getData()) {
                    if ("0".equals(dictData.getStatus())) { // 只取启用状态的字典
                        warningTypeIdList.add(dictData.getDictValue());
                    }
                }
                log.info("从字典获取告警类型ID列表：{}", warningTypeIdList);
            } else {
                // 如果字典中没有配置，使用默认值
                warningTypeIdList.add("1717113");
                log.warn("字典中未找到告警类型配置，使用默认值：{}", warningTypeIdList);
            }
            return warningTypeIdList;

        } catch (Exception e) {
            log.error("从字典获取告警类型ID列表异常", e);
            // 异常时返回默认值
            return Arrays.asList("1717113");
        }
    }
}
