package com.chinatower.platform.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.event.entity.dto.OrderFileDO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: OrderFlowRecordVO.java
 *
 * @author lyy
 * @version v1.6.30
 * @date 2023/10/31 10:55
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class OrderFlowRecordEntity extends BaseDataScopeEntity implements Serializable {

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
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date flowNodeTime;

    /**
     * 环节结束时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date flowEndTime;

    /**
     * 是否当前环节  1:是 0:否
     */
    private String ifCurrentLink;

    /**
     * 调度过程的附件
     */
    private String orderFileListVO;

    private List<OrderFileDO> orderFileList;

    /**
     * 处置过程的附件
     */
    private String filesVO;

    private transient List<Map> orderHandleFileList;

    /**
     * 处理人
     */
    private String processId;

    /**
     * 处理人姓名
     */
    private String processName;

    /**
     * 环节选择的所有处理人
     */
    private String processNameList;

    /**
     * 下个环节处理人
     */
    private String nextLinkProcessName;

    /**
     * 小组id
     */
    private String groupId;

    /**
     * 0否  1是
     */
    private String ifTimeout;

    /**
     * 0正常 1删除
     */
    //private String deleteFlag;

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
     * 关闭说明
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
     * 所选下个环节id
     */
    private String nextLinkId;

    /**
     * 所选下个环节名称
     */
    private String nextLinkName;

    /**
     * 流转类型（0默认、 1转派、2回退）
     */
    private String flowLinkType;

    /**
     * 真实环节类型(1研判2调度4处置5核实6办结)
     */
    private String realLinkType;

    /**
     * 是否开启受理模式 (0：不开启 1：开启（待受理）2：已受理 3：受理完成)
     */
    private String enableAcceptanceMode;

    /**
     * 受理人
     */
    private String acceptProcessId;

    /**
     * 工单流转状态
     */
    private String status;

    /**
     * 查看展示按钮 1是0否
     */
    private String ifAttachment;

}
