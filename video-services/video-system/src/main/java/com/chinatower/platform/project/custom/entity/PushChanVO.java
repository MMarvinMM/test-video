package com.chinatower.platform.project.custom.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class PushChanVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String status;
    private String channelCode;
    private String channelName;
    private String facade;
    private String channelType;
    private String streamType;
}
