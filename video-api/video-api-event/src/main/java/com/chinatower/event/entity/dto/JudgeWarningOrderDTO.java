package com.chinatower.event.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: JudgeWarningOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/31 15:01
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class JudgeWarningOrderDTO implements Serializable {
    /**
     * 告警事件编码
     * @required true
     */
    private String warningOrderId;

    /**
     * 流程ID
     * @required true
     */
    @NotNull(message = "flowId不能为空")
    private String flowId;

    /**
     * 当前环节ID
     * @required true
     */
    @NotNull(message = "currentLink不能为空")
    private String currentLink;

}
