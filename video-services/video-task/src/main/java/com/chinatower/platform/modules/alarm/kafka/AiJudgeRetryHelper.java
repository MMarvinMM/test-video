package com.chinatower.platform.modules.alarm.kafka;

import com.alibaba.fastjson.JSONObject;

/**
 * 告警分级 Kafka 消息重试工具
 */
public final class AiJudgeRetryHelper {

    public static final String RETRY_KEY = "_retry";

    private AiJudgeRetryHelper() {
    }

    /**
     * 判断是否还可回队；并在可回队时将 _retry +1
     *
     * @param payload  消息体
     * @param maxRetry 最大重试次数（不含首次）
     * @return true=应回队；false=已超限应标记失败
     */
    public static boolean prepareRequeue(JSONObject payload, int maxRetry) {
        if (payload == null) {
            return false;
        }
        int retry = payload.getIntValue(RETRY_KEY);
        if (retry >= maxRetry) {
            return false;
        }
        payload.put(RETRY_KEY, retry + 1);
        return true;
    }
}
