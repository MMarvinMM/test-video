package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AIS船舶画像入参实体类
 */
@Data
public class RadarShipPortraitAisReq implements Serializable {

    /**
     * MMSI
     */
    private String param;

    /**
     * 开始时间 2024-05-08 00:00:00
     */
    private String startTime;

    /**
     * 结束 2024-05-08 00:00:00
     */
    private String endTime;

    /**
     * 行为值集合
     * fishingBehavior:捕鱼行为 multiAisBehavior:一船多码 approachBehavior:搭靠行为 aisAbnormalBehavior: AIS设备开关异常
     * aisInfoChangeBehavior:AIS信息变更 fakeBehavior:套牌船 regionalOccurrenceBehavior:敏感区域出
     */
    private List<String> behavior;


}
