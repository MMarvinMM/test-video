package com.chinatower.common.core.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 事件审批表
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-15
 */
@Getter
@Setter
public class ProcessApproveDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 审批单ID
     */
    private String approveId;

    /**
     * 入库时间
     */
    private Date createTime;

    /**
     * 处理单ID
     */
    private String processOrderId;

    /**
     * 工单ID
     */
    private String orderId;

    /**
     * 审批建议
     */
    private String approveResult;

    /**
     * 审批人
     */
    private String approvePerson;

    /**
     * 审批状态 0:审批失败 1:待审批 2:审批通过
     */
    private String approveStatus;

    /**
     * 审批人等级
     */
    private String approveLevel;

    /**
     * 审批时间
     */
    private String approveTime;

    /**
     * 是否逐级审批
     */
    private String ifLevelApprove;

    /**
     * 父级审批单ID
     */
    private Integer parentApproveId;

    /**
     * 最后更新时间
     */
    private Date modifierDate;

    /**
     * 0：正常 1：删除
     */
    private String deleteFlag;


}
