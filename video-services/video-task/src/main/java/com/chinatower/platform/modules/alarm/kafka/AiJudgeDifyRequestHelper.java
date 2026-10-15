package com.chinatower.platform.modules.alarm.kafka;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * 组装 Dify /v1/chat-messages 请求体
 * 约定：
 * {
 *   "inputs": { "alarm_params": "<告警JSON字符串>" },
 *   "user": "<alarmCode>",
 *   "query": "森林智保烟火告警研判"
 * }
 */
public final class AiJudgeDifyRequestHelper {

    public static final String ALARM_PARAMS_KEY = "alarm_params";
    public static final String INPUTS_KEY = "inputs";
    public static final String USER_KEY = "user";
    public static final String QUERY_KEY = "query";
    public static final String ALARM_CODE_KEY = "alarmCode";

    /** 默认 query 文案 */
    public static final String DEFAULT_QUERY = "森林智保烟火告警研判";

    private AiJudgeDifyRequestHelper() {
    }

    /**
     * 从 Kafka 消息组装 Dify 入参（去掉内部重试字段后整体作为 JSON 字符串放入 inputs.alarm_params）
     */
    public static JSONObject buildRequestBody(JSONObject kafkaPayload) {
        return buildRequestBody(kafkaPayload, DEFAULT_QUERY);
    }

    /**
     * 从 Kafka 消息组装 Dify 入参，可指定 query
     */
    public static JSONObject buildRequestBody(JSONObject kafkaPayload, String query) {
        JSONObject alarmParams = new JSONObject();
        if (kafkaPayload != null) {
            alarmParams.putAll(kafkaPayload);
            alarmParams.remove(AiJudgeRetryHelper.RETRY_KEY);
        }
        JSONObject inputs = new JSONObject();
        inputs.put(ALARM_PARAMS_KEY, alarmParams.toJSONString());

        JSONObject body = new JSONObject();
        body.put(INPUTS_KEY, inputs);
        // user 传 alarmCode
        String alarmCode = kafkaPayload == null ? null : kafkaPayload.getString(ALARM_CODE_KEY);
        body.put(USER_KEY, StringUtils.defaultString(alarmCode));
        body.put(QUERY_KEY, StringUtils.defaultIfBlank(query, DEFAULT_QUERY));
        return body;
    }

    /**
     * 拼装鉴权头：Authorization: Bearer {apiKey}
     */
    public static String buildAuthorization(String apiKey) {
        if (apiKey == null) {
            return "Bearer ";
        }
        return "Bearer " + apiKey.trim();
    }
}
