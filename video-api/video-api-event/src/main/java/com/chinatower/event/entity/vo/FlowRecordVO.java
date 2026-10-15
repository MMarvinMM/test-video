package com.chinatower.event.entity.vo;


import java.util.List;
import java.util.Map;

import com.chinatower.event.entity.dto.OrderFileDO;
import com.chinatower.platform.entity.admin.UserInfoDto;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: FlowRecordVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/6/9 14:16
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class FlowRecordVO{
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
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String flowNodeTime;

    /**
     * 环节结束时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String flowEndTime;

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
     *
     */
    private String closeRemark;

    /**
     * 完成时限，单位：分钟
     */
    private Integer timeLimit;

    /**
     * 语音
     */
    private String voice;

    /**
     * 重复告警ID
     */
    private String duplicateOrderId;

    /**
     * 研判结果
     */
    private String judgeResult;

    /**
     * 处理人ID
     */
    private String warningProcessId;

    /**
     *审批人
     */
    private String approvePerson;

    /**
     * 处置人ID
     */
    private String pendingHandleId;

    /**
     * 处置人名称
     */
    private String pendingHandleName;

    /**
     * 电话
     */
    private String mobilePhone;

    /**
     * 研判人电话
     */
    private String judgedPhone;

    /**
     * 研判人名称
     */
    private String judgedName;

    private String judgeDeptName;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 重复告警 告警内容
     */
    private String duplicateAlarmBody;

    /**
     *
     */
    private String judgedType;

    /**
     * 状态
     */
    private String status;

    /**
     * 处置人名称
     */
    private String handleProcessName;
    /**
     * 环节结束时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String handleTime;

    /**
     *
     */
    private String handleRemark;

    /**
     *
     */
    private String dispatchProcessName;

    /**
     * 下个环节处理人集合
     */
    private String nextLinkProcessName;

    /**
     *
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String dispatchTime;

    /**
     *
     */
    private String dispatchRemark;

    /**
     * 附件列表
     */
    private List<OrderFileDO> orderFileList;

    /**
     * 处置环节附件列表
     */
    private List<Map> orderHandleFileList;

    /**
     * 查看展示按钮 1是0否
     */
    private String ifAttachment;

    /**
     * 调度指定待处理人账号合集
     */
    private String processNameList;
    /**
     * 流转类型（0默认、 1转派、2回退）
     */
    private String flowLinkType;
    /**
     * 真实环节类型(1研判2调度4处置5核实6办结)
     */
    private String realLinkType;

    /**
     * 所选下个环节名称
     */
    private String nextLinkName;

    /**
     * 所选下个环节id
     */
    private String nextLinkId;

    private List<UserInfoDto> userInfoList;

    /**
     * 受理人id
     */
    private String acceptProcessId;

    /**
     * 受理人姓名
     */
    private String acceptProcessName;

    /**
     * 受理人电话
     */
    private String acceptProcessPhone;

    /**
     * 受理状态
     */
    private String enableAcceptanceMode;
}
