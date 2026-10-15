package com.chinatower.platform.modules.alarm.kafka;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 告警分级：调 Dify 前对 Kafka 载荷中的图片、视频 URL 批量加密
 */
@Slf4j
@Component
public class AiJudgeMediaEncryptHelper {

    /** 告警视频 URL 字段 */
    public static final String KEY_ALARM_VIDEO_URL = "alarmVideoUrl";
    /** 原始图片 URL 字段 */
    public static final String KEY_ORIGINAL_PICTURE_URL = "originalPictureUrl";
    /** 告警图片 URL 字段 */
    public static final String KEY_ALARM_PICTURE_URL = "alarmPictureUrl";

    private static final String COMMA = ",";

    @Autowired(required = false)
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     * 对载荷中的图片、视频字段加密（支持逗号分隔的多 URL），仅影响传给 Dify 的参数
     */
    public void encryptMediaUrls(JSONObject payload) {
        if (payload == null) {
            return;
        }
        if (fileUrlEncryptUtil == null) {
            log.warn("FileUrlEncryptUtil未注入，跳过媒体URL加密, alarmId={}", payload.getString("alarm_id"));
            return;
        }
        try {
            String videoUrl = payload.getString(KEY_ALARM_VIDEO_URL);
            String originalUrl = payload.getString(KEY_ORIGINAL_PICTURE_URL);
            String alarmPicUrl = payload.getString(KEY_ALARM_PICTURE_URL);

            List<String> allUrls = new ArrayList<>();
            allUrls.addAll(splitUrls(videoUrl));
            allUrls.addAll(splitUrls(originalUrl));
            allUrls.addAll(splitUrls(alarmPicUrl));
            if (allUrls.isEmpty()) {
                return;
            }

            Map<String, String> encryptedMap = fileUrlEncryptUtil.dealObsFileUrlBatch(allUrls);
            if (encryptedMap == null || encryptedMap.isEmpty()) {
                log.warn("媒体URL加密结果为空, alarmId={}", payload.getString("alarm_id"));
                return;
            }

            payload.put(KEY_ALARM_VIDEO_URL, replaceUrls(videoUrl, encryptedMap));
            payload.put(KEY_ORIGINAL_PICTURE_URL, replaceUrls(originalUrl, encryptedMap));
            payload.put(KEY_ALARM_PICTURE_URL, replaceUrls(alarmPicUrl, encryptedMap));
            log.info("告警分级媒体URL加密完成, alarmId={}", payload.getString("alarm_id"));
        } catch (Exception e) {
            log.error("告警分级媒体URL加密异常, alarmId={}", payload.getString("alarm_id"), e);
        }
    }

    private List<String> splitUrls(String url) {
        if (StringUtils.isBlank(url)) {
            return Collections.emptyList();
        }
        return Arrays.stream(url.split(COMMA))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    /**
     * 按逗号拆分后逐段替换为加密 URL，保持与原值相同的多 URL 格式
     */
    private String replaceUrls(String url, Map<String, String> encryptedMap) {
        if (StringUtils.isBlank(url) || encryptedMap == null || encryptedMap.isEmpty()) {
            return url;
        }
        return splitUrls(url).stream()
                .map(item -> encryptedMap.getOrDefault(item, item))
                .collect(Collectors.joining(COMMA));
    }
}
