package com.chinatower.platform.entity.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AlgorithmGiveConfigAdd {

    /**
     * 主键
     */
    private int id;

    /**
     * 设备编码
     */
    private String deviceCode;
    
    /**
     * 设备名称
     */
    private String deviceName;
    
    /**
     * 通道编码
     */
    private String channelCode;
    
    /**
     * 通道名称
     */
    private String channelName;
    
    /**
     * 是否二次研判 0：是  1：否
     */
    private String secondSwitch;
    
    /**
     * 告警类型编码
     */
    private String alarmType;
    
    /**
     * 告警类型名称
     */
    private String alarmTypeName;
    
    /**
     * 后端算法编码
     */
    private String algorithmCode;
    
    /**
     * 后端算法名称
     */
    private String algorithmName;
    
    /**
     * 触发规则
     */
    private String triggerRuleStr;
}
