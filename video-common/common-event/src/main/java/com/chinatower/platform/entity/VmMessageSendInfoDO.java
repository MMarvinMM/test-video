package com.chinatower.platform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 消息发送通知信息表
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-25
 */
@Getter
@Setter
@TableName("vm_message_send_info_t")
public class VmMessageSendInfoDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    private String uniId;

    /**
     * 告警ID
     */
    private String msgId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 通知类型:枚举值类型72
     * 1	系统消息
     * 2	运营研判消息
     * 3	领导批示消息
     * 4	巡查任务消息
     */
    private String notifyType;

    /**
     * 通知标题
     */
    private String notifyTitle;

    /**
     * 是否已读：1是0否
     */
    private String ifRead;

    /**
     * 告警事件状态：0虚假告警完结 1 待研判    2 派单中    3抢单中    4 待处置  5待核实 6工单完成  7重复告警完成
     */
    private String orderStatus;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date creatorDate;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date modifierDate;

    /**
     * 1正常；0删除
     */
    private String deleteFlag;

    /**
     * 地址
     */
    private String address;

    /**
     * 告警类型名称
     */
    private String warningTypeName;

    /**
     * 告警事件ID
     */
    private String orderId;

    /**
     * 审批人ID
     */
    private String auditId;

}
