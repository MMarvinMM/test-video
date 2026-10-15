package com.chinatower.event.vo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class AlarmChannelParamVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String channelCode;
    private String deviceCode;
    private String warningTypeId;
    private String tenantId;
    //1:5分钟不再告警 2:1小时不再告警 3:今日不再告警 4:自定义
    private String intervalType;
    //自定义压制时间（分钟）
    private String intervalTime;
    //研判告警压制结束时间 yyyy-MM-dd HH:mm:ss
    private String intervalEndTime;

}

