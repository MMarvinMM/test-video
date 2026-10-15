package com.chinatower.receive.service;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.aijudge.AiAgentExtendHelper;
import com.chinatower.receive.aijudge.AiAgentExtendVO;
import com.chinatower.receive.aijudge.AiJudgeResult;
import com.chinatower.receive.aijudge.AiJudgeSceneCodeConverter;
import com.chinatower.receive.constant.AlarmConstants;
import com.chinatower.receive.constant.TableConstant;
import com.chinatower.receive.mapper.AiJudgeSceneRelMapper;
import com.chinatower.receive.mapper.WarningOrderMapper;
import com.chinatower.receive.model.AiJudgeCallbackReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 告警分级：上报标记未研判 + 回调回填
 */
@Slf4j
@Service
public class AiJudgeService {

    @Autowired
    private AiJudgeSceneRelMapper aiJudgeSceneRelMapper;
    @Autowired
    private WarningOrderMapper warningOrderMapper;

    /**
     * 告警上报成功后：命中行业+算法配置则写入研判状态=未研判，供定时任务拉取
     *
     * @param alarmList 已落库告警列表
     */
    public void markPendingAfterAlarm(List<ReceiveAlarmVO> alarmList) {
        if (CollectionUtils.isEmpty(alarmList)) {
            return;
        }
        for (ReceiveAlarmVO item : alarmList) {
            try {
                markOnePending(item);
            } catch (Exception e) {
                log.error("告警分级标记未研判异常, warningOrderId={}",
                        item == null ? null : item.getWarningOrderId(), e);
            }
        }
    }

    private void markOnePending(ReceiveAlarmVO item) {
        if (item == null || StringUtils.isEmpty(item.getWarningOrderId())) {
            return;
        }
        String sceneTemplate = resolveSceneTemplate(item);
        if (StringUtils.isEmpty(sceneTemplate)) {
            log.info("告警分级未配置行业/算法关系，不标记未研判, warningOrderId={}, industryCode={}, algorithmCode={}",
                    item.getWarningOrderId(), item.getIndustryCode(), item.getAlgorithmCode());
            return;
        }
        String tableName = TableConstant.WARN_ORDER_TEBLE + item.getWarningOrderId().substring(0, 6);
        int rows = warningOrderMapper.updateAiJudgePending(
                tableName,
                item.getWarningOrderId(),
                sceneTemplate,
                AlarmConstants.AI_JUDGE_STATUS_PENDING
        );
        // 若该告警已被收藏，同步未研判状态与场景模板
        warningOrderMapper.updateAiJudgePendingOnCollection(
                item.getWarningOrderId(),
                sceneTemplate,
                AlarmConstants.AI_JUDGE_STATUS_PENDING
        );
        log.info("告警分级标记未研判完成, warningOrderId={}, sceneTemplate={}, rows={}",
                item.getWarningOrderId(), sceneTemplate, rows);
    }

    /**
     * 接收外部 AI 研判回调并回填告警字段，状态改为研判完成/失败
     */
    public Result callback(AiJudgeCallbackReq req) {
        log.info("告警分级回调入参:{}", JSON.toJSONString(req));
        if (req == null) {
            return Result.fail("回调参数不能为空");
        }
        AiJudgeResult judgeResult = toJudgeResult(req);
        if (StringUtils.isEmpty(judgeResult.getAlarmId())) {
            return Result.fail("alarm_id不能为空");
        }
        if (judgeResult.getAlarmId().length() < 6) {
            return Result.fail("alarm_id格式不正确");
        }
        String tableName = TableConstant.WARN_ORDER_TEBLE + judgeResult.getAlarmId().substring(0, 6);
        if (!judgeResult.isSuccess()) {
            log.warn("告警分级回调业务失败, alarmId={}, error={}",
                    judgeResult.getAlarmId(), judgeResult.getErrorMsg());
            warningOrderMapper.updateAiJudgeStatus(
                    tableName,
                    judgeResult.getAlarmId(),
                    AlarmConstants.AI_JUDGE_STATUS_FAIL
            );
            return Result.fail(StringUtils.isEmpty(judgeResult.getErrorMsg())
                    ? "研判失败" : judgeResult.getErrorMsg());
        }
        // 与库中已有扩展字段（含调 Dify 前写入的气象）合并后再回填
        String existingExtend = warningOrderMapper.selectAgentExtendField(tableName, judgeResult.getAlarmId());
        AiAgentExtendVO mergedExtend = AiAgentExtendHelper.mergeFromCallback(existingExtend, req);
        String agentExtendField = AiAgentExtendHelper.toJson(mergedExtend);
        int rows = warningOrderMapper.updateAiJudgeResult(
                tableName,
                judgeResult.getAlarmId(),
                judgeResult.getRiskLevel(),
                judgeResult.getSceneCode(),
                agentExtendField,
                AlarmConstants.AI_JUDGE_STATUS_DONE
        );
        // 同步收藏表，保证我的收藏列表筛选/排序与工单一致
        warningOrderMapper.updateAiJudgeResultOnCollection(
                judgeResult.getAlarmId(),
                judgeResult.getRiskLevel(),
                judgeResult.getSceneCode(),
                agentExtendField,
                AlarmConstants.AI_JUDGE_STATUS_DONE
        );
        if (rows <= 0) {
            log.warn("告警分级回填未更新到数据, alarmId={}, tableName={}", judgeResult.getAlarmId(), tableName);
            return Result.fail("未找到对应告警或更新失败");
        }
        return Result.success("回填成功");
    }

    private AiJudgeResult toJudgeResult(AiJudgeCallbackReq req) {
        AiJudgeResult result = new AiJudgeResult();
        result.setSuccess(Boolean.TRUE.equals(req.getSuccess()));
        result.setAlarmId(req.getAlarm_id());
        result.setRiskLevel(req.getRisk_level() == null ? null : String.valueOf(req.getRisk_level()));
        // Dify 中文枚举 → 库内编码；未识别不落库
        String mappedSceneCode = AiJudgeSceneCodeConverter.toStorageCode(req.getSceneCode());
        result.setSceneCode(mappedSceneCode);
        if (StringUtils.hasText(req.getSceneCode()) && mappedSceneCode == null) {
            log.info("告警分级回调sceneCode不做映射, alarmId={}, sceneCode={}",
                    req.getAlarm_id(), req.getSceneCode());
        }
        return result;
    }

    private String resolveSceneTemplate(ReceiveAlarmVO item) {
        if (StringUtils.isEmpty(item.getIndustryCode()) || StringUtils.isEmpty(item.getAlgorithmCode())) {
            return null;
        }
        try {
            return aiJudgeSceneRelMapper.selectSceneTemplate(item.getIndustryCode(), item.getAlgorithmCode());
        } catch (Exception e) {
            log.warn("查询告警分级场景关系失败, industryCode={}, algorithmCode={}",
                    item.getIndustryCode(), item.getAlgorithmCode(), e);
            return null;
        }
    }
}
