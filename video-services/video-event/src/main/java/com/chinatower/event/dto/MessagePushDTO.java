package com.chinatower.event.dto;

import com.chinatower.event.dataobject.MessageSendInfoDO;
import com.chinatower.event.entity.dto.VmFlowLinkAttrDto;
import com.chinatower.platform.entity.request.CommonSmsRequest;
import com.chinatower.platform.entity.request.JgPushRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: MessagePushDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/18 17:36
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class MessagePushDTO extends MessageSendInfoDO implements Serializable {

    /**
     * 消息通知类型，1，APP(废弃，走APP用户维度配置), 2,短信，3，PC,多种方式中间用逗 隔开
     */
    private String msgNotifyType;

    /** 是否通知管理员，0，不通知，1通知 */
    private String notifyAdminFlag;

    /**
     * 用户类型，1用户，2角色
     */
    private String userType;

    /**
     * 角色ID，中间用逗号隔开
     */
    private String roleId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 环节ID
     */
    private String linkId;

    /**
     * 告警类型
     */
    private String typeValue;

    /**
     *  地区编码
     *  省份、地市编码
     */
    private List<String> regionCodes;

    /**
     * 组织列表
     */
    private List<String> orgIdList;

    /**
     * 短信推送方式 短信内容
     */
    private String msg;

    /**
     * 是否通知客户管理员
     */
    private String notifyManager;

    /**
     * 通知环节
     */
    private String notificationLink;

    /**
     * 短信推送类型
     * 1：内网
     * 2：公网
     */
    private String smsType;

    /**
     * 消息内容
     */
    private String msgContent;

    /**
     * 极光推送入参
     */
    private JgPushRequest jgPushRequest;

    /**
     * 短信入参
     */
    private CommonSmsRequest commonSmsRequest;

    /**
     *
     */
    private String reminderType;

    /**
     *
     */
    private String reminderStatus;

    /**
     * 事件ID
     */
    private String msgId;

    /**
     * 流程ID
     */
    private String alarmFlowId;
    /**
     * 模板id
     */
    private String flowTemplate;
    /**
     * 告警时间
     */
    private String alarmTime;

    /**
     * 行业版本编码
     */
    private String industryCode;
    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 领导批示权限
     */
    private String leaderFlag;

    /**
     * 是否在短信发送时间区间（0：是（默认） 1：不是）
     */
    private String msgSendTimeFlag;

    /**
     * 告警类型id
     */
    private String warningTypeId;

    /**
     * 摄像机编码
     */
    private String deviceCode;

    /**市编码*/
    private String cityId;
    /** 县编码 */
    private String countyId;
    /** 省编码 */
    private String provinceId;
    private String warningSource;
    private String realLinkType;
    /**
     * 是否微信推送标记位
     */
    private String wxSendFlag;

    /**
     * 事件流转标记位（1：从事件流转过来 其他：不是从事件流转过来）
     */
    private String flowFlag;

    /**
     * 网格id
     */
    private String gridId;

    /**
     * 流程环节配置属性
     */
    private VmFlowLinkAttrDto vmFlowLinkAttrDto;

    /**
     * 推送极光传入app过滤的环节结束时间
     */
    private String flowOverTime;

    /**
     * 受理状态
     */
    private String enableAcceptanceMode;

    /**
     * 卫片标记位（1：来自卫片）
     */
    private String regionAlarmFlag;

    private String deviceName;

    /**
     * 行业扩展字段
     */
    private String extraField;

    /**
     * 督办人姓名
     */
    private String handleUserName;
}
