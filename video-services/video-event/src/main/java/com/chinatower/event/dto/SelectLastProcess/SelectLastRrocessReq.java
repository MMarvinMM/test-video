package com.chinatower.event.dto.SelectLastProcess;

import lombok.Data;

import java.io.Serializable;

@Data
public class SelectLastRrocessReq implements Serializable {

    /**
     * 工单号
     */
    private String orderId;

    private String linkId;
}
