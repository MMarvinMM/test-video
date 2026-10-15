package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 雷达船舶画像返回实体类
 */
@Data
public class RadarShipPortraitRadarRsp implements Serializable {

    /**
     * 是否可以生成画像
     */
    private Boolean isGenerateShipPortraitRadar;

    /**
     * 变更mmsi list
     */
    private List<String> multiAisBehavior;

    /**
     * 雷达目标信息
     */
    private RadarCommonShipPortraitRadarParam radarLatestPosition;

    /**
     * 高速行为
     */
    private RadarCommonShipPortraitRadarParam highSpeedBehavior;

    /**
     * 捕鱼行为
     */
    private RadarCommonShipPortraitRadarParam fishingBehavior;

    /**
     * 搭靠行为
     */
    private RadarCommonShipPortraitRadarParam approachBehavior;

    /**
     * 船速异常变化
     */
    private RadarCommonShipPortraitRadarParam speedAbnormal;
}
