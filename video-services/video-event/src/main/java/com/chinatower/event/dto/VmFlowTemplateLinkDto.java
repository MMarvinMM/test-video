package com.chinatower.event.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 告警流程环节 业务对象
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Data
public class VmFlowTemplateLinkDto extends VmFlowTemplateLinkModel implements Serializable {
    private static final long serialVersionUID = -4074803938124426158L;
    /** 流程配置id */
    private Long alarmFlowId;
    /** 流程配置名称 */
    private String alarmFlowName;
    /** 节点名称 */
    private String linkName;
    /** 时限，单位分钟 */
    private String timeLimit;
    /** 默认关联角色ID */
    private String roleId;
    /** 默认关联用户ID */
    private String userId;
    /** 用户类型，1用户，2角色 */
    private String userType;
    /** 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC系统消息 4、PC告警声音 */
    private String msgNotifyType;
    /**流程属性id,用于关联查询流程环节处理人信息**/
    private Long attrId;
    /** 是否通知管理员，0，不通知，1通知 */
    private String notifyAdminFlag;
    /** 是否通知领导，0，不通知，1通知 */
    private String leaderFlag;
    /**
     * 是否转派标识
     */
    private String turnFlag;

    /**
     * 是否过滤数据权限（0否,1是）
     */
    private String dataFilterPermiss;
    /**
     * 是否开启受理模式(0:默认，不开启；1:开启)
     */
    private String enableAcceptanceMode;

    /**
     * 受理时限
     */
    private String timeLimitAcceptance;

    /**
     * 流程环节id
     */
    private String flowAttrId;

}
