package com.chinatower.device.entity.dto;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/5 16:05
 */
@Data
public class AirResultTaskReq extends PageDTO {
    private String accessNode;
    private String taskRecordId;
    private List<String> taskRecordIds;
    private String deviceCode;
    private List<String> flightRouteId;
    private String flightRouteName;
    private String taskName;
    private String picType;
    private List<String> picTypes;
    private List<String> photoIds;
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
    private String refly;
    private String executeStartTime;
    private String executeEndTime;
    private String createStartTime;
    private String createEndTime;
    private String photoName;
    private String videoName;
    private String originalTaskRecordId;
    private Double longitude; // 经度
    private Double latitude; // 纬度
    private Double rangeValue;
}
