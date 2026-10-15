package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lyy
 * @version 1.0
 * @date 2022/7/20 11:03
 */
@Data
@ApiModel(description = "告警流程配置-流程环节属性入参")
public class VmFlowLinkAttrPageDTO extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = -4074803938124426158L;
    /** 主键 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /** 模板ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long templateId;
    /** 租户ID */
    private String tenantId;
    /** 当前环节Id */
    private Integer linkId;
    /** 如下三个属性来自：流程环节配置表vm_flow_link_industry_config */
    private String linkName; //流程环节名称
    private String roleId; //默认关联角色ID，多个角色中间用逗号隔开
    private String attrFlag; //属性：0为默认属性，1为算法配置属性
    /** 告警流程配置ID */
    private Long alarmFlowId;
    /** 用户类型，1用户，2角色 */
    private String userType;
    /** 消息通知类型，1：APP,2：短信,3：PC系统消息,4：PC告警声音，多种方式中间用逗号隔开 */
    private String msgNotifyType;
    /** 短信通知间隔 单位分钟 */
    private Integer msgNotifyLimit;
    /** 短信通知间隔 单位 1：分钟，2：小时*/
    private String msgNotifyCompany;
    /** 转领导批示，1:：是，0：否 */
    private String leaderFlag;
    /** 会签标识，0，不会签，1会签 */
    private String signFlag;
    /** 回退标识 0-不回退，1-可回退 */
    private String rollbackFlag;
    /** 环节处理时限 */
    private Integer timeLimit;
    /** 是否通知管理员，0，不通知，1通知 */
    private String notifyAdminFlag;

    /** 流程环节用户集合 */
    private List<VmFlowLinkUserDO> flowLinkUserList;
    /** 流程环节角色集合 */
    private List<VmFlowLinkRoleDO> flowLinkRoleList;
    /** 流程环节处理超期提醒集合 */
    private List<VmFlowLinkOverdueRemindDO> flowLinkOverdueRemindList;

    /** 短信通知时间区间 */
    private String msgSendTimeInterval;

    /** 环节别名 */
    private String linkAlias;
    /** 转派标识(1:：是，0：否) */
    private Integer turnFlag;
    /** 环节类型(1研判2调度4处置5核实6办结7开始) */
    private String realLinkType;

    /** 网格员-用户类型，1用户,2角色 */
    private String	gridUserType;
    /** 是否过滤数据权限,0否,1是 */
    private String	dataFilterPermiss;
    /** 是否显示客户管理员,0否,1是 */
    private String	customAdmin;
    /** 环节处理时限单位, 0: 秒，1：分钟，2：小时，3：天 */
    private String	timeLimitCompany;
    /** 告警声音提醒开始时段 */
    private String	timeStartAlram;
    /** 告警声音提醒结束时段 */
    private String	timeEndAlram;
    /** 告警提示音 */
    private String	voiceAlram;
    /** 提醒时长类型，0： 手动关闭、1：自动停止 */
    private String	timeRemind;
    /** 提醒时长自动停止单位 0: 秒，1：分钟 */
    private String	timeRemindAuto;
    /** 提醒时长自动停止 */
    private String	timeRemindNumber;
    /** 稍后提醒0否,1是 */
    private String	ifLaterRemind;
    /** 稍后提醒*/
    private String	laterRemind;
    /** 稍后提醒单位 0: 秒，1：分钟，2：小时 */
    private String	laterRemindCompany;
    /** 短信推送时效 实时推送0 定时推送1 */
    private String	timePushType;
    /** 短信通知上限'*/
    private String	timeLimitPush;
    /** 网格员角色 */
    private String	gridSignRole;
    /** 是否开启受理模式(0:默认，不开启；1:开启) */
    private String enableAcceptanceMode;
    /** 受理时限 */
    private Integer timeLimitAcceptance;
    /** 受理时限单位, 0: 秒，1：分钟，2：小时，3：天 */
    private String acceptanceCompany;
}
