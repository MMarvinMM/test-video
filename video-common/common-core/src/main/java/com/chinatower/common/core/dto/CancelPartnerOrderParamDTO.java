package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新订单状态
 *
 * @author: fangjun
 */
@Data
public class CancelPartnerOrderParamDTO implements Serializable {

    /**
     * 订单编码.
     */
    private String partnerOrderCode;
    /**
     * 取消原因  取值：对应字典 cancel_cause
     */
    private String cancelCause;

    /**
     * 备注
     */
    private String remark;


}
