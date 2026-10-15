package com.chinatower.sync.consumer.entity;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/1/16 16:15
 */
@Data
public class BreakPointReq {
    private Double longitude;
    private Double latitude;
    private Double altitude;
}
