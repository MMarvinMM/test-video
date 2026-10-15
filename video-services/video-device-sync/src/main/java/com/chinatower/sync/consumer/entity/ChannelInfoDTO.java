package com.chinatower.sync.consumer.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChannelInfoDTO {
    @JsonProperty("channelCode")
    private String channelCode;
    @JsonProperty("channelName")
    private String channelName;
    @JsonProperty("facade")
    private String facade;
    @JsonProperty("channelType")
    private String channelType;
    @JsonProperty("streamType")
    private String streamType;
    @JsonProperty("maxPreset")
    private int maxPreset;
    @JsonProperty("maxPatrol")
    private int maxPatrol;
    @JsonProperty("visualRange")
    private String visualRange;
    @JsonProperty("horizRange")
    private String horizRange;
    @JsonProperty("vertiRange")
    private String vertiRange;
    @JsonProperty("deviceCode")
    private String deviceCode;
    @JsonProperty("channelStatus")
    private String channelStatus;


    @JsonProperty("businStatus")
    private Integer businStatus;


    @JsonProperty("deleteFlag")
    private String deleteFlag;
}
