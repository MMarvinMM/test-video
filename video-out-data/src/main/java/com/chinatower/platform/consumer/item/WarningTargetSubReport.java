package com.chinatower.platform.consumer.item;

import lombok.Data;

import java.io.Serializable;

@Data
public class WarningTargetSubReport implements Serializable {
    // 目标编码
    private String targetId;
    // mmsi
    private String MMSI;
}
