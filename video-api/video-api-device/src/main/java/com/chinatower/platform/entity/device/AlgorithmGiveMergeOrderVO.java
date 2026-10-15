package com.chinatower.platform.entity.device;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AlgorithmGiveMergeOrderVO {
    /** 订单编码 */
    @NotNull(message = "订单编码不能为空")
    private String orderCode;
    /** 租户编码 */
    @NotNull(message = "租户编码不能为空")
    private String tenantId;
    /** 行业编码 */
    @NotNull(message = "行业编码不能为空")
    private String industryCode;
    /** 退订设备集合 */
    @NotNull(message = "设备编码不能为空")
    private List<String> deviceCodeList;
}
