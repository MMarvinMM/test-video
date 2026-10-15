package com.chinatower.industry.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RadarTargetFollowResultVO implements Serializable {

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 处理失败设备编码列表
     */
    private List<String> failDeviceCodeList;
}
