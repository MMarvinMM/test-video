package com.chinatower.airline.dto.request;

import com.chinatower.device.entity.vo.AirLineVo;
import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/3/17 11:07
 */
@Data
public class AirUavInfo {
    private String uavCode;
    private String uavName;
    private String uavNestCode;
    private String uavNestName;
    private List<AirLineVo> airLineVoList;
    private String uavNestType;
    private String uavNestTypeCode;
    private String uavNestLongitude;
    private String uavNestLatitude;
}
