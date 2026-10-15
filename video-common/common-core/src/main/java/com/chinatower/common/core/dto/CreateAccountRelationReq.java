package com.chinatower.common.core.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author jwk
 * @version 1.0
 * @date 2021/9/8 14:35
 * @description
 **/
@Data
public class CreateAccountRelationReq {
    @NotNull(message = "业务客户编码不能为空")
    @NotEmpty(message = "业务客户编码不能为空")
    private String custId;

    @NotNull(message = "订单编码不能为空")
    @NotEmpty(message = "订单编码不能为空")
    private String orderCode;

    private String operatorName;
}
