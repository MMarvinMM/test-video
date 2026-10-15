package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoInspectionRelVO extends BaseDataScopeEntity implements Serializable {

     private String deviceCode;

     private String deviceName;

     private String channelCode;

     private String channelName;

     private String presetId;

     private String presetName;

     private Integer durationTime;

     private String durationUnit;

     private Long inspectionRouteId;

     private Long inspectionRouteSubId;

     private Integer sortSeq;

     private Integer routeStatus;

     private Date pauseStartTime;

     private Date inspectionStartTime;

     private Integer pauseTime;
}
