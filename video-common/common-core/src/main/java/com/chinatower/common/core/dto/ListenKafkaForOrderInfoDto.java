package com.chinatower.common.core.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ListenKafkaForOrderInfoDto {
    @NotNull(message = "partnerOrderCode不为空")
    private String partnerOrderCode;
    /**
     * 操作类型：1-新增， 2-删除， 3-修改
     */
    @NotNull(message = "opType不为空")
    private String opType;

}
