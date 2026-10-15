package com.chinatower.platform.project.custom.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class AlarmInfoReqVo implements Serializable {

    private String alarmSource;

    private String operation_alarm_type_str;


}
