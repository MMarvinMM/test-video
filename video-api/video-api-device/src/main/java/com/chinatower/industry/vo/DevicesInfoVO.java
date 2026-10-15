package com.chinatower.industry.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DevicesInfoVO implements Serializable {
    //摄像机编码
    private String deviceCode;

    //是否在线状态 0：在线 1：离线
    private String isOnline;
}
