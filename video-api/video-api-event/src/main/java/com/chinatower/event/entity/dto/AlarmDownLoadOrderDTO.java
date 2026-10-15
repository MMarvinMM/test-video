package com.chinatower.event.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: AlarmOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/21 09:34
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class AlarmDownLoadOrderDTO extends BaseAlarmOrderDTO {

    private String[] orderIds;

    /**
     * 是否开启权限调用（1：开启 空：不开启）
     */
    private String powerFlag;

    private String processId;

    /**
     * 是否指定选择人
     */
    private String beforeDispatchFlag;

    /**
     * 加密deOrderId
     */
    private String deOrderId;

    private String timeLimit;

    private List<AlarmLableRelDTO> alarmLableRelDTOList;

    private List<AreaCodeReq> areaCodeList;
}
