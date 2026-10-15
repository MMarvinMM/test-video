package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 雷达告警区域查询实体类
 */
@Data
public class RadarAlarmRegionJson implements Serializable {

    private String regionCenter;

    private String regionRange;

    private String regionColor;
}
