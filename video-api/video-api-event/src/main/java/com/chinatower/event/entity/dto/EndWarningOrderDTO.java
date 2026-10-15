package com.chinatower.event.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: EndWarningOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/30 16:48
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class EndWarningOrderDTO implements Serializable {

    /**
     * 告警事件编码
     * @required true
     */
    @NotNull(message = "warningOrderId不能为空")
    private String warningOrderId;

    /**
     * 当前环节ID
     * @required true
     */
    @NotNull(message = "currentLink不能为空")
    private String currentLink;
}
