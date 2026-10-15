package com.chinatower.platform.modules.alarm.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @className: JudgeAlarmSmsNoticeVO
 * @description: 研判环节告警短信通知VO
 * @author: peng.haibo
 * @date: 2022/6/24
 */
@Data
public class JudgeAlarmSmsNoticeVO implements Serializable {

    /** 动态表名 */
    private String tableName;

    /** 租户ID */
    private String tenantId;

    /** 处理人userIds */
    private String processId;

    /** 告警类型ID */
    private String warningTypeId;

    /** 告警流程ID */
    private String flowId;

    /** 通知类型 */
    private String msgNotifyType;

    /** 统计通知短信间隔 */
    private String msgNotifyLimit;

    /** 统计通知短信间隔单位 */
    private String msgNotifyCompany;

}
