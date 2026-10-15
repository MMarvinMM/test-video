package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AlarmLableRelAPIDTO
 * @Deacription
 * @Author yhb
 * @Date 2022/12/29 17:20
 * @Version 1.0
 **/
@Data
public class AlarmLableRelAPIDTO implements Serializable {

    private Long lableRelId;
    private Long lableId;
    private String lableName;
    private String waringOrderId;
    private String tableType;
    private String tableTime;
}
