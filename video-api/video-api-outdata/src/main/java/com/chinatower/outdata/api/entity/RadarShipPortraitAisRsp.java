package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AIS船舶画像返回实体类
 */
@Data
public class RadarShipPortraitAisRsp implements Serializable {

    /**
     * 捕鱼行为
     */
    private List<RadarCommonBehavior> fishingBehavior;

    /**
     * 一船多码 变更mmsi list
     */
    private List<String> multiAisBehavior;

    /**
     * 搭靠行为
     */
    private List<RadarCommonBehavior> approachBehavior;

    /**
     * AIS设备开关异常
     */
    private List<RadarCommonBehavior> aisAbnormalBehavior;

    /**
     * AIS信息变更
     */
    private List<RadarAisInfoChangeBehavior> aisInfoChangeBehavior;

    /**
     * 套牌船 目标唯一标识list
     */
    private List<String> fakeBehavior;

    /**
     * 敏感区域出没
     */
    private List<RadarRegionalOccurrenceBehavior> regionalOccurrenceBehavior;

}
