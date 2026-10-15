package com.chinatower.platform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 告警事件工单流程表
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-11
 */
@Getter
@Setter
public class IOrderFlowRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单流程ID
     */
    private String orderFlowId;

    /**
     * 工单ID
     */
    private String orderId;

    /**
     * 环节ID
     */
    private String linkId;

    /**
     * 环节名称
     */
    private String linkName;

    /**
     * 流程记录名称
     */
    private String flowRecordName;

    /**
     * 环节开始时间
     */
    private Date flowNodeTime;

    /**
     * 环节结束时间
     */
    private Date flowEndTime;

    /**
     * 是否当前环节  1:是 0:否
     */
    private String ifCurrentLink;

    /**
     * 处理人
     */
    private String processId;

    /**
     * 处理人姓名
     */
    private String processName;

    /**
     * 小组id
     */
    private String groupId;

    /**
     * 0否  1是
     */
    private String ifTimeout;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 0正常 1删除
     */
    private String deleteFlag;

    /**
     * 处理单ID
     */
    private String processOrderId;

    /**
     * 审批单ID
     */
    private String approveOrderId;

    /**
     * 操作备注
     */
    private String remark;

    /**
     * 完成时限，单位：分钟
     */
    private Integer timeLimit;

    /**
     * 语音
     */
    private String voice;

    /**
     * 所选下个环节id
     */
    private String nextLinkId;

    /**
     * 所选下个环节名称
     */
    private String nextLinkName;


}
