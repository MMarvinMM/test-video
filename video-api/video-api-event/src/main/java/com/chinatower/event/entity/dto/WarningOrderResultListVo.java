package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WarningOrderResultListVo implements Serializable {

    private String fileImgUrlSrc;

    private String fileImgUrlIcon;

    private String videoUrl;

    private String frontAlarmPicUrl;

    private String frontAlarmVedioUrl;

    private String orderId;

    private String warningTypeId;

    private String warningTypeName;

    private String alarmTime;

    private String orderStatus;

    private String orderStatusName;

    private String processId;

    private String alarmBody;

    private int ifEdit;

    private String deviceCode;

    private String warningSource;

    private String warningSourceName;

    private String enableAcceptanceMode;
}
