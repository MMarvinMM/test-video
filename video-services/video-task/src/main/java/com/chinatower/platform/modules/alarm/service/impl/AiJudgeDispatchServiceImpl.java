package com.chinatower.platform.modules.alarm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.util.AiJudgeColumnSupportUtil;
import com.chinatower.common.core.utils.TableUtil;
import com.chinatower.platform.common.Constants;
import com.chinatower.platform.modules.alarm.dao.AiJudgeAlarmDAO;
import com.chinatower.platform.modules.alarm.entity.AiJudgePendingVO;
import com.chinatower.platform.modules.alarm.kafka.AiJudgeKafkaProducer;
import com.chinatower.platform.modules.alarm.service.AiJudgeDispatchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 告警分级：定时拉取未研判 → 研判中 → 投递 Kafka
 */
@Slf4j
@Service
public class AiJudgeDispatchServiceImpl implements AiJudgeDispatchService {

    /** 告警分级 Kafka Topic */
    public static final String AI_JUDGE_ALARM_TOPIC = "alarm_grading_ai_judge";

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_PROCESSING = 1;
    public static final int STATUS_FAIL = 3;

    @Autowired
    private AiJudgeAlarmDAO aiJudgeAlarmDAO;
    @Autowired(required = false)
    private AiJudgeKafkaProducer aiJudgeKafkaProducer;

    /** 扫描月表数量（含当月），默认当前月+上月 */
    @Value("${ai.judge.dispatch.scan-months:2}")
    private int scanMonths;

    /** 研判中超时重置为未研判的分钟数，默认30分钟 */
    @Value("${ai.judge.dispatch.stuck-timeout-minutes:30}")
    private int stuckTimeoutMinutes;

    @Override
    public void dispatchPending(int limit) {
        if (aiJudgeKafkaProducer == null) {
            log.warn("告警分级Kafka未就绪(spring.kafka.bootstrap-servers未配置)，跳过调度");
            return;
        }
        int batchLimit = limit <= 0 ? 5 : limit;
        Date now = new Date();
        for (int i = 0; i < Math.max(scanMonths, 1); i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.MONTH, -i);
            String tableName = TableUtil.getTableName(calendar.getTime(), Constants.WARN_ORDER_TEBLE);
            if (!AiJudgeColumnSupportUtil.isSupported(tableName)) {
                log.debug("告警分级跳过无 AI 字段月表, tableName={}", tableName);
                continue;
            }
            try {
                // 超时仍停留在研判中的数据，重置为未研判，保证可再次投递
                int resetRows = aiJudgeAlarmDAO.resetStuckProcessing(
                        tableName, STATUS_PROCESSING, STATUS_PENDING, stuckTimeoutMinutes);
                if (resetRows > 0) {
                    log.info("告警分级重置超时研判中数据, tableName={}, rows={}", tableName, resetRows);
                }
                dispatchByTable(tableName, batchLimit);
            } catch (Exception e) {
                log.error("告警分级调度异常, tableName={}", tableName, e);
            }
        }
    }

    private void dispatchByTable(String tableName, int limit) {
        List<AiJudgePendingVO> pendingList = aiJudgeAlarmDAO.selectPendingList(tableName, limit);
        if (CollectionUtils.isEmpty(pendingList)) {
            log.info("告警分级无未研判数据, tableName={}", tableName);
            return;
        }
        log.info("告警分级拉取未研判数据, tableName={}, size={}", tableName, pendingList.size());
        for (AiJudgePendingVO item : pendingList) {
            try {
                dispatchOne(tableName, item);
            } catch (Exception e) {
                log.error("告警分级单条投递异常, warningOrderId={}", item.getWarningOrderId(), e);
            }
        }
    }

    private void dispatchOne(String tableName, AiJudgePendingVO item) {
        if (item == null || StringUtils.isBlank(item.getWarningOrderId())) {
            return;
        }
        // 乐观锁：仅未研判可改为研判中，避免并发重复投递
        int updated = aiJudgeAlarmDAO.updateStatusToProcessing(
                tableName, item.getWarningOrderId(), STATUS_PENDING, STATUS_PROCESSING);
        if (updated <= 0) {
            log.info("告警分级状态已变更，跳过投递, warningOrderId={}", item.getWarningOrderId());
            return;
        }
        JSONObject message = buildKafkaMessage(item);
        try {
            aiJudgeKafkaProducer.send(AI_JUDGE_ALARM_TOPIC, message.toJSONString());
        } catch (Exception e) {
            // 发送失败：回滚为未研判，保证消息不丢、可再次调度
            log.error("告警分级Kafka发送失败，回滚为未研判, warningOrderId={}", item.getWarningOrderId(), e);
            aiJudgeAlarmDAO.updateAiJudgeStatus(tableName, item.getWarningOrderId(), STATUS_PENDING);
        }
    }

    /**
     * 组装与历史上报外呼一致的消息体（data字段 + alarm_id + scene_template）
     */
    private JSONObject buildKafkaMessage(AiJudgePendingVO item) {
        JSONObject body = new JSONObject();
        body.put("alarmBody", item.getAlarmBody());
        body.put("msgType", item.getMsgType());
        body.put("importanceLevel", item.getImportanceLevel());
        body.put("alarmTime", item.getAlarmTime());
        body.put("latitude", item.getLatitude());
        body.put("longitude", item.getLongitude());
        body.put("zoom", item.getZoom());
        body.put("pan", item.getPan());
        body.put("tilt", item.getTilt());
        body.put("compConfidenceLevel", item.getCompConfidenceLevel());
        body.put("deviceCode", item.getDeviceCode());
        body.put("deviceName", item.getDeviceName());
        body.put("channelCode", item.getChannelCode());
        body.put("channelName", item.getChannelName());
        body.put("alarmCode", item.getAlarmCode());
        body.put("algorithmCode", item.getAlgorithmCode());
        body.put("alarmType", item.getWarningTypeId());
        body.put("alarmSource", item.getWarningSource());
        body.put("taskId", item.getTaskId());
        body.put("alarmVideoUrl", item.getVideoUrl());
        body.put("originalPictureUrl", item.getFileImgUrlSrc());
        body.put("alarmPictureUrl", item.getFileImgUrlIcon());
        body.put("industryCode", item.getIndustryCode());
        body.put("tenantId", item.getTenantId());
        body.put("tenantName", item.getTenantName());
        body.put("address", item.getAddress());
        body.put("provinceCode", item.getProvinceCode());
        body.put("cityCode", item.getCityCode());
        body.put("countyCode", item.getCountyCode());
        body.put("alarm_id", item.getWarningOrderId());
        body.put("scene_template", item.getSceneTemplate());
        body.put("_retry", 0);
        return body;
    }
}
