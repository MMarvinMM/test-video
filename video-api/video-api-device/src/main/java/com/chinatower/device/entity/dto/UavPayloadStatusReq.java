package com.chinatower.device.entity.dto;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2024/12/4 9:50
 */
@Data
public class UavPayloadStatusReq {
    private String sn; // 挂载SN
    private String status; // 状态
}
