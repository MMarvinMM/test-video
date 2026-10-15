package com.chinatower.event.dto;

import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：督办操作接口入参实体
 * <p>
 * 类名称: SendSupervisionMsgDTO.java
 *
 * @author lyy
 * @version v1.6.80
 * @date 2024/06/25 09:34
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class SendSupervisionMsgDTO {

    /**
     * 告警编码
     */
    private String orderId;

    /**
     * 督办意见
     */
    private String remark;

}
