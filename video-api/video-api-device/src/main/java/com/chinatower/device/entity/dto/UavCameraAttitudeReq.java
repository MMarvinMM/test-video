package com.chinatower.device.entity.dto;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2024/12/4 9:49
 */
@Data
public class UavCameraAttitudeReq {
    private String pitch; // 俯仰轴角度
    private String roll; // 横滚轴角度
    private String head; // 偏航轴角度
    private Number radio; // 云台变焦
    private Number lens; // 云台焦距
}
