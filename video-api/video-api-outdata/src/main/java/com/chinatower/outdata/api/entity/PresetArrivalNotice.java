package com.chinatower.outdata.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresetArrivalNotice implements Serializable {

    private String accessKeyId;

    private String alarmType;

    private String channelCode;

    private String channelName;

    private String deviceCode;

    private String deviceName;

    private String endPoint;

    private ExtendInfo extendInfo;

    private String extractionType;

    private String noticeResource;

    private String patrolId;

    private String picPath;

    private Preset preset;

    private String secretAccessKey;

    private String type;

    private Long accessNode;

    private String createTime;
}