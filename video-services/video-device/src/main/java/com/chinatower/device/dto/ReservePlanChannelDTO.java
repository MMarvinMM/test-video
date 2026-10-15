package com.chinatower.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @ClassName ReservePlanChannelDTO
 * @Deacription 预案通道关系实体
 * @Author chenmf
 * @Date 2022/4/18 15:16
 * @Version 1.0
 **/
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservePlanChannelDTO implements Serializable {
    
    //预案id
    private String planId;
    
    //设备编码
    private String deviceCode;
    
    //通道编码
    private String channelCode;
    
    //码流类型  1-主码流；2-子码流
    private int streamType;
    
    //是否调用预置位：0否/预置位ID
    private String ifPreset;
    
    //预置位名称
    private String presetName;
    
    //顺序
    private String chanOrder;
    
    private String longitude;
    private String latitude;
    
    private String deviceName;
    private String channelName;
}
