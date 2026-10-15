package com.chinatower.platform.modules.alarm.kafka;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.order.api.IWeatherApiService;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.modules.alarm.dao.AiJudgeAlarmDAO;
import com.chinatower.platform.modules.alarm.service.impl.AiJudgeDispatchServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 告警分级 Kafka 消费：查气象 → 落扩展字段 → 调用 Dify；失败则回队，超过重试次数标记研判失败
 */
@Slf4j
@Component
@ConditionalOnProperty(name = {"spring.kafka.bootstrap-servers", "ai.judge.dify.url", "ai.judge.dify.api-key"})
public class AiJudgeKafkaConsumer {

    private static final String RETRY_KEY = AiJudgeRetryHelper.RETRY_KEY;

    @Value("${ai.judge.dify.url}")
    private String difyUrl;

    /** Dify API Key，请求头 Authorization: Bearer {api-key} */
    @Value("${ai.judge.dify.api-key}")
    private String difyApiKey;

    @Value("${ai.judge.dify.timeout-ms:60000}")
    private int difyTimeoutMs;

    @Value("${ai.judge.dify.max-retry:3}")
    private int maxRetry;

    /** Dify query 固定文案，默认：森林智保烟火告警研判 */
    @Value("${ai.judge.dify.query:森林智保烟火告警研判}")
    private String difyQuery;

    @Autowired
    private AiJudgeKafkaProducer aiJudgeKafkaProducer;
    @Autowired
    private AiJudgeAlarmDAO aiJudgeAlarmDAO;
    @Autowired(required = false)
    private IWeatherApiService weatherApiService;
    @Autowired
    private AiJudgeMediaEncryptHelper aiJudgeMediaEncryptHelper;

    @KafkaListener(
            topics = AiJudgeDispatchServiceImpl.AI_JUDGE_ALARM_TOPIC,
            containerFactory = "aiJudgeKafkaListenerContainerFactory"
    )
    public void onMessage(String message) {
        log.info("告警分级Kafka消费开始, message={}", message);
        JSONObject payload;
        try {
            payload = JSON.parseObject(message);
        } catch (Exception e) {
            log.error("告警分级Kafka消息非JSON，丢弃, message={}", message, e);
            return;
        }
        if (payload == null) {
            return;
        }
        String alarmId = payload.getString("alarm_id");
        try {
            // 调 Dify 前查气象写入载荷，并落库扩展字段
            fillWeatherAndPersist(payload, alarmId);
            // 调 Dify 前对图片、视频 URL 加密
            aiJudgeMediaEncryptHelper.encryptMediaUrls(payload);
            callDify(payload);
            log.info("告警分级调用Dify成功(等待回调回填), alarmId={}", alarmId);
        } catch (Exception e) {
            log.error("告警分级调用Dify失败, alarmId={}", alarmId, e);
            requeueOrFail(payload, alarmId);
        }
    }

    /**
     * 查询气象写入 payload，并更新 agent_extend_field（查不到则跳过，不影响调 Dify）
     */
    private void fillWeatherAndPersist(JSONObject payload, String alarmId) {
        String extendJson = AiJudgeWeatherHelper.fillWeatherAndBuildExtendJson(weatherApiService, payload);
        if (StringUtils.isBlank(extendJson) || StringUtils.isBlank(alarmId) || alarmId.length() < 6) {
            return;
        }
        String tableName = Constants.WARN_ORDER_TEBLE + alarmId.substring(0, 6);
        try {
            int rows = aiJudgeAlarmDAO.updateAgentExtendField(tableName, alarmId, extendJson);
            log.info("告警分级气象写入扩展字段完成, alarmId={}, rows={}", alarmId, rows);
        } catch (Exception e) {
            log.error("告警分级气象写入扩展字段异常, alarmId={}", alarmId, e);
        }
    }

    /**
     * 调用 Dify：Bearer 鉴权 + {"inputs":{"alarm_params":"..."},"user":"alarmCode","query":"..."}
     */
    private void callDify(JSONObject payload) {
        if (StringUtils.isBlank(difyUrl)) {
            throw new IllegalStateException("ai.judge.dify.url未配置");
        }
        if (StringUtils.isBlank(difyApiKey)) {
            throw new IllegalStateException("ai.judge.dify.api-key未配置");
        }
        JSONObject body = AiJudgeDifyRequestHelper.buildRequestBody(payload, difyQuery);
        String authorization = AiJudgeDifyRequestHelper.buildAuthorization(difyApiKey);
        log.info("告警分级调用Dify, url={}, body={}", difyUrl, body.toJSONString());
        HttpResponse response = HttpRequest.post(difyUrl)
                .header("Authorization", authorization)
                .header("Content-Type", "application/json")
                .body(body.toJSONString())
                .timeout(difyTimeoutMs)
                .execute();
        if (response == null || !response.isOk()) {
            throw new IllegalStateException("Dify响应异常, status="
                    + (response == null ? null : response.getStatus())
                    + ", body=" + (response == null ? null : response.body()));
        }
        log.info("告警分级Dify响应, status={}, body={}", response.getStatus(), response.body());
    }

    /**
     * 调不通时消息回队；超过最大重试则标记研判失败
     */
    private void requeueOrFail(JSONObject payload, String alarmId) {
        if (AiJudgeRetryHelper.prepareRequeue(payload, maxRetry)) {
            try {
                aiJudgeKafkaProducer.send(AiJudgeDispatchServiceImpl.AI_JUDGE_ALARM_TOPIC, payload.toJSONString());
                log.warn("告警分级消息已回队, alarmId={}, retry={}", alarmId, payload.getIntValue(RETRY_KEY));
            } catch (Exception ex) {
                log.error("告警分级回队失败, alarmId={}", alarmId, ex);
                markFail(alarmId);
            }
            return;
        }
        log.error("告警分级超过最大重试，标记研判失败, alarmId={}, maxRetry={}", alarmId, maxRetry);
        markFail(alarmId);
    }

    private void markFail(String alarmId) {
        if (StringUtils.isBlank(alarmId) || alarmId.length() < 6) {
            return;
        }
        String tableName = Constants.WARN_ORDER_TEBLE + alarmId.substring(0, 6);
        try {
            aiJudgeAlarmDAO.updateAiJudgeStatus(tableName, alarmId, AiJudgeDispatchServiceImpl.STATUS_FAIL);
        } catch (Exception e) {
            log.error("告警分级标记失败状态异常, alarmId={}", alarmId, e);
        }
    }
}
