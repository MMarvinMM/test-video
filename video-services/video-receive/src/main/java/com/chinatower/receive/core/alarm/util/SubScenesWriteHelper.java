package com.chinatower.receive.core.alarm.util;

import com.chinatower.event.entity.vo.ReceiveAlarmVO;

/**
 * 接警写路径：为 ReceiveAlarmVO 填充 subScenes（委托 SubScenesExtractUtil）。
 * <p>
 * 大模型场景在 Kafka 发送前调用；普通告警在 insert 前调用。
 * 非大模型 / 解析失败时 subScenes 为 null，不按告警类型兜底。
 * </p>
 */
public final class SubScenesWriteHelper {

    private SubScenesWriteHelper() {
    }

    /**
     * 根据 alarmType + extraField 提取并写入 subScenes。
     *
     * @param vo 告警 VO；null 时忽略
     */
    public static void fillSubScenes(ReceiveAlarmVO vo) {
        if (vo == null) {
            return;
        }
        // 与方案 G 一致：mapper 传 null，alarmType 即 type_value
        vo.setSubScenes(SubScenesExtractUtil.extractSubScenes(
                vo.getAlarmType(), vo.getExtraField(), null));
    }
}
