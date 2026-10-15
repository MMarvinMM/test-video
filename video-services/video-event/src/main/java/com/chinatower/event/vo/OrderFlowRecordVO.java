package com.chinatower.event.vo;

import com.chinatower.event.entity.dto.OrderFileDO;
import com.chinatower.platform.entity.admin.UserInfoDto;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: OrderFlowRecordVO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/4/12 11:08
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class OrderFlowRecordVO implements Serializable {

    /**
     * 环节开始时间
     */
    private String flowNodeTime;

    /**
     * 环节结束时间
     */
    private String flowEndTime;
    /**
     * 处理单ID
     */
    private String processOrderId;

    /**
     * 审批单ID
     */
    private String approveOrderId;
    /**
     * 语音
     */
    private String voice;
    /**
     * 处理人
     */
    private String processId;
    /**
     * 处理人姓名
     */
    private String processName;

    /**
     * 操作备注
     */
    private String remark;
    /**
     * 状态
     */
    private String status;

    /**
     * 处置人
     */
    private String handleProcessName;

    /**
     * 处置时间
     */
    private String handleTime;

    /**
     * 处理备注
     */
    private String handleRemark;

    /**
     * 关闭说明
     */
    private String closeRemark;

    /**
     * 调度人名称
     */
    private String dispatchProcessName;

    /**
     * 调度时间
     */
    private String dispatchTime;

    /**
     * 调度备注
     */
    private String dispatchRemark;

    /**
     * 手机号
     */
    private String mobilePhone;

    private List<UserInfoDto> userInfoList;

    /**
     * 附件信息
     */
    private List<OrderFileDO> files;

    /**
     * 所选下个环节名称
     */
    private String nextLinkName;

    /**
     * 流转类型（0默认、 1转派、2回退）
     */
    private String flowLinkType;

    private String realLinkType;

    /**
     * 所选下个环节id
     */
    private String nextLinkId;

    /**
     * 下个环节处理人集合
     */
    private String nextLinkProcessName;

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
     * 受理模式
     */
    private String enableAcceptanceMode;

}
