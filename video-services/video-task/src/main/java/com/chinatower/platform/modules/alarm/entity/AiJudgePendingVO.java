package com.chinatower.platform.modules.alarm.entity;

import lombok.Data;

/**
 * 告警分级：待研判/投递 Kafka 的告警数据
 */
@Data
public class AiJudgePendingVO {

    private String warningOrderId;
    private String alarmBody;
    private String msgType;
    private String importanceLevel;
    private String alarmTime;
    private String latitude;
    private String longitude;
    private String zoom;
    private String pan;
    private String tilt;
    private String compConfidenceLevel;
    private String deviceCode;
    private String deviceName;
    private String channelCode;
    private String channelName;
    private String alarmCode;
    private String algorithmCode;
    private String warningTypeId;
    private String warningSource;
    private String taskId;
    private String videoUrl;
    private String fileImgUrlSrc;
    private String fileImgUrlIcon;
    private String industryCode;
    /** 场景模板（来自配置表 scene_template） */
    private String sceneTemplate;
    private String tenantId;
    private String tenantName;
    private String address;
    private String provinceCode;
    private String cityCode;
    private String countyCode;
}
