package com.chinatower.event.dto;

import com.chinatower.common.core.dto.BasePageDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: WarningOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/10 14:25
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class WarningOrderDTO extends BasePageDTO implements Serializable {

    //告警ID
    private String warningOrderId;

    //主告警ID
    private String mainAlarmId;
}
