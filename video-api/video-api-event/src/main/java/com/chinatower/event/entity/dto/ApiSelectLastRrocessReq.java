package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiSelectLastRrocessReq implements Serializable {

    /**
     * 工单号
     * @required true
     */
    private String orderId;

    /**
     * 当前环节ID
     * @required true
     */
    private String linkId;
}
