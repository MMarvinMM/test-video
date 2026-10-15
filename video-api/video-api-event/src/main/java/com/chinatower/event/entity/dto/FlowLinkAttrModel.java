package com.chinatower.event.entity.dto;

import com.chinatower.common.core.dto.BaseModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/25 11:03
 */
@Data
public class FlowLinkAttrModel extends BaseModel implements Serializable {
    private static final long serialVersionUID = -4074803938124426158L;
    /** 主键 */
    private Long flowLinkId;
    /**
     * 流程配置ID
     * @required true
     */
    private Long flowId;
    /**
     * 当前环节ID
     * @required true
     */
    private Long curLink;

    /**
     * 当前环节Id
     */
    private Long linkId;

    /**
     * 环节名称
     */
    private String linkName;

    /**
     * 环节属性，1开始环节，2中间环节，3结束环节
     */
    private String linkType;

    /**
     * 上一个环节
     */
    private String preLink;

    /**
     * 下一个环节
     */
    private String nextLink;

    /**
     * 用户类型，1用户，2角色
     */
    private String userType;

    /**
     * 角色ID，多个以,分隔
     */
    private String roleId;

    /**
     * 组织ID，多个以,分隔
     */
    private String orgId;

    /**
     * 根组织ID，多个以,分隔
     */
    private String rootOrgId;

    /**
     * 告警流程配置表ID
     */
    private Long alarmFlowId;

    /**
     * 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开
     */
    private String msgNotifyType;

    /**
     * 是否通知管理员，0，不通知，1通知
     */
    private String notifyAdminFlag;

    /**
     * 是否通知领导，0，不通知，1通知
     */
    private String leaderFlag;

    /**
     * 会签标识，0，不会签，1会签
     */
    private String signFlag;

    /**
     * 回退标识 0-不回退，1-可回退
     */
    private String rollbackFlag;

    /**
     * 时限，单位分钟
     */
    private String timeLimit;

    /**
     * 0为默认属性，1为配置后属性
     */
    private String attrFlag;

    /**
     * 查询开启分页：true-开启，false-关闭
     */
    private boolean enablePage;

    /**
     * 用户ID,可存在多个，中间用逗号隔开
     */
    private String userId;

    /**
     * 通知领导ID，多个ID中间用逗号隔开
     */
    private String leaderId;

    /**
     * 用户信息
     */
    private List<SysInfo> userInfos;

    /**
     * 租户ID
     * @required true
     */
    private String tenantId;

    /**
     * 短信通知间隔 单位分钟
     */
    private String msgNotifyLimit;

    /**
     * 短信通知间隔 单位
     */
    private String msgNotifyCompany;

    /**
     * 环节集合
     */
    private List<Long> linkList;

    /**
     * 告警来源
     * @required true
     */
    private String warningSource;

    /**
     * 模板id
     */
    private Long templateId;

    /**
     * 摄像机code*(用于查询所选用户是否有该摄像机权限)
     * @required true
     */
    private String deviceCode;

    /**
     * 市编码
     * @required true
     */
    private String cityId;

    /**
     * 县编码
     * @required true
     */
    private String countyId;

    /**
     * 省编码
     * @required true
     */
    private String provinceId;

    /**
     * 调度选择人标记位（行业为1）
     */
    private String statusFlag;

    /**
     * 是否转派标识（0-否，1-是）
     */
    private String turnFlag;

    /**
     * 真实环节类型(1研判2调度4处置5核实6办结)
     */
    private String realLinkType;

    /**
     * 网格ID
     * @required true
     */
    private String gridId;

    /**
     * 是否有网格员（1：有）
     * @required false
     */
    private String gridFlag;

    /**
     * 是否过滤数据权限（0否,1是）
     * @required false
     */
    private String dataFilterPermiss;

    /**
     * 是否展示客户管理员（0否,1是）
     * @required false
     */
    private String customAdmin;

    /**
     * 用户昵称（用于选择处理人模糊查询）
     * @required false
     */
    private String userName;

    /**
     * 流程环节属性id
     * @required false
     */
    private String attrId;

    /**
     * 是否开启受理模式（0:不开启 1:开启）
     * @required false
     */
    private String enableAcceptanceMode;

    /**
     * 受限时限
     * @required false
     */
    private String timeLimitAcceptance;

    /**
     * 告警类型id
     */
    private String warningTypeId;


}
