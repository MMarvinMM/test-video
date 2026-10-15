package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DelayMessageDto implements Serializable {

    /**
     * 工单号
     * @required true
     */
    private String orderId;

    /**
     * 用户Id
     * @required true
     */
    private String userId;

    /**
     * 行业编码
     * @required true
     */
    private String industryCode;
}
