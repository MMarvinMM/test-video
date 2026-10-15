package com.chinatower.common.core.vo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Spec：工单持久化实际触发子任务编码（Task 1 — 实体字段）。
 * 验证 BaseReceiveAlarmVO 可承载月表列 sub_scenes 的 JSON 文本
 *（ReceiveAlarmVO 继承本类，接警 insert 走该字段）。
 */
public class BaseReceiveAlarmVOSubScenesFieldTest {

    /**
     * 场景：多子任务写入多个编码 — 实体可 set/get JSON 数组字符串。
     */
    @Test
    public void shouldSetAndGetSubScenesJsonArray() {
        BaseReceiveAlarmVO vo = new BaseReceiveAlarmVO();
        // 与 extraField 一致：JSON 列以 String 绑定
        String subScenesJson = "[\"10313124\",\"10212138\"]";

        vo.setSubScenes(subScenesJson);

        assertEquals("subScenes 应保持写入的 JSON 文本", subScenesJson, vo.getSubScenes());
    }

    /**
     * 场景：非大模型/解析失败兜底前 — 字段允许为空（DDL NULL）。
     */
    @Test
    public void shouldAllowNullSubScenesByDefault() {
        BaseReceiveAlarmVO vo = new BaseReceiveAlarmVO();

        assertNull("未赋值时 subScenes 应为 null", vo.getSubScenes());
    }
}
