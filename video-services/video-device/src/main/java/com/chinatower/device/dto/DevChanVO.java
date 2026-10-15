package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @ClassName DevChanVO
 * @Description
 * @Author zhaozj
 * @Date 2022/11/10
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DevChanVO extends BaseDataScopeEntity {

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

    private List<DevChanVO> list;

    private int rangeMeter;

    private String distance;

    private String channelName;
}
