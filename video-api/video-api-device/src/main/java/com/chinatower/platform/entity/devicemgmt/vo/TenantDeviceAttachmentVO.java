package com.chinatower.platform.entity.devicemgmt.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TenantDeviceAttachmentVO implements Serializable {

    private Long id;

    private String deviceCode;

    private Integer deviceType;

    private String fileUrl;

    private String tenantId;

    private String industryCode;

    private String appVerCode;

    private String creator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
}
