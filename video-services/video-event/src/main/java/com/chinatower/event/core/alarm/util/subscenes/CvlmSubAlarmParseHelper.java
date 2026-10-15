package com.chinatower.event.core.alarm.util.subscenes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 从 extra_field 解析 CVLM 子告警列表（刷数侧精简副本）。
 * <p>
 * 与 video-receive LargeModelAlarmFieldSplitUtil.parseSubAlarmsFromExtendField 同源，后续可抽公共模块。
 * 仅保留刷数/提取所需方法，helpers 包内可见。
 * </p>
 */
final class CvlmSubAlarmParseHelper {

    private static final Logger log = LoggerFactory.getLogger(CvlmSubAlarmParseHelper.class);

    private CvlmSubAlarmParseHelper() {
    }

    /**
     * 从拆分后的 extendField / extra_field 解析子告警列表。
     */
    static List<ReceiveAlarmVO> parseSubAlarmsFromExtendField(Object extendField) {
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
            JSONArray outerAttrList = outerNode.getJSONArray(SubScenesCvlmConstants.JSON_FIELD_ATTR_LIST);
            if (outerAttrList == null || outerAttrList.isEmpty()) {
                continue;
            }
            for (int j = 0; j < outerAttrList.size(); j++) {
                JSONObject attr = outerAttrList.getJSONObject(j);
                if (attr == null || !SubScenesCvlmConstants.ATTR_TYPE_CVLM_ALARM_INFO.equals(
                        attr.getString(SubScenesCvlmConstants.JSON_FIELD_ATTR_TYPE))) {
                    continue;
                }
                JSONArray alarmInfoArray = parseAttrValueAsArray(
                        attr.get(SubScenesCvlmConstants.JSON_FIELD_ATTR_VALUE));
                if (alarmInfoArray == null || alarmInfoArray.isEmpty()) {
                    continue;
                }
                // Fastjson 反序列化，避免依赖 receive 侧 GsonUtils
                List<ReceiveAlarmVO> parsed =
                        JSON.parseArray(alarmInfoArray.toJSONString(), ReceiveAlarmVO.class);
                if (parsed != null && !parsed.isEmpty()) {
                    result.addAll(parsed);
                }
            }
        }
        return result;
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
            try {
                return JSON.parseArray(attrValueStr);
            } catch (Exception e) {
                log.warn("parseAttrValueAsArray fail", e);
                return null;
            }
        }
        return JSON.parseArray(JSON.toJSONString(attrValue));
    }
}
