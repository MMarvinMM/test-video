package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoInspectionDetailVO implements Serializable {

     private Long inspectionRouteId;

     private String inspectionRouteName;

     /**
      * 是否暂停巡航 0-否 1-是
      */
     private Integer pauseCruiseFlag;

     private List<VideoInspectionRelVO> inspectionRelList;
}
