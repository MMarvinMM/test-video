package com.chinatower.device.dto;

import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaozj
 * @version 1.5.30
 * @date 2022/11/26
 */
@Data
@ToString
public class DevListForAppExtendVO extends DevListForAppVO implements Serializable {
    private String deviceName;

    private String deviceCode;

    private String channelCode;

    private String status;

    private String channelStatus;

    private String longitude;

    private String latitude;

    private String location;

    private String height;

    private String siteCode;

    private String modelCode;

    private String modelName;

    //是否收藏字段，0未收藏 1已收藏
    private String isMonitor;

    //收藏数量
    private int isMonitorNum = 0;

    private Integer num;

    private int rangeMeter;

    private String distance;

    private List<ChannelInfoVO> children;


}