package com.chinatower.event.dto;

import java.io.Serializable;
import java.util.HashMap;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class JPushDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 用户编码
    private String userId;

    // 登录名
    private String userAccount;

    // 注册id
    private String registrationId;

    // 系统待办提醒类型：1.告警事件待办提醒 2.巡查任务待办提醒
    private String toDoReminderType;

    /**
     * 系统待办提醒状态： 若系统待办提醒类型为1.告警事件待办提醒 则状态为1.待认领 2.待处置 3.待批示 若系统待办提醒类型为2.巡查任务待办提醒 则状态为1.待处理 2.待认领
     */
    private String toDoReminderStatus;

    // 通知内容标题
    private String notificationTitle;

    // 消息内容标题
    private String msgTitle;

    // 消息内容
    private String msgContent;

    // 附加參數
    private HashMap<String, String> extras;
}