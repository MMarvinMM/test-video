package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FlowOverdueRemind implements Serializable {

    /**
     * 超期提醒时间
     */
    private String timeLimitRemind;
    /**
     * 超期提醒人
     */
    private String timeLimitRemindName;
    /**
     * 超期提醒标识（0:环节处理超期提醒；1：受理超期提醒）
     */
    private String overdueRemindSign;
}
