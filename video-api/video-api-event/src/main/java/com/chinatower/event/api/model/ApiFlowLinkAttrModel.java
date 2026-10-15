package com.chinatower.event.api.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/25 11:03
 */
@Data
public class ApiFlowLinkAttrModel implements Serializable {
    private static final long serialVersionUID = -4074803938124426158L;
    /** 主键 */
    private Long flowLinkId;
    /**
     * 告警流程ID
     * @required true
     */
    private Long flowId;
    /**
     * 当前环节Id
     * @required true
     */
    private Long curLink;
    /** 环节名称 */
    private String linkName;
    /** 环节属性，1开始环节，2中间环节，3结束环节 */
    private String linkType;
    /** 上一个环节 */
    private String preLink;
    /** 下一个环节 */
    private String nextLink;
    /** 用户类型，1用户，2角色 */
    private String userType;
    /** 角色ID，多个以,分隔 */
    private String roleId;
    /** 组织ID，多个以,分隔 */
    private String orgId;
    /** 根组织ID，多个以,分隔 */
    private String rootOrgId;
    /** 告警流程配置表ID */
    private Long alarmFlowId;
    /** 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开 */
    private String msgNotifyType;
    /** 是否通知管理员，0，不通知，1通知 */
    private String notifyAdminFlag;
    /** 是否通知领导，0，不通知，1通知 */
    private String leaderFlag;
    /** 会签标识，0，不会签，1会签 */
    private String signFlag;
    /** 回退标识 0-不回退，1-可回退 */
    private String rollbackFlag;
    /** 时限，单位分钟 */
    private String timeLimit;
    /** 0为默认属性，1为配置后属性 */
    private String attrFlag;
    /** 查询开启分页：true-开启，false-关闭 */
    private boolean enablePage;

    /** 用户ID,可存在多个，中间用逗号隔开 */
    private String userId;
    /** 通知领导ID，多个ID中间用逗号隔开 */
    private String leaderId;

    /**
     * 告警来源
     * @required true
     */
    private String warningSource;

    /**
     * 模板ID
     * @required true
     */
    private String templateId;

    private String turnFlag;

    private String realLinkType;

    /**
     * 网格id
     */
    private String gridId;

    /**
     * 是否有网格员（1：有）
     */
    private String gridFlag;

    /**
     * 是否过滤数据权限（0否,1是）
     */
    private String dataFilterPermiss;
    /**
     * 是否展示客户管理员（0否,1是）
     */
    private String customAdmin;

    /**
     * 用户昵称（用于选择处理人模糊查询）
     */
    private String userName;

    /**
     * 流程环节属性id
     */
    private String attrId;

    /**
     * 是否开启受理模式（0:不开启 1:开启）
     */
    private String enableAcceptanceMode;

    /**
     * 受限时限
     */
    private String timeLimitAcceptance;

    /** 用户信息 */
    //private List<SysInfo> userInfos;
}
