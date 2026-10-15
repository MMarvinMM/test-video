package com.chinatower.common.core.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author jwk
 * @version 1.0
 * @date 2021/6/16 18:47
 * @description
 **/
@Data
public class QueryAccountBalanceRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    // 业务客户编码
    private String custId;

    // 账户编码
    private String accountId;

    private String orderCode;

    @NotNull(message = "业务属性不能为空")
    @NotEmpty(message = "业务属性不能为空")
    private String businessProperty;

    @NotNull(message = "省份编码不能为空")
    @NotEmpty(message = "省份编码不能为空")
    private String provinceCode;

    @NotNull(message = "地市编码不能为空")
    @NotEmpty(message = "地市编码不能为空")
    private String cityCode;
}
