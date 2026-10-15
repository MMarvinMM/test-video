package com.chinatower.event.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AlarmLables
 * @Deacription 告警类型标签
 * @Author chenmf
 * @Date 2022/4/26 16:01
 * @Version 1.0
 **/
@Data
@TableName("vm_alarm_share")
public class AlarmShareDTO implements Serializable {

    /**
     *  告警分享ID
     *  @required true
     */
    private String shareId;

    /**
     * 告警分享
     * @required true
     */
    private String alarmShare;

}
