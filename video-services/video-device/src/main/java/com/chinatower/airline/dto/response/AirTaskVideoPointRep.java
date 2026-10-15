package com.chinatower.airline.dto.response;

import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/7 20:40
 */
@Data
public class AirTaskVideoPointRep {
    private Integer nestConnected;
    private String saveDateTime;
    private List<AircraftLocationRep> aircraftLocationRep;
    private List<AircraftMsgRep> aircraftMsgRep;
}
