package com.chinatower.receive.core.alarm.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Spec：工单持久化实际触发子任务编码（Task 2 — SubScenesExtractUtil）。
 * 覆盖：多子任务写入多个编码 / 非大模型置空 / 解析失败置空，以及无有效检测框过滤。
 */
public class SubScenesExtractUtilTest {

    private static final String WARNING_TYPE_ID = "10313124";
    private static final String SUB_TYPE_A = "10212138";
    private static final String SUB_TYPE_B = "10212139";

    /**
     * 场景：多子任务写入多个编码
     * WHEN 大模型场景告警实际触发子任务 A、B
     * THEN sub_scenes 仅含 A、B（不合并场景主类型 warning_type_id）
     */
    @Test
    public void shouldExtractMultipleTriggeredSubScenesAsJsonArray() {
        String extraField = buildCvlmExtraField(
                triggeredSubAlarm(SUB_TYPE_A),
                triggeredSubAlarm(SUB_TYPE_B)
        );

        String subScenes = SubScenesExtractUtil.extractSubScenes(WARNING_TYPE_ID, extraField, null);

        List<String> codes = JSON.parseArray(subScenes, String.class);
        assertEquals("应仅包含两个实际触发下游任务", 2, codes.size());
        assertTrue("不得混入场景主类型", !codes.contains(WARNING_TYPE_ID));
        assertTrue("应包含子任务 A", codes.contains(SUB_TYPE_A));
        assertTrue("应包含子任务 B", codes.contains(SUB_TYPE_B));
    }

    /**
     * 场景：非大模型不写 sub_scenes
     * WHEN 普通 AI 告警落库（extra_field 为空）
     * THEN sub_scenes 为 null（不按告警类型兜底）
     */
    @Test
    public void shouldReturnNullWhenExtraFieldBlankForNonLlm() {
        assertNull(SubScenesExtractUtil.extractSubScenes(WARNING_TYPE_ID, null, null));
        assertNull(SubScenesExtractUtil.extractSubScenes(WARNING_TYPE_ID, "   ", null));
    }

    /**
     * 场景：解析失败不兜底
     * WHEN extra_field 为非法 JSON
     * THEN sub_scenes 为 null，且不抛异常
     */
    @Test
    public void shouldReturnNullWhenExtraFieldIllegalJson() {
        assertNull(SubScenesExtractUtil.extractSubScenes(WARNING_TYPE_ID, "{not-a-json-array", null));
    }

    /**
     * 无有效检测框的子告警不得写入（实际触发规则）。
     */
    @Test
    public void shouldIgnoreSubAlarmWithoutAlarmPointList() {
        String extraField = buildCvlmExtraField(
                triggeredSubAlarm(SUB_TYPE_A),
                subAlarmWithoutPoints(SUB_TYPE_B)
        );

        String subScenes = SubScenesExtractUtil.extractSubScenes(WARNING_TYPE_ID, extraField, null);

        List<String> codes = JSON.parseArray(subScenes, String.class);
        assertEquals(1, codes.size());
        assertEquals(SUB_TYPE_A, codes.get(0));
        assertTrue("不得混入场景主类型", !codes.contains(WARNING_TYPE_ID));
    }

    /**
     * 可选 typeValueMapper：对 alarmType 做映射后写入；null 时 identity。
     */
    @Test
    public void shouldApplyTypeValueMapperWhenProvided() {
        String mapped = "MAPPED_" + SUB_TYPE_A;
        String extraField = buildCvlmExtraField(triggeredSubAlarm(SUB_TYPE_A));
        Function<String, String> mapper = alarmType -> "MAPPED_" + alarmType;

        String subScenes = SubScenesExtractUtil.extractSubScenes(WARNING_TYPE_ID, extraField, mapper);

        List<String> codes = JSON.parseArray(subScenes, String.class);
        assertEquals(1, codes.size());
        assertEquals(mapped, codes.get(0));
        assertTrue("不得混入场景主类型", !codes.contains(WARNING_TYPE_ID));
    }

    /**
     * 去重：实际触发编码重复时只保留一次；若触发编码恰等于主类型，仍可作为下游写入一次。
     */
    @Test
    public void shouldDedupeTriggeredSubAlarmTypes() {
        String extraField = buildCvlmExtraField(
                triggeredSubAlarm(SUB_TYPE_A),
                triggeredSubAlarm(SUB_TYPE_A)
        );

        String subScenes = SubScenesExtractUtil.extractSubScenes(WARNING_TYPE_ID, extraField, null);

        List<String> codes = JSON.parseArray(subScenes, String.class);
        assertEquals(1, codes.size());
        assertEquals(SUB_TYPE_A, codes.get(0));
    }

    /**
     * WHEN 实际触发下游编码与场景主类型 type_value 相同
     * THEN 仍写入该编码（来自触发，非合并主场景）
     */
    @Test
    public void shouldKeepTriggeredCodeEvenIfEqualsWarningTypeId() {
        String extraField = buildCvlmExtraField(triggeredSubAlarm(WARNING_TYPE_ID));

        String subScenes = SubScenesExtractUtil.extractSubScenes(WARNING_TYPE_ID, extraField, null);

        List<String> codes = JSON.parseArray(subScenes, String.class);
        assertEquals(1, codes.size());
        assertEquals(WARNING_TYPE_ID, codes.get(0));
    }

    // ---------- 测试夹具：构造 CVLM extra_field ----------

    private static String triggeredSubAlarm(String alarmType) {
        return "{"
                + "\"alarmType\":\"" + alarmType + "\","
                + "\"alarmAreaList\":[{"
                + "\"alarmArea\":[{"
                + "\"alarmAreaId\":\"area-001\","
                + "\"alarmPointList\":[{\"percentX\":\"0.1\",\"percentY\":\"0.2\"}],"
                + "\"confidenceLevel\":\"0.85\""
                + "}]"
                + "}]"
                + "}";
    }

    private static String subAlarmWithoutPoints(String alarmType) {
        return "{"
                + "\"alarmType\":\"" + alarmType + "\","
                + "\"alarmAreaList\":[{"
                + "\"alarmArea\":[{"
                + "\"alarmAreaId\":\"area-002\","
                + "\"alarmPointList\":[],"
                + "\"confidenceLevel\":\"0.50\""
                + "}]"
                + "}]"
                + "}";
    }

    /**
     * 构造与附录一致的 CVLM 外层结构；attrValue 为字符串化子告警数组。
     */
    private static String buildCvlmExtraField(String... subAlarmJsonObjects) {
        JSONArray attrValueArray = new JSONArray();
        for (String sub : subAlarmJsonObjects) {
            attrValueArray.add(JSON.parseObject(sub));
        }
        JSONObject attr = new JSONObject();
        attr.put("attrType", "CVLM-ALARM-INFO");
        // 贴近生产：attrValue 常为字符串化 JSON 数组
        attr.put("attrValue", attrValueArray.toJSONString());

        JSONArray attrList = new JSONArray();
        attrList.add(attr);

        JSONObject outer = new JSONObject();
        outer.put("alarmAreaId", "CVLM");
        outer.put("attrList", attrList);

        JSONArray root = new JSONArray();
        root.add(outer);
        return root.toJSONString();
    }
}
