package com.chinatower.platform.project.custom.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;


/**
 * @ClassName: MessagePushVO
 * @Description: 消息推送VO
 * @Author: zhaol
 * @Date: 2021-05-06 11:05
 **/
@Data
@ToString
public class PushDevVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String deviceCode;
    private String deviceName;
    private String status;
    private String categoryCode;
    private String devExInfo;
    private String regionCode;
    private String regionName;
    private String location;
    private String enableSnap;
    private String longitude;
    private String latitude;
    private String collectType;
    private int height;
    private List<PushChanVO> channels;
}
