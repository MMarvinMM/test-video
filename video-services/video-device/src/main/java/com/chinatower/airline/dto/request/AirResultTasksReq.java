package com.chinatower.airline.dto.request;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/5 16:05
 */
@Data
public class AirResultTasksReq extends PageDTO {
    private String accessNode;
    private List<String> taskRecordId;
    private String deviceCode;
    private List<String> flightRouteId;
    private String flightRouteName;
    private String taskName;
    private List<String> picType;
    private String acceptNode;
    private String videoStartTime;
    private String videoEndTime;
    private Double duration;
    private Integer pageNo;
    private Integer pageSize;
    private Integer page;
    private Integer limit;
    private Integer sort;
    private String order;
    private String isCollect;
    private String isReFly;
    private String createStartTime;
    private String createEndTime;
    private String photoName;
    private String videoName;
}
