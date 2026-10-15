package com.chinatower.receive.core.alarm.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Spec：工单持久化实际触发子任务编码（Task 3 — 写路径填充）。
 * 覆盖：大模型多子任务写入 / 非大模型置空 / null 安全。
 */
public class SubScenesWriteHelperTest {

    private static final String WARNING_TYPE_ID = "10313124";
    private static final String SUB_TYPE_A = "10212138";
    private static final String SUB_TYPE_B = "10212139";

    /**
     * 场景：多子任务写入多个编码
     * WHEN 大模型场景告警实际触发子任务 A、B（extra_field 已裁剪）
     * THEN VO.subScenes 仅含 A、B（不合并场景主类型）
     */
    @Test
    public void shouldFillMultipleTriggeredSubScenesFromExtraField() {
        ReceiveAlarmVO vo = new ReceiveAlarmVO();
        vo.setAlarmType(WARNING_TYPE_ID);
        vo.setExtraField(buildCvlmExtraField(
                triggeredSubAlarm(SUB_TYPE_A),
                triggeredSubAlarm(SUB_TYPE_B)
        ));

        SubScenesWriteHelper.fillSubScenes(vo);

        List<String> codes = JSON.parseArray(vo.getSubScenes(), String.class);
        assertEquals(2, codes.size());
        assertTrue(!codes.contains(WARNING_TYPE_ID));
        assertTrue(codes.contains(SUB_TYPE_A));
        assertTrue(codes.contains(SUB_TYPE_B));
    }

    /**
     * 场景：非大模型不写 sub_scenes
     * WHEN 普通 AI 告警落库（extra_field 为空）
     * THEN VO.subScenes 为 null
     */
    @Test
    public void shouldLeaveSubScenesNullWhenExtraFieldBlank() {
        ReceiveAlarmVO vo = new ReceiveAlarmVO();
        vo.setAlarmType(WARNING_TYPE_ID);
        vo.setExtraField(null);

        SubScenesWriteHelper.fillSubScenes(vo);

        assertNull(vo.getSubScenes());
    }

    /**
     * 场景：解析失败不兜底
     * WHEN extra_field 非法 JSON
     * THEN VO.subScenes 为 null，且不抛异常
     */
    @Test
    public void shouldLeaveSubScenesNullWhenExtraFieldIllegalJson() {
        ReceiveAlarmVO vo = new ReceiveAlarmVO();
        vo.setAlarmType(WARNING_TYPE_ID);
        vo.setExtraField("{not-a-json-array");

        SubScenesWriteHelper.fillSubScenes(vo);

        assertNull(vo.getSubScenes());
    }

    /**
     * null VO 不得抛异常。
     */
    @Test
    public void shouldIgnoreNullVoSafely() {
        SubScenesWriteHelper.fillSubScenes(null);
    }

    /**
     * alarmType 为空且无 extra_field：subScenes 为 null，且不抛异常。
     */
    @Test
    public void shouldHandleBlankAlarmTypeWithoutThrowing() {
        ReceiveAlarmVO vo = new ReceiveAlarmVO();
        vo.setAlarmType(null);
        vo.setExtraField(null);

        SubScenesWriteHelper.fillSubScenes(vo);

        assertNull(vo.getSubScenes());
    }

    // ---------- 夹具（与 SubScenesExtractUtilTest 对齐） ----------

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

    private static String buildCvlmExtraField(String... subAlarmJsonObjects) {
        JSONArray attrValueArray = new JSONArray();
        for (String sub : subAlarmJsonObjects) {
            attrValueArray.add(JSON.parseObject(sub));
        }
        JSONObject attr = new JSONObject();
        attr.put("attrType", "CVLM-ALARM-INFO");
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
