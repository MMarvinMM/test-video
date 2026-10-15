package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 轨迹信息
 */
@Data
public class TrackAllInfo implements Serializable {

    /**
     * 轨迹线
     */
    private String trackLine;

    /**
     * 船舶位置
     */
    private TrackPointInfo pointInfo;

    /**
     * 船舶首次位置
     */
    private TrackPointInfo pointFirstInfo;

    /**
     * 航行状态
     */
    private List<TrackPointInfo> sailStatePointList;

    /**
     * 历史轨迹中间接续的点
     */
    private List<TrackPointInfo> spicePointList;

    /**
     * 总航程
     */
    private List<TrackPointInfo> trackPoints;

    /**
     * 平均船长
     */
    private String averageLen;

    /**
     * 平均航向
     */
    private String averageCourse;

    /**
     * 平均速度
     */
    private String averageSpeed;

    /**
     * 航行总时间
     */
    private String totalTime;

    /**
     * 总航程
     */
    private String totalDistance;

    /**
     * 箭头
     */
    private List<TrackArrowInfo> arrows;

    /**
     * 目标最大速度
     */
    private String maxSpeed;

}
