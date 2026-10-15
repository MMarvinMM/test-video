package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceTreeNewVO extends BaseDataScopeEntity implements Serializable {
    private String deviceCode;

    private String deviceName;

    private String channelCode;

    private String channelName;

    //摄像头状态
    private String deviceStatus;

    //通道状态
    private String channelStatus;

    private String siteCode;

    private String longitude;

    private String latitude;

    private String labelId;

    private List<DeviceTreeNewVO> channelList;
}
