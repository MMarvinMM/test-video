package com.chinatower.event.vo.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName: channelVideoVO
 * @Description: 通道录像实体
 * @Author: liuxt
 * @Date: 2021-05-11 11:51
 **/
@Data
@ToString
public class PTZVO {
    //设备编号
    private String deviceCode;

    //通道编号
    private String channelCode;

    //P参数 ⽔平参数，范围[0, 3600]
    private int pan;

    //T参数 垂直参数，范围[-1800, 1800]
    private int tilt;

    //Z参数 变倍参数，范围[0, 254]
    private int zoom;

    //客户唯一编码
    private String customerCode;

    //用户编码
    private String userCode;

    //订购关系编码
    private String orderCode;
}
