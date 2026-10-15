package com.chinatower.event.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: OrderFlowRecordDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/4/12 10:55
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class OrderFlowRecordDTO extends BaseDataScopeEntity implements Serializable {

    /**
     * 事件ID
     */
    private String orderId;

    /**
     * 工单流程ID
     */
    private String orderFlowId;

    /**
     * 表名
     */
    private String tableName;

    //处理单ID
    private String processOrderId;

    //环节ID
    private String linkId;

    //环节开始时间
    private String nodeTime;

    /**
     * 真实环节类型(1研判2调度4处置5核实6办结)
     */
    private String realLinkType;

}
