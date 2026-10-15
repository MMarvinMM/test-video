package com.chinatower.video.api.entity;

import lombok.Data;

@Data
public class QueryPatrolListData {
    /**
     * 预置位id
     * @required true
     */
    private int presetId;
    /**
     * 预置位名称
     * @required true
     */
    private String presetName;
    /**
     * 停留时间
     * @required true
     */
    private int staySeconds;
    /**
     * 巡航速度
     * @required true
     */
    private int speed;
    /**
     * 预置位排序
     * @required true
     */
    private int sort;
    /**
     * 巡航路径Id
     * @required false
     */
    private Long patrolId;
    private String channelCode;
    private String deviceCode;
    private String channelName;
}
