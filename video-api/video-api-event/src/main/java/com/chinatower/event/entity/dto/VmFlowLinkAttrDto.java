package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

/**
 * @Description 告警流程环节 业务对象
 * @Author guyuefei
 * @Date 2022/7/25
 * @Version release_v1.5
 **/
@Data
public class VmFlowLinkAttrDto extends BaseIndustryEntity {
    /** 模板id */
    private Long templateId;
    /** 租户ID */
    private String tenantId;
    /** 环节ID */
    private Integer linkId;
    /** 告警流程配置Id */
    private Long alarmFlowId;
    /** 用户类型，1用户，2角色 */
    private String userType;
    /** 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开 */
    private String msgNotifyType;

    /** 选消息通知短信，填写短信通知时间间隔 单位分钟 */
    private String msgNotifyLimit;

    /** 短信通知间隔 单位, 1：分钟，2：小时 */
    private String msgNotifyCompany;

    /** 是否通知领导，0，不通知，1通知 */
    private String leaderFlag;

    /** 会签标识，0，不会签，1会签 */
    private String signFlag;

    /** 回退标识 0-不回退，1-可回退 */
    private String rollbackFlag;

    /** 时限，单位分钟 */
    private String timeLimit;

    /** 是否通知管理员，0，不通知，1通知 */
    private String notifyAdminFlag;

    /**环节id**/
    private Long attrId;

    /**短信通知时间区间**/
    private String msgSendTimeInterval;

    private String realLinkType;

    /**
     * 是否过滤数据权限（0否,1是）
     */
    private String dataFilterPermiss;

    /**
     * 是否显示客户管理员,0否,1是
     */
    private String customAdmin;

    /**
     * 声音提示开始时间
     */
    private String timeStartAlram;

    private String timeEndAlram;

    private String voiceAlram;

    /**
     * 提醒时长类型，0： 手动关闭、1：自动停止
     */
    private String timeRemind;

    /**
     * 本地配置播放时长单位( 0: 秒，1：分钟)
     */
    private String timeRemindAuto;

    /**
     * 播放时长
     */
    private String timeRemindNumber;

    private String ifLaterRemind;

    private String laterRemindCompany;

    /**
     * 短信推送时效 实时推送0 定时推送1
     */
    private String timePushType;

    /**
     * 短信通知上限
     */
    private String timeLimitPush;
    /**
     * 是否开启受理模式(0:默认，不开启；1:开启)
     */
    private String enableAcceptanceMode;

    /**
     * 受理时限
     */
    private String timeLimitAcceptance;

}
