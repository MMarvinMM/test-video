package com.chinatower.bdm.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoInspectionRelBriefInfoVO implements Serializable {

     private String deviceCode;

     private String channelCode;

     private String presetId;

     /**
      * 暂停巡航标识 1-是 0-否
      */
     private Integer pauseCruiseFlag;
}
