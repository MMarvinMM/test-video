package com.chinatower.event.core.alarm.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 智能体扩展字段拆分工具：将 agentExtendField JSON 拆到独立属性，且不向前端返回原始 JSON 字段
 */
public final class AiAgentExtendFieldUtil {

    private AiAgentExtendFieldUtil() {
    }

    /**
     * 批量拆分列表 VO
     */
    public static void fillAndClearList(List<WarningOrderListVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (WarningOrderListVO vo : list) {
            fillAndClear(vo);
        }
    }

    /**
     * 列表 VO：拆分扩展字段到独立属性，并清空 agentExtendField
     */
    public static void fillAndClear(WarningOrderListVO vo) {
        if (vo == null) {
            return;
        }
        JSONObject extend = parse(vo.getAgentExtendField());
        if (extend != null) {
            if (StringUtils.isEmpty(vo.getRiskLevel())) {
                vo.setRiskLevel(extend.getString("riskLevel"));
            }
            vo.setHumidity(extend.getString("humidity"));
            vo.setTemperature(extend.getString("temperature"));
            vo.setWindSpeed(extend.getString("windSpeed"));
            vo.setWindDir(extend.getString("windDir"));
            vo.setFireLevel(extend.getInteger("fireLevel"));
            vo.setSurroundings(extend.getString("surroundings"));
            vo.setJudgmentBasis(extend.getString("judgmentBasis"));
        }
        // 不直接把扩展 JSON 返回给前端
        vo.setAgentExtendField(null);
    }

    /**
     * 详情 VO：拆分扩展字段到独立属性，并清空 agentExtendField
     */
    public static void fillAndClear(OrderInfoVO vo) {
        if (vo == null) {
            return;
        }
        JSONObject extend = parse(vo.getAgentExtendField());
        if (extend != null) {
            if (StringUtils.isEmpty(vo.getRiskLevel())) {
                vo.setRiskLevel(extend.getString("riskLevel"));
            }
            vo.setHumidity(extend.getString("humidity"));
            vo.setTemperature(extend.getString("temperature"));
            vo.setWindSpeed(extend.getString("windSpeed"));
            vo.setWindDir(extend.getString("windDir"));
            vo.setFireLevel(extend.getInteger("fireLevel"));
            vo.setSurroundings(extend.getString("surroundings"));
            vo.setJudgmentBasis(extend.getString("judgmentBasis"));
        }
        vo.setAgentExtendField(null);
    }

    private static JSONObject parse(String agentExtendField) {
        if (StringUtils.isEmpty(agentExtendField) || "null".equals(agentExtendField)) {
            return null;
        }
        try {
            return JSON.parseObject(agentExtendField);
        } catch (Exception e) {
            return null;
        }
    }
}
