package com.chinatower.platform.entity.devicemgmt.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TenantDeviceLabelDetailVO implements Serializable {

    private Long labelTypeId;

    private String labelTypeName;

    private Long labelId;

    private String labelName;

    private String creator;

    private String modifier;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;
}
