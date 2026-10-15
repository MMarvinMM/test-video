package com.chinatower.video.api.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 功能说明: 视频管理-简约模式，抓图接口入参实体
 * @param:
 * @return:
 * @author: hued
 * @date: 2022年10月10日16:29:42
 * @requestID  R-ZBZB-202210080955525 视频管理-实时视频增加简约模式
 */
@Data
@ToString
public class SnapShotSimpleReq implements Serializable {
    private String deviceCode;
    private String deviceName;
    private String channelCode;
    private String channelName;
    //通道状态，0在线，1离线
    private String status;
}
