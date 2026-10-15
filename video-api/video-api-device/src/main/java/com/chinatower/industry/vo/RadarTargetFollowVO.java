package com.chinatower.industry.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RadarTargetFollowVO implements Serializable {

    private Long id;

    private String deviceCode;

    private Integer status;

    private String targetId;

    private String messageId;

    private String tenantId;

    private String industryCode;

    private String appVerCode;

    private String creator;

    private String modifier;

    private Date createTime;

    private Date updateTime;

    private Integer deleteFlag;

    private String accessNode;
}
