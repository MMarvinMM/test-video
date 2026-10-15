package com.chinatower.product.api.modules.videosquare.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName: PlatformControlVO
 * @Description: 云台控制VO
 * @Author: zhaozj
 * @Date: 2021-05-06 11:05
 **/
@Data
@ToString
public class PlatformControlVO {
    private String lockId;
    private String deviceCode;//设备编号
    private String channelCode;//通道编码
    private String customerCode;
    private int lock;
    private int lockStatus;
    private String token;
    private int duration;
    private int queue;
    private int wait;
    private String endDate;
    private String deleteFlag;
    private String status;
}
