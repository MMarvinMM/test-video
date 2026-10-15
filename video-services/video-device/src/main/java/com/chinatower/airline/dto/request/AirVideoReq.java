package com.chinatower.airline.dto.request;

import lombok.Data;

/**
 * @author WLF
 * @description
 * @date 2025/1/14 15:58
 */
@Data
public class AirVideoReq {
    private String deviceCode; // 设备编码
    private String streamType; // 流协议
    private String netType; // 拉流url的网域类型
    private String videoType; // 视频类型
    private String repubsh; // 是否重新推流标识
    private String accessNode; // 设备的接入节点
    private String acceptNode; // 接受节点
    private String type; // 0：无人机；1：基站
    private String videoQuality; // 视频质量 0 :自适应,1:流畅,2:标清,3:高清,4:超清
}
