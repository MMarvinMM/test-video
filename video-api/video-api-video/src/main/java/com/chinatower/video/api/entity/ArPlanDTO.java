package com.chinatower.video.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArPlanDTO implements Serializable {

    private Long id;

    private String arViewId;

    private String executionStatus;

    private String executionStep;

    private Integer stayDuration;

    private Integer orderNum;

    private String coordinate;

    private Integer resolutionX;

    private Integer resolutionY;

    private String channelCode;

    private String tagId;

    private Integer presetId;

    private Long arCruisePlanRelId;

    private String tagClass;

    private String longitude;

    private String latitude;

    private String tenantId;

    private String industryCode;
}
