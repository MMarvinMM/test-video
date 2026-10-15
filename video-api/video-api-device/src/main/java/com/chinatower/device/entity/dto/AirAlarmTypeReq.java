package com.chinatower.device.entity.dto;

import com.chinatower.common.core.dto.BasePageDTO;
import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: AirAlarmTypeDTO.java
 *
 * @author lyy
 * @version v1.7.00
 * @date 2024/11/04 10:56
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class AirAlarmTypeReq extends BasePageDTO{

    /**
     * 告警来源
     */
    private String alarmSource;

    /**
     * 是否开启图片对比
     */
    private String isPhotoContrast;

    /**
     * 查询设备类型 2无人机  1摄像机
     */
    private List<String> appScenarios;

    /**
     * 查询租户维度 还是租户下设备维度
     */
    private String deviceCode;
}
