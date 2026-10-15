package com.chinatower.receive.aijudge;

import com.alibaba.fastjson.JSON;
import com.chinatower.receive.model.AiJudgeCallbackReq;
import org.springframework.util.StringUtils;

/**
 * 智能体扩展字段组装 / 合并
 */
public final class AiAgentExtendHelper {

    private AiAgentExtendHelper() {
    }

    /**
     * 从回调入参组装扩展字段（驼峰 JSON），研判依据仅取 judgment_basis
     */
    public static AiAgentExtendVO buildFromCallback(AiJudgeCallbackReq req) {
        AiAgentExtendVO extend = new AiAgentExtendVO();
        if (req == null) {
            return extend;
        }
        extend.setRiskLevel(req.getRisk_level() == null ? null : String.valueOf(req.getRisk_level()));
        extend.setSurroundings(req.getSurroundings());
        extend.setJudgmentBasis(req.getJudgment_basis());
        return extend;
    }

    /**
     * 回调回填时与库中已有扩展字段合并：回调非空覆盖，空值保留原气象等字段
     */
    public static AiAgentExtendVO mergeFromCallback(String existingJson, AiJudgeCallbackReq req) {
        AiAgentExtendVO merged = parse(existingJson);
        if (merged == null) {
            merged = new AiAgentExtendVO();
        }
        AiAgentExtendVO fromCallback = buildFromCallback(req);
        if (StringUtils.hasText(fromCallback.getRiskLevel())) {
            merged.setRiskLevel(fromCallback.getRiskLevel());
        }
        if (StringUtils.hasText(fromCallback.getHumidity())) {
            merged.setHumidity(fromCallback.getHumidity());
        }
        if (StringUtils.hasText(fromCallback.getTemperature())) {
            merged.setTemperature(fromCallback.getTemperature());
        }
        if (StringUtils.hasText(fromCallback.getWindSpeed())) {
            merged.setWindSpeed(fromCallback.getWindSpeed());
        }
        if (StringUtils.hasText(fromCallback.getWindDir())) {
            merged.setWindDir(fromCallback.getWindDir());
        }
        if (fromCallback.getFireLevel() != null) {
            merged.setFireLevel(fromCallback.getFireLevel());
        }
        if (StringUtils.hasText(fromCallback.getSurroundings())) {
            merged.setSurroundings(fromCallback.getSurroundings());
        }
        if (StringUtils.hasText(fromCallback.getJudgmentBasis())) {
            merged.setJudgmentBasis(fromCallback.getJudgmentBasis());
        }
        return merged;
    }

    public static AiAgentExtendVO parse(String agentExtendField) {
        if (!StringUtils.hasText(agentExtendField) || "null".equals(agentExtendField)) {
            return null;
        }
        try {
            return JSON.parseObject(agentExtendField, AiAgentExtendVO.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static String toJson(AiAgentExtendVO extend) {
        return JSON.toJSONString(extend == null ? new AiAgentExtendVO() : extend);
    }
}
