package com.chinatower.event.core.alarm.util.subscenes;

import com.alibaba.fastjson.JSON;
import com.chinatower.event.entity.vo.AlarmOriginalAreaListVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 从工单 extra_field 提取实际触发下游任务编码，生成 sub_scenes JSON 数组字符串。
 * <p>
 * 与 video-receive SubScenesExtractUtil 同源，后续可抽公共模块。
 * 仅写入实际触发下游编码（LinkedHashSet 去重保序），不合并场景主类型 warningTypeId。
 * 非大模型（extra_field 为空）或解析失败 / 无实际触发：返回 null，不按告警类型兜底。
 * </p>
 */
public final class SubScenesExtractUtil {

    private static final Logger log = LoggerFactory.getLogger(SubScenesExtractUtil.class);

    private SubScenesExtractUtil() {
    }

    /**
     * 从 extra_field 提取 sub_scenes JSON 数组字符串。
     *
     * @param warningTypeId   主场景 type_value（warning_type_id）
     * @param extraField      工单 extra_field（大模型场景为裁剪后的 CVLM JSON）
     * @param typeValueMapper 可选：alarmType → type_value；null 时 identity
     * @return JSON 数组字符串；非大模型/解析失败/无触发时为 null
     */
    public static String extractSubScenes(String warningTypeId, String extraField,
                                          Function<String, String> typeValueMapper) {
        // 非大模型：extra_field 为空，sub_scenes 置空，不按告警类型兜底
        if (StringUtils.isBlank(extraField)) {
            return null;
        }
        LinkedHashSet<String> triggered = new LinkedHashSet<>();
        try {
            List<ReceiveAlarmVO> subAlarms =
                    CvlmSubAlarmParseHelper.parseSubAlarmsFromExtendField(extraField);
            for (ReceiveAlarmVO sub : subAlarms) {
                if (sub == null || !isTriggeredSubAlarm(sub)) {
                    continue;
                }
                String alarmType = sub.getAlarmType();
                if (StringUtils.isBlank(alarmType)) {
                    continue;
                }
                String typeValue = typeValueMapper != null
                        ? typeValueMapper.apply(alarmType) : alarmType;
                if (StringUtils.isNotBlank(typeValue)) {
                    triggered.add(typeValue);
                }
            }
        } catch (Exception e) {
            // 解析失败不阻断刷数：不写 sub_scenes
            log.warn("extractSubScenes parse fail, warningTypeId={}", warningTypeId, e);
            return null;
        }
        // 仅实际触发下游；warningTypeId 仅用于日志，不写入 sub_scenes
        if (triggered.isEmpty()) {
            return null;
        }
        return toJsonArray(triggered);
    }

    /**
     * 是否产生告警：alarmAreaList 内至少存在一个 alarmArea，其 alarmPointList 非空。
     */
    private static boolean isTriggeredSubAlarm(ReceiveAlarmVO sub) {
        if (CollectionUtils.isEmpty(sub.getAlarmAreaList())) {
            return false;
        }
        return sub.getAlarmAreaList().stream()
                .filter(Objects::nonNull)
                .map(AlarmOriginalAreaListVO::getAlarmArea)
                .filter(list -> !CollectionUtils.isEmpty(list))
                .flatMap(List::stream)
                .anyMatch(area -> area != null && !CollectionUtils.isEmpty(area.getAlarmPointList()));
    }

    /** LinkedHashSet 保序序列化为 JSON 数组字符串 */
    private static String toJsonArray(LinkedHashSet<String> sceneSet) {
        return JSON.toJSONString(new ArrayList<>(sceneSet));
    }
}
