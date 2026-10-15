package com.chinatower.event.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PushAlarmThirdVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String warningOrderId;
    private String alarmType;
    private String alarmTime;
    private String alarmBody;
    private String deviceCode;
    private String deviceName;
    private String channelCode;
    private String channelName;
    private String pan;
    private String tilt;
    private String zoom;
    private String eventLongitude;
    private String eventLatitude;
    private String alarmCode;
    private String originalPictureUrl;
    private String alarmVideoUrl;
    private String alarmPictureUrl;
    private String regionCode;
    private String regionName;
    private String location;
    private String deviceLongitude;
    private String deviceLatitude;
    private String frontAlarmPicUrl;
    private String frontAlarmVedioUrl;
    private String address;
    private String presetId;
    private String presetName;
    private String extraField;
    private String warningSource;
}
