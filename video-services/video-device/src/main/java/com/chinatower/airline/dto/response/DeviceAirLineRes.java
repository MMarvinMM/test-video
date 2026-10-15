package com.chinatower.airline.dto.response;

import com.chinatower.device.entity.vo.AirLineVo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/3/11 10:05
 */
@Data
public class DeviceAirLineRes {
    private String deviceCode;
    private String uavName;
    private String uavCode;
    private String uavNestCode;
    private String uavNestName;
    private String uavNestAltitude;
    private List<AirLineVo> airLineVos;
}
