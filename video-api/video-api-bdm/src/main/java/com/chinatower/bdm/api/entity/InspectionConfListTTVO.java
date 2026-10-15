package com.chinatower.bdm.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class InspectionConfListTTVO extends InspectionConfListVO {

    private String tenantName;

    private String provinceId;

    private String provinceName;

    private String cityId;

    private String cityName;

    private String taskStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
