package com.chinatower.device.dataobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UavFlightStatusVO {

    //设备编码
    private String deviceCode;
    //飞行任务ID
    private String planTaskId;
    //飞行状态
    private String flightStatus;
    //执行时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date planTime;
    //执行时间HH:MM
    private String planTimeHHmm;
}
