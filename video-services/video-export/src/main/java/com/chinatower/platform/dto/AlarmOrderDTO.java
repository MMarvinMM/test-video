package com.chinatower.platform.dto;

import com.chinatower.event.entity.dto.BaseAlarmOrderDTO;
import com.chinatower.platform.entity.admin.SysUser;
import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: AlarmOrderDTO.java
 *
 * @author lyy
 * @version v1.0.0
 * @date 2022/12/09 15:19
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class AlarmOrderDTO extends BaseAlarmOrderDTO {

    private SysUser sysUser;
}
