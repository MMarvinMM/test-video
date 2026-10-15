package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class AlgorithmGiveSyncVO{
    //联动策略id
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer alarmLinkageId;
    //告警策略状态 1-启用；0-停用
    private Integer alarmLinkageStatus;
    //是否需要二次分析
    private SecondAnalysisVO secondAnalysis;
    //设备编码
    private String deviceCode;
    //通道编码
    private String channelCode;
    //抓拍设备通道
    private List<DeviceChannelVO> pictureInfo;
    //录像设备通道
    private List<DeviceChannelVO> videoSetInfo;
    //受理节点编码
    private String acceptNode;
    //接入节点编码
    private String accessNode;

    private List<ThresholdConfig> thresholdConfig;

    public AlgorithmGiveSyncVO() {
        this.alarmLinkageStatus = 1;
    }
}
