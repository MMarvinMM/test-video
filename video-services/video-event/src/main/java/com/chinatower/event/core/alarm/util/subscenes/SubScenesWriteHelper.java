package com.chinatower.event.core.alarm.util.subscenes;

import com.chinatower.event.entity.vo.ReceiveAlarmVO;

/**
 * 运营研判写路径：为 ReceiveAlarmVO 填充 subScenes（委托 SubScenesExtractUtil）。
 * <p>
 * 与 video-receive SubScenesWriteHelper 同源；非大模型 / 解析失败时 subScenes 为 null。
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
        vo.setSubScenes(SubScenesExtractUtil.extractSubScenes(
                vo.getAlarmType(), vo.getExtraField(), null));
    }
}
