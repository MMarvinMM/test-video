package com.chinatower.industry.dto;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import lombok.Data;

@Data
public class SensorDeviceCountReqDTO extends CommonFeignEntity {

    /**
     * 设备类型 1-雷达 2-物联设备
     * @required true
     */
    private Integer categoryCode;
}
