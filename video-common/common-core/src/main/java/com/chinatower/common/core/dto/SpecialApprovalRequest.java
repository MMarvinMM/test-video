package com.chinatower.common.core.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author jwk
 * @version 1.0
 * @date 2021/8/3 14:13
 * @description
 **/
@Data
public class SpecialApprovalRequest implements Serializable {

    private String operatorName;

    @NotNull(message = "审批单名称不能为空")
    @NotEmpty(message = "审批单名称不能为空")
    private String processName;

    @NotNull(message = "订单编号不能为空")
    @NotEmpty(message = "订单编号不能为空")
    private String orderCode;

    @NotNull(message = "特殊审批类型不能为空")
    @Min(value = 0)
    @Max(value = 1)
    private Integer type;

    @NotNull(message = "申请原因不能为空")
    @NotEmpty(message = "申请原因不能为空")
    private String reason;

    @NotNull(message = "订单金额不能为空")
    private Double amount;

    @NotNull(message = "开通时长不能为空")
    @NotEmpty(message = "开通时长不能为空")
    private String duration;

    @NotNull(message = "客户编码不能为空")
    @NotEmpty(message = "客户编码不能为空")
    private String custId;

    @NotNull(message = "客户名称不能为空")
    @NotEmpty(message = "客户名称不能为空")
    private String custName;

}
