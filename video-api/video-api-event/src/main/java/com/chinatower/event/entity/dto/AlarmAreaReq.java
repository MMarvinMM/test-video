package com.chinatower.event.entity.dto;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2025/4/29 9:35
 */

@Data
public class AlarmAreaReq {

    /**
     * 摄像机code
     */
    private String deviceCode;

    /**
     * 通道code
     */
    private String channelCode;

}
