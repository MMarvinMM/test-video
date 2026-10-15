package com.chinatower.receive.support.rule.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.receive.mapper.AlarmOperateMapper;
import com.chinatower.receive.mapper.VmAgentOperationTaskExecutionMapper;
import com.chinatower.receive.support.rule.enums.OperationJudgeRuleEnum;
import com.chinatower.receive.support.rule.interfaces.OperationJudgeSimpleRule;
import com.chinatower.receive.support.rule.model.RuleContext;
import com.chinatower.receive.vo.dto.TypeCountDTO;
import com.chinatower.receive.vo.entity.VmAgentOperationTaskExecutionDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 运营人员进入系统之后，若系统已经执行完了并且人工未处理的时候需要及时通知规则
 */
@Slf4j
@Component
public class PendingManualReviewRule implements OperationJudgeSimpleRule {

    @Autowired
    private VmAgentOperationTaskExecutionMapper vmAgentOperationTaskExecutionMapper;
    @Autowired
    private AlarmOperateMapper alarmOperateMapper;

    @Override
    public OperationJudgeRuleEnum getCode() {
        return OperationJudgeRuleEnum.PENDING_MANUAL_REVIEW;
    }

    @Override
    public boolean execute(RuleContext context) {
        /**
         1. 查询用户最新已完成的任务记录信息
         2. 查询该任务记录下，人工未处理的数据
         */
        SysUser sysUser = context.getSysUser();
        String userId = sysUser.getUserId();
        VmAgentOperationTaskExecutionDO taskExecutionDO = vmAgentOperationTaskExecutionMapper.getLatestEndTaskExecutionByUserId(userId);
        if (Objects.isNull(taskExecutionDO)) {
            return Boolean.FALSE;
        }
        Long executionId = taskExecutionDO.getExecutionId();
        Long notManualCount = alarmOperateMapper.countByExecutionIdAndNotManual(executionId);
        if (notManualCount == 0) {
            // 不存在人工未处理的数据
            return Boolean.FALSE;
        }
        /**
         * A.单次执行：“您在[年月日时分]进行了告警自动研判，本次共处理n条，其中真警n、假警n、未研判n，请及时确认结果哦~”。
         * B.执行任务：“[X日至Y日]执行违建告警自动研判处理结果，本次共处理n条，其中真警n、假警n、未研判n，请及时确认结果哦~。其中X代表研判结果中处理日期最早的时间，Y代表最晚的，如果X和Y是一天，就只展示X日。
         */
        String notificationMessage = "";

        if ("1".equals(taskExecutionDO.getTaskExecutionType())) {
            // 单次任务
            Date createTime = taskExecutionDO.getCreateTime();
            List<Long> executionIds = Lists.newArrayList();
            executionIds.add(executionId);
            List<TypeCountDTO> typeCountDTOS = alarmOperateMapper.groupByJudgeResultCount(executionIds);

            // 拼装单次任务提示语句
            notificationMessage = buildSingleTaskNotificationMessage(createTime, typeCountDTOS);

        } else if ("2".equals(taskExecutionDO.getTaskExecutionType())) {
            // 持续任务
            String groupId = taskExecutionDO.getGroupId();
            List<VmAgentOperationTaskExecutionDO> taskExecutionByGroupId = vmAgentOperationTaskExecutionMapper.getTaskExecutionByGroupId(groupId);
            List<Long> executionIds = taskExecutionByGroupId.stream().map(VmAgentOperationTaskExecutionDO::getExecutionId).collect(Collectors.toList());
            List<TypeCountDTO> typeCountDTOS = alarmOperateMapper.groupByJudgeResultCount(executionIds);

            // 拼装持续任务提示语句
            notificationMessage = buildContinuousTaskNotificationMessage(taskExecutionByGroupId, typeCountDTOS);
        } else {
            log.error("执行类型错误");
            return false;
        }

        // 将提示信息存储到上下文中，供后续通知使用
        context.putExtendInfo("message", notificationMessage);
        context.putExtendInfo("taskExecutionType", taskExecutionDO.getTaskExecutionType());
        log.info("生成通知消息: {}", notificationMessage);
        return true;
    }

    /**
     * 拼装单次任务通知消息
     * 格式：您在[年月日时分]进行了告警自动研判，本次共处理n条，其中真警n、假警n、未研判n，请及时确认结果哦~
     *
     * @param createTime    创建时间
     * @param typeCountDTOS 类型统计列表
     * @return 通知消息
     */
    private String buildSingleTaskNotificationMessage(Date createTime, List<TypeCountDTO> typeCountDTOS) {
        // 格式化时间：年月日时分
        String timeStr = DateUtil.format(createTime, "yyyy年MM月dd日HH时mm分");

        // 统计各类型数量
        int trueAlarmCount = 0;  // 真警数量
        int falseAlarmCount = 0; // 假警数量
        int unjudgedCount = 0;   // 未研判数量

        for (TypeCountDTO typeCount : typeCountDTOS) {
            String type = typeCount.getType();
            Integer count = typeCount.getCount();

            if ("true".equals(type)) {
                trueAlarmCount = count;
            } else if ("false".equals(type)) {
                falseAlarmCount = count;
            } else if ("unjudged".equals(type)) {
                unjudgedCount = count;
            }
        }

        // 计算总处理数量
        int totalCount = trueAlarmCount + falseAlarmCount + unjudgedCount;

        // 拼装消息
        return String.format("您在%s进行了告警自动研判，本次共处理%d条，其中真警%d、假警%d、未研判%d，请及时确认结果哦~",
                timeStr, totalCount, trueAlarmCount, falseAlarmCount, unjudgedCount);
    }

    /**
     * 拼装持续任务通知消息
     * 格式：[X日至Y日]执行违建告警自动研判处理结果，本次共处理n条，其中真警n、假警n、未研判n，请及时确认结果哦~
     * 其中X代表研判结果中处理日期最早的时间，Y代表最晚的，如果X和Y是一天，就只展示X日
     *
     * @param taskExecutionList 任务执行记录列表
     * @param typeCountDTOS     类型统计列表
     * @return 通知消息
     */
    private String buildContinuousTaskNotificationMessage(List<VmAgentOperationTaskExecutionDO> taskExecutionList,
                                                          List<TypeCountDTO> typeCountDTOS) {
        // 获取最早和最晚的创建时间
        LocalDateTime earliestTime = taskExecutionList.stream()
                .map(t-> LocalDateTimeUtil.of(t.getCreateTime()))
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        LocalDateTime latestTime = taskExecutionList.stream()
                .map(t-> LocalDateTimeUtil.of(t.getCreateTime()))
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        // 格式化时间范围
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM月dd日");
        String timeRangeStr;

        if (earliestTime.toLocalDate().equals(latestTime.toLocalDate())) {
            // 同一天，只显示日期
            timeRangeStr = earliestTime.format(dateFormatter);
        } else {
            // 不同天，显示时间范围
            timeRangeStr = earliestTime.format(dateFormatter) + "至" + latestTime.format(dateFormatter);
        }

        // 统计各类型数量
        int trueAlarmCount = 0;  // 真警数量
        int falseAlarmCount = 0; // 假警数量
        int unjudgedCount = 0;   // 未研判数量

        for (TypeCountDTO typeCount : typeCountDTOS) {
            String type = typeCount.getType();
            Integer count = typeCount.getCount();

            if ("true".equals(type)) {
                trueAlarmCount = count;
            } else if ("false".equals(type)) {
                falseAlarmCount = count;
            } else if ("unjudged".equals(type)) {
                unjudgedCount = count;
            }
        }

        // 计算总处理数量
        int totalCount = trueAlarmCount + falseAlarmCount + unjudgedCount;

        // 拼装消息
        return String.format("%s执行违建告警自动研判处理结果，本次共处理%d条，其中真警%d、假警%d、未研判%d，请及时确认结果哦~",
                timeRangeStr, totalCount, trueAlarmCount, falseAlarmCount, unjudgedCount);
    }
}
