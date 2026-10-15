package com.chinatower.analysis.api.entity;

import lombok.Data;

/**
 * 统计待研判数量发送短信实体类
 * @author zhaozh
 */
@Data
public class JudgeStatisticsVO {
    private String tenantId; //客户编码
    private Integer judgeNum; //待研判数量
    private Integer judgeNumAll; //待研判当月数量
    private String alarmTypeId;
    private String alarmTypeName;
    private String judgeSentTimeMinute;// 研判环节发送短信间隔时间(单位:分钟)

    /**短信通知时间区间**/
    private String msgSendTimeInterval;

    private String notifyManager;
    private String deviceCode;
    private String regionCode;
    private String warningSource;
    private String mobilePhone;

    private String industryCode;
    private String appVerCode;

    private String msgNotifyCompany;
    private Integer msgNotifyLimit; //时限

    private String userType;
    private String roleUsers;
    private String linkId;

    /**
     * 是否过滤数据权限
     */
    private String dataFilterPermiss;

    private String gridId;

    private String userIds;
    private String gridUserIds;
    private String roleIds;
    private String gridRoleIds;
    /**
     * 是否今日告警（1：今日  0:往日）
     */
    private String flag;
    /**
     * 短信通知上限
     */
    private String timeLimePush;
}
