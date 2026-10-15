package com.chinatower.receive.core.alarm.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.GsonUtils;
import com.chinatower.event.entity.vo.AlarmOriginalAreaListVO;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.constant.CommonConstant;
import com.chinatower.receive.core.alarm.ModelConfigDTO.AnalysisPeriodDTO;
import com.chinatower.receive.core.alarm.ModelConfigDTO.ModelConfigItemDTO;
import com.chinatower.receive.core.alarm.ModelConfigDTO.ModelConfigPointDTO;
import com.chinatower.receive.core.alarm.ModelConfigDTO.PointsRelationDTO;
import com.chinatower.event.constants.Constants;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 大模型场景告警拆分后 extendField / extraField 处理工具
 * <p>
 * 按场景子告警类型与标绘框 monitorAreaId 过滤嵌套字段，保证 objectCount 与标绘区域一致。
 * </p>
 */
public final class LargeModelAlarmFieldSplitUtil {

    private static final Logger log = LoggerFactory.getLogger(LargeModelAlarmFieldSplitUtil.class);

    private LargeModelAlarmFieldSplitUtil() {
    }

    /**
     * 从 modelConfig 解析当前场景下的标绘区域 monitorAreaId 列表
     */
    public static List<String> extractSceneMonitorAreaIds(String modelConfig, List<String> subAlarmTypeList,
                                                          String sceneCode, List<String> drawAreaConfigCodes) {
        if (StringUtils.isEmpty(modelConfig) || CollectionUtils.isEmpty(subAlarmTypeList)
                || StringUtils.isEmpty(sceneCode) || CollectionUtils.isEmpty(drawAreaConfigCodes)) {
            return Collections.emptyList();
        }
        try {
            Type type = GsonUtils.getTypeToken(new TypeToken<List<ModelConfigItemDTO>>() {
            });
            List<ModelConfigItemDTO> configList = GsonUtils.parseObject(modelConfig, type);
            if (CollectionUtils.isEmpty(configList)) {
                return Collections.emptyList();
            }
            Set<String> monitorAreaIds = new LinkedHashSet<>();
            LocalTime now = LocalTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(CommonConstant.HH_MM_SS);

            for (ModelConfigItemDTO configItem : configList) {
                if (configItem == null || !drawAreaConfigCodes.contains(configItem.getCode())
                        || CollectionUtils.isEmpty(configItem.getPointsRelation())) {
                    continue;
                }
                collectMonitorAreaIds(configItem.getPointsRelation(), subAlarmTypeList, sceneCode, now,
                        timeFormatter, monitorAreaIds);
            }
            return new ArrayList<>(monitorAreaIds);
        } catch (Exception e) {
            log.warn("extractSceneMonitorAreaIds解析modelConfig异常,sceneCode:{}", sceneCode, e);
            return Collections.emptyList();
        }
    }

    /**
     * 按场景拆分 extendField
     */
    public static Object splitExtendField(Object extendField, List<String> subAlarmTypeList,
                                          Set<String> sceneMonitorAreaIds) {
        if (extendField == null) {
            return null;
        }
        JSONArray rootArray = parseRootArray(extendField);
        if (rootArray == null || rootArray.isEmpty()) {
            return extendField;
        }
        JSONArray newRootArray = splitCvlmRootArray(rootArray, subAlarmTypeList, sceneMonitorAreaIds);
        return newRootArray.isEmpty() ? newRootArray : newRootArray;
    }

    /**
     * 按场景拆分 extraField 字符串
     */
    public static String splitExtraField(String extraField, List<String> subAlarmTypeList,
                                         Set<String> sceneMonitorAreaIds) {
        if (StringUtils.isEmpty(extraField)) {
            return extraField;
        }
        try {
            JSONArray rootArray = JSON.parseArray(extraField);
            if (rootArray == null || rootArray.isEmpty()) {
                return extraField;
            }
            JSONArray newRootArray = splitCvlmRootArray(rootArray, subAlarmTypeList, sceneMonitorAreaIds);
            return newRootArray.toJSONString();
        } catch (Exception e) {
            log.warn("splitExtraField解析失败,返回原始值", e);
            return extraField;
        }
    }

    /**
     * 从拆分后的 extendField 解析子告警列表（与 parseExtendField 逻辑一致）
     */
    public static List<ReceiveAlarmVO> parseSubAlarmsFromExtendField(Object extendField) {
        JSONArray rootArray = parseRootArray(extendField);
        if (rootArray == null || rootArray.isEmpty()) {
            return Collections.emptyList();
        }
        List<ReceiveAlarmVO> result = new ArrayList<>();
        for (int i = 0; i < rootArray.size(); i++) {
            JSONObject outerNode = rootArray.getJSONObject(i);
            if (outerNode == null) {
                continue;
            }
            JSONArray outerAttrList = outerNode.getJSONArray(LargeModelAlarmFieldConstants.JSON_FIELD_ATTR_LIST);
            if (outerAttrList == null || outerAttrList.isEmpty()) {
                continue;
            }
            for (int j = 0; j < outerAttrList.size(); j++) {
                JSONObject attr = outerAttrList.getJSONObject(j);
                if (attr == null || !LargeModelAlarmFieldConstants.ATTR_TYPE_CVLM_ALARM_INFO.equals(
                        attr.getString(LargeModelAlarmFieldConstants.JSON_FIELD_ATTR_TYPE))) {
                    continue;
                }
                JSONArray alarmInfoArray = parseAttrValueAsArray(attr.get(LargeModelAlarmFieldConstants.JSON_FIELD_ATTR_VALUE));
                if (alarmInfoArray == null || alarmInfoArray.isEmpty()) {
                    continue;
                }
                result.addAll(GsonUtils.parseArray(alarmInfoArray.toJSONString(), ReceiveAlarmVO.class));
            }
        }
        return result;
    }

    /**
     * 将子告警 alarmAreaList 展平为 alarmArea 列表
     */
    public static List<AlarmOriginalAreaVO> flattenAlarmAreaList(List<ReceiveAlarmVO> subAlarmList) {
        if (CollectionUtils.isEmpty(subAlarmList)) {
            return Collections.emptyList();
        }
        return subAlarmList.stream()
                .map(ReceiveAlarmVO::getAlarmAreaList)
                .filter(list -> !CollectionUtils.isEmpty(list))
                .flatMap(List::stream)
                .map(AlarmOriginalAreaListVO::getAlarmArea)
                .filter(list -> !CollectionUtils.isEmpty(list))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * 合并子告警 alarmAreaList
     */
    public static List<AlarmOriginalAreaListVO> mergeAlarmAreaList(List<ReceiveAlarmVO> subAlarmList) {
        if (CollectionUtils.isEmpty(subAlarmList)) {
            return Collections.emptyList();
        }
        List<AlarmOriginalAreaListVO> merged = new ArrayList<>();
        for (ReceiveAlarmVO subAlarm : subAlarmList) {
            if (subAlarm == null || CollectionUtils.isEmpty(subAlarm.getAlarmAreaList())) {
                continue;
            }
            merged.addAll(subAlarm.getAlarmAreaList());
        }
        return merged;
    }

    private static JSONArray splitCvlmRootArray(JSONArray rootArray, List<String> subAlarmTypeList,
                                                Set<String> sceneMonitorAreaIds) {
        JSONArray newRootArray = new JSONArray();
        Set<String> subAlarmTypeSet = subAlarmTypeList == null ? Collections.emptySet() : new HashSet<>(subAlarmTypeList);

        for (int i = 0; i < rootArray.size(); i++) {
            JSONObject outerNode = rootArray.getJSONObject(i);
            if (outerNode == null) {
                continue;
            }
            JSONObject newOuterNode = JSON.parseObject(outerNode.toJSONString());
            JSONArray outerAttrList = newOuterNode.getJSONArray(LargeModelAlarmFieldConstants.JSON_FIELD_ATTR_LIST);
            if (outerAttrList == null || outerAttrList.isEmpty()) {
                continue;
            }

            for (int j = 0; j < outerAttrList.size(); j++) {
                JSONObject attr = outerAttrList.getJSONObject(j);
                if (attr == null || !LargeModelAlarmFieldConstants.ATTR_TYPE_CVLM_ALARM_INFO.equals(
                        attr.getString(LargeModelAlarmFieldConstants.JSON_FIELD_ATTR_TYPE))) {
                    continue;
                }
                JSONArray originalAlarmInfoArray = parseAttrValueAsArray(
                        attr.get(LargeModelAlarmFieldConstants.JSON_FIELD_ATTR_VALUE));
                if (originalAlarmInfoArray == null) {
                    continue;
                }
                JSONArray filteredAlarmInfoArray = filterSubAlarmArray(originalAlarmInfoArray, subAlarmTypeSet,
                        sceneMonitorAreaIds);
                Object originalAttrValue = attr.get(LargeModelAlarmFieldConstants.JSON_FIELD_ATTR_VALUE);
                Object newAttrValue = wrapAttrValue(filteredAlarmInfoArray, originalAttrValue);
                attr.put(LargeModelAlarmFieldConstants.JSON_FIELD_ATTR_VALUE, newAttrValue);
            }
            newRootArray.add(newOuterNode);
        }
        return newRootArray;
    }

    /**
     * 过滤子告警数组：按 alarmType、monitorAreaId 裁剪 alarmAreaList 与内层 extendField/extraField
     */
    private static JSONArray filterSubAlarmArray(JSONArray alarmInfoArray, Set<String> subAlarmTypeSet,
                                                 Set<String> sceneMonitorAreaIds) {
        JSONArray filteredArray = new JSONArray();
        if (alarmInfoArray == null || alarmInfoArray.isEmpty()) {
            return filteredArray;
        }
        for (int i = 0; i < alarmInfoArray.size(); i++) {
            JSONObject subAlarm = alarmInfoArray.getJSONObject(i);
            if (subAlarm == null) {
                continue;
            }
            String alarmType = subAlarm.getString(LargeModelAlarmFieldConstants.JSON_FIELD_ALARM_TYPE);
            if (!subAlarmTypeSet.contains(alarmType)) {
                continue;
            }
            JSONObject filteredSubAlarm = JSON.parseObject(subAlarm.toJSONString());
            filterSubAlarmAreas(filteredSubAlarm, sceneMonitorAreaIds);
            filterSubAlarmNestedFields(filteredSubAlarm, sceneMonitorAreaIds);
            filteredArray.add(filteredSubAlarm);
        }
        return filteredArray;
    }

    /**
     * 过滤子告警 alarmAreaList 中 alarmArea，仅保留 alarmAreaId 匹配标绘框的项
     */
    private static void filterSubAlarmAreas(JSONObject subAlarm, Set<String> sceneMonitorAreaIds) {
        if (CollectionUtils.isEmpty(sceneMonitorAreaIds)) {
            return;
        }
        JSONArray alarmAreaList = subAlarm.getJSONArray(LargeModelAlarmFieldConstants.JSON_FIELD_ALARM_AREA_LIST);
        if (alarmAreaList == null || alarmAreaList.isEmpty()) {
            return;
        }
        JSONArray newAlarmAreaList = new JSONArray();
        for (int i = 0; i < alarmAreaList.size(); i++) {
            JSONObject areaListItem = alarmAreaList.getJSONObject(i);
            if (areaListItem == null) {
                continue;
            }
            JSONArray alarmArea = areaListItem.getJSONArray(LargeModelAlarmFieldConstants.JSON_FIELD_ALARM_AREA);
            JSONArray filteredAlarmArea = filterAlarmAreaByMonitorAreaId(alarmArea, sceneMonitorAreaIds);
            if (filteredAlarmArea == null || filteredAlarmArea.isEmpty()) {
                continue;
            }
            JSONObject newAreaListItem = JSON.parseObject(areaListItem.toJSONString());
            newAreaListItem.put(LargeModelAlarmFieldConstants.JSON_FIELD_ALARM_AREA, filteredAlarmArea);
            newAlarmAreaList.add(newAreaListItem);
        }
        subAlarm.put(LargeModelAlarmFieldConstants.JSON_FIELD_ALARM_AREA_LIST, newAlarmAreaList);
    }

    /**
     * 过滤子告警内层 extendField / extraField
     */
    private static void filterSubAlarmNestedFields(JSONObject subAlarm, Set<String> sceneMonitorAreaIds) {
        Object innerExtendField = subAlarm.get(LargeModelAlarmFieldConstants.JSON_FIELD_EXTEND_FIELD);
        if (innerExtendField != null) {
            JSONArray filteredInnerExtend = filterInnerFieldByMonitorAreaId(
                    parseInnerFieldArray(innerExtendField), sceneMonitorAreaIds);
            subAlarm.put(LargeModelAlarmFieldConstants.JSON_FIELD_EXTEND_FIELD, filteredInnerExtend);
        }
        Object innerExtraField = subAlarm.get(LargeModelAlarmFieldConstants.JSON_FIELD_EXTRA_FIELD);
        if (innerExtraField != null) {
            JSONArray filteredInnerExtra = filterInnerFieldByMonitorAreaId(
                    parseInnerFieldArray(innerExtraField), sceneMonitorAreaIds);
            if (innerExtraField instanceof String) {
                subAlarm.put(LargeModelAlarmFieldConstants.JSON_FIELD_EXTRA_FIELD, filteredInnerExtra.toJSONString());
            } else {
                subAlarm.put(LargeModelAlarmFieldConstants.JSON_FIELD_EXTRA_FIELD, filteredInnerExtra);
            }
        }
    }

    /**
     * 内层 extendField/extraField：alarmAreaId 与标绘框 monitorAreaId 相等则保留 attrList
     */
    private static JSONArray filterInnerFieldByMonitorAreaId(JSONArray innerFieldArray,
                                                             Set<String> sceneMonitorAreaIds) {
        if (innerFieldArray == null || innerFieldArray.isEmpty()) {
            return innerFieldArray == null ? new JSONArray() : innerFieldArray;
        }
        if (CollectionUtils.isEmpty(sceneMonitorAreaIds)) {
            return innerFieldArray;
        }
        JSONArray filtered = new JSONArray();
        for (int i = 0; i < innerFieldArray.size(); i++) {
            JSONObject item = innerFieldArray.getJSONObject(i);
            if (item == null) {
                continue;
            }
            String alarmAreaId = item.getString(LargeModelAlarmFieldConstants.JSON_FIELD_ALARM_AREA_ID);
            if (sceneMonitorAreaIds.contains(alarmAreaId)) {
                filtered.add(JSON.parseObject(item.toJSONString()));
            }
        }
        return filtered;
    }

    private static JSONArray filterAlarmAreaByMonitorAreaId(JSONArray alarmArea, Set<String> sceneMonitorAreaIds) {
        if (alarmArea == null || alarmArea.isEmpty()) {
            return new JSONArray();
        }
        JSONArray filtered = new JSONArray();
        for (int i = 0; i < alarmArea.size(); i++) {
            JSONObject area = alarmArea.getJSONObject(i);
            if (area == null) {
                continue;
            }
            String alarmAreaId = area.getString(LargeModelAlarmFieldConstants.JSON_FIELD_ALARM_AREA_ID);
            if (sceneMonitorAreaIds.contains(alarmAreaId)) {
                filtered.add(JSON.parseObject(area.toJSONString()));
            }
        }
        return filtered;
    }

    private static void collectMonitorAreaIds(List<PointsRelationDTO> pointsRelation, List<String> subAlarmTypeList,
                                              String sceneCode, LocalTime now, DateTimeFormatter timeFormatter,
                                              Set<String> monitorAreaIds) {
        for (PointsRelationDTO relation : pointsRelation) {
            if (relation == null || StringUtils.isEmpty(relation.getItemCode())) {
                continue;
            }
            if (!subAlarmTypeList.contains(relation.getItemCode())) {
                continue;
            }
            if (!StringUtils.isEmpty(relation.getAnalysisPeriod())
                    && !isInAnyPeriod(now, relation.getAnalysisPeriod(), timeFormatter)) {
                continue;
            }
            if (StringUtils.isEmpty(relation.getPoints())) {
                continue;
            }
            List<ModelConfigPointDTO> points = GsonUtils.parseArray(relation.getPoints(), ModelConfigPointDTO.class);
            if (CollectionUtils.isEmpty(points)) {
                continue;
            }
            for (ModelConfigPointDTO pointCfg : points) {
                if (pointCfg == null || !isSceneMatched(pointCfg.getSceneCode(), sceneCode)) {
                    continue;
                }
                if (Constants.ZERO.equals(pointCfg.getIsShow())) {
                    continue;
                }
                Integer monitorType = pointCfg.getMonitorType();
                if (monitorType == null || monitorType != LargeModelAlarmFieldConstants.MONITOR_TYPE_DRAW_AREA) {
                    continue;
                }
                if (!StringUtils.isEmpty(pointCfg.getMonitorAreaId())) {
                    monitorAreaIds.add(pointCfg.getMonitorAreaId());
                }
            }
        }
    }

    private static boolean isSceneMatched(String pointSceneCode, String sceneCode) {
        if (StringUtils.isEmpty(pointSceneCode)) {
            return true;
        }
        return pointSceneCode.contains(sceneCode);
    }

    private static boolean isInAnyPeriod(LocalTime now, String analysisPeriodJson, DateTimeFormatter formatter) {
        List<AnalysisPeriodDTO> periodList = GsonUtils.parseArray(analysisPeriodJson, AnalysisPeriodDTO.class);
        if (CollectionUtils.isEmpty(periodList)) {
            return true;
        }
        for (AnalysisPeriodDTO period : periodList) {
            if (period == null || StringUtils.isEmpty(period.getStartTime())
                    || StringUtils.isEmpty(period.getEndTime())) {
                continue;
            }
            LocalTime start = LocalTime.parse(period.getStartTime(), formatter);
            LocalTime end = LocalTime.parse(period.getEndTime(), formatter);
            if (!now.isBefore(start) && !now.isAfter(end)) {
                return true;
            }
        }
        return false;
    }

    private static JSONArray parseRootArray(Object fieldValue) {
        if (fieldValue == null) {
            return null;
        }
        if (fieldValue instanceof JSONArray) {
            return (JSONArray) fieldValue;
        }
        if (fieldValue instanceof String) {
            String fieldStr = (String) fieldValue;
            if (StringUtils.isEmpty(fieldStr)) {
                return null;
            }
            return JSON.parseArray(fieldStr);
        }
        return JSON.parseArray(JSON.toJSONString(fieldValue));
    }

    private static JSONArray parseInnerFieldArray(Object fieldValue) {
        return parseRootArray(fieldValue);
    }

    private static JSONArray parseAttrValueAsArray(Object attrValue) {
        if (attrValue == null) {
            return null;
        }
        if (attrValue instanceof JSONArray) {
            return (JSONArray) attrValue;
        }
        if (attrValue instanceof String) {
            String attrValueStr = (String) attrValue;
            if (StringUtils.isEmpty(attrValueStr)) {
                return null;
            }
            return JSON.parseArray(attrValueStr);
        }
        return JSON.parseArray(JSON.toJSONString(attrValue));
    }

    /**
     * 保持 attrValue 原始类型：字符串则序列化为字符串，数组则保持数组
     */
    private static Object wrapAttrValue(JSONArray filteredArray, Object originalAttrValue) {
        if (originalAttrValue instanceof String) {
            return filteredArray == null ? "[]" : filteredArray.toJSONString();
        }
        return filteredArray == null ? new JSONArray() : filteredArray;
    }
}
