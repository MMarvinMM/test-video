package com.chinatower.platform.entity.device;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 级联平台绑定摄像机下发信息
 */
@Data
public class CasCadeDeviceBindVO implements Serializable {

    private Long id;

    private String tenantId;

    private String platCode;

    private String permissionType;

    private String deviceCode;

    private String channelCode;

    private String customChannelCode;

    private String orgCode;

    private String creator;

    private String modifier;

    private Date createTime;

    private Date updateTime;

}
