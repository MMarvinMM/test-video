package com.chinatower.event.core.alarm.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.event.constant.AiExtraFieldConstants;
import com.chinatower.event.constant.AlarmConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AI 告警 extraField 解析工具
 */
public final class AiExtraFieldUtil {

    private static final Logger log = LoggerFactory.getLogger(AiExtraFieldUtil.class);

    private AiExtraFieldUtil() {
    }

    /**
     * 解析 AI 告警 extraField，保留原有 JSON 结构不变，
     * 仅在包含 CVLM-ALARM-INFO 的外层 attrList 节点末尾追加 objectCount。
     *
     * @param warningSource 告警来源
     * @param extraField    原始 extraField JSON 字符串
     * @return 追加后的完整 extraField；非 AI 告警、无需追加或解析失败时返回原值
     */
    public static String enrichExtraField(String warningSource, String extraField) {
        if (!AlarmConstants.ALARM_SOURCE_1.equals(warningSource) || StringUtils.isEmpty(extraField)) {
            return extraField;
        }
        try {
            JSONArray rootArray = JSON.parseArray(extraField);
            if (rootArray == null || rootArray.isEmpty()) {
                return extraField;
            }
            boolean changed = false;
            for (int i = 0; i < rootArray.size(); i++) {
                JSONObject outerNode = rootArray.getJSONObject(i);
                if (outerNode == null) {
                    continue;
                }
                // 外层 attrList：与 CVLM-ALARM-INFO 同级，在其末尾追加
                JSONArray outerAttrList = outerNode.getJSONArray(AiExtraFieldConstants.JSON_FIELD_ATTR_LIST);
                if (outerAttrList == null || outerAttrList.isEmpty()) {
                    continue;
                }
                if (!containsCvlmAlarmInfo(outerAttrList) || containsOuterObjectCount(outerAttrList)) {
                    continue;
                }
                if (appendObjectCountToOuterAttrList(outerAttrList)) {
                    changed = true;
                }
            }
            return changed ? rootArray.toJSONString() : extraField;
        } catch (Exception e) {
            log.warn("AI告警extraField解析失败，返回原始值，extraField={}", extraField, e);
            return extraField;
        }
    }

    /**
     * 从 CVLM-ALARM-INFO.attrValue.extendField 解析实际触发信息，追加到外层 attrList
     *
     * @return 是否有追加
     */
    private static boolean appendObjectCountToOuterAttrList(JSONArray outerAttrList) {
        boolean appended = false;
        for (int i = 0; i < outerAttrList.size(); i++) {
            JSONObject attr = outerAttrList.getJSONObject(i);
            if (attr == null || !AiExtraFieldConstants.ATTR_TYPE_CVLM_ALARM_INFO.equals(
                    attr.getString(AiExtraFieldConstants.JSON_FIELD_ATTR_TYPE))) {
                continue;
            }
            String attrValueStr = attr.getString(AiExtraFieldConstants.JSON_FIELD_ATTR_VALUE);
            if (StringUtils.isEmpty(attrValueStr)) {
                continue;
            }
            JSONArray alarmInfoArray = JSON.parseArray(attrValueStr);
            if (alarmInfoArray == null || alarmInfoArray.isEmpty()) {
                continue;
            }
            for (int j = 0; j < alarmInfoArray.size(); j++) {
                JSONObject alarmInfo = alarmInfoArray.getJSONObject(j);
                if (alarmInfo == null) {
                    continue;
                }
                appended |= appendFromExtendField(alarmInfo.get(AiExtraFieldConstants.JSON_FIELD_EXTEND_FIELD), outerAttrList);
            }
        }
        return appended;
    }

    /**
     * 解析 extendField，将 objectCount 追加到外层 attrList 末尾
     */
    private static boolean appendFromExtendField(Object extendFieldObj, JSONArray outerAttrList) {
        JSONArray extendFieldArray = parseExtendFieldArray(extendFieldObj);
        if (extendFieldArray == null || extendFieldArray.isEmpty()) {
            return false;
        }
        boolean appended = false;
        for (int i = 0; i < extendFieldArray.size(); i++) {
            JSONObject extendItem = extendFieldArray.getJSONObject(i);
            if (extendItem == null) {
                continue;
            }
            String objectCount = findAttrValue(extendItem.getJSONArray(AiExtraFieldConstants.JSON_FIELD_ATTR_LIST),
                    AiExtraFieldConstants.ATTR_TYPE_OBJECT_COUNT);
            if (StringUtils.isNotEmpty(objectCount)) {
                outerAttrList.add(buildAttr(AiExtraFieldConstants.ATTR_TYPE_OBJECT_COUNT, objectCount));
                appended = true;
            }
        }
        return appended;
    }

    private static JSONArray parseExtendFieldArray(Object extendFieldObj) {
        if (extendFieldObj == null) {
            return null;
        }
        if (extendFieldObj instanceof JSONArray) {
            return (JSONArray) extendFieldObj;
        }
        if (extendFieldObj instanceof String) {
            String extendFieldStr = (String) extendFieldObj;
            if (StringUtils.isEmpty(extendFieldStr)) {
                return null;
            }
            return JSON.parseArray(extendFieldStr);
        }
        return JSON.parseArray(JSON.toJSONString(extendFieldObj));
    }

    private static JSONObject buildAttr(String attrType, String attrValue) {
        JSONObject attr = new JSONObject();
        attr.put(AiExtraFieldConstants.JSON_FIELD_ATTR_TYPE, attrType);
        attr.put(AiExtraFieldConstants.JSON_FIELD_ATTR_VALUE, attrValue);
        return attr;
    }

    private static String findAttrValue(JSONArray attrList, String targetAttrType) {
        if (attrList == null || attrList.isEmpty() || StringUtils.isEmpty(targetAttrType)) {
            return null;
        }
        for (int i = 0; i < attrList.size(); i++) {
            JSONObject attr = attrList.getJSONObject(i);
            if (attr != null && targetAttrType.equals(attr.getString(AiExtraFieldConstants.JSON_FIELD_ATTR_TYPE))) {
                return attr.getString(AiExtraFieldConstants.JSON_FIELD_ATTR_VALUE);
            }
        }
        return null;
    }

    private static boolean containsCvlmAlarmInfo(JSONArray outerAttrList) {
        for (int i = 0; i < outerAttrList.size(); i++) {
            JSONObject attr = outerAttrList.getJSONObject(i);
            if (attr != null && AiExtraFieldConstants.ATTR_TYPE_CVLM_ALARM_INFO.equals(
                    attr.getString(AiExtraFieldConstants.JSON_FIELD_ATTR_TYPE))) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsOuterObjectCount(JSONArray outerAttrList) {
        for (int i = 0; i < outerAttrList.size(); i++) {
            JSONObject attr = outerAttrList.getJSONObject(i);
            if (attr != null && AiExtraFieldConstants.ATTR_TYPE_OBJECT_COUNT.equals(
                    attr.getString(AiExtraFieldConstants.JSON_FIELD_ATTR_TYPE))) {
                return true;
            }
        }
        return false;
    }
}
