package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：督办记录信息对象
 * <p>
 * 类名称: HandleRecordVO.java
 *
 * @author lyy
 * @version v1.0.0
 * @date 2024/06/26 11:09
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class HandleRecordVO implements Serializable {

    /**
     * 工单ID
     */
    private String orderId;

    /**
     * 工单流程ID
     */
    private String orderFlowId;

    /**
     * 督办环节ID
     */
    private String linkId;

    /**
     * 督办环节名称
     */
    private String handleLinkName;

    /**
     * 督办人ID
     */
    private String handleUserId;

    /**
     * 督办人姓名
     */
    private String handleUserName;

    /**
     * 督办人手机号
     */
    private String phoneNumber;

    /**
     * 督办时间
     */
    private String handleTime;

    /**
     * 督办意见
     */
    private String handleRemark;

    /**
     * 删除标识
     */
    private String deleteFlag;

}
