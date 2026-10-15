package com.chinatower.sync.consumer.entity;

import lombok.Data;

import java.util.List;

@Data
public class ChannelCanalDTO {
    private String returnCode;
    private String returnMsg;
    private String errorCode;
    private String errorMsg;
    private List<DeviceInfoDTO> data;
}
