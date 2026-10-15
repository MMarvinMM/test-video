package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AlarmLableRelDTO
 * @Deacription
 * @Author yhb
 * @Date 2022/12/29 17:20
 * @Version 1.0
 **/
@Data
public class AlarmLableRelDTO implements Serializable {

    /**
     * 告警标签关联ID
     * @required true
     */
    private Long lableRelId;

    /**
     * 标签ID
     * @required true
     */
    private Long lableId;

    /**
     * 标签名称
     * @required true
     */
    private String lableName;

    /**
     * 告警事件编码
     * @required true
     */
    private String waringOrderId;
    private String tableType;
    private String tableTime;
}
