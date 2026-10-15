package com.chinatower.airline.dto.response;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2024/11/7 20:45
 */
@Data
public class AircraftMsgRep {
    private Double gimbalPitch;
    private Double horizontalSpeed;
    private Double aircraftHeadDirection;
    private Double roll;
    private Double pitch;
    private Double verticalSpeed;
}
