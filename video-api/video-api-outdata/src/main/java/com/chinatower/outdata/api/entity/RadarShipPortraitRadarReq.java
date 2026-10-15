package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 雷达船舶画像入参实体类
 */
@Data
public class RadarShipPortraitRadarReq implements Serializable {

    /**
     * 目标ID
     */
    private String targetId;

    /**
     * 目标最后更新时间 2024-05-08 00:00:00
     */
    private String lastDT;


    /**
     * 行为值集合
     * isGenerateShipPortraitRadar:是否可以生成画像 radarLatestPosition: 雷达目标信息
     * highSpeedBehavior: 高速行为 fishingBehavior:捕鱼行为 approachBehavior:搭靠行为 speedAbnormal:船速异常变化
     */
    private List<String> behavior;


}
