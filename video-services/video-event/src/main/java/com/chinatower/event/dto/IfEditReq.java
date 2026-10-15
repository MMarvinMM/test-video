package com.chinatower.event.dto;

import lombok.Data;

@Data
public class IfEditReq {

    private String warningOrderId;

    private String userId;

    private String tableName;
}
