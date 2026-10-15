package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 雷达轨迹数据信息
 */
@Data
public class RadarReviewInfo implements Serializable {

    /**
     * 船舶MMSI: 船舶9位码
     */
    private Integer mmsi;

    /**
     * 目标id
     */
    private String targetId;

    /**
     * 航向
     */
    private Double course;

    /**
     * 朝向
     */
    private Double heading;

    /**
     * 速度
     */
    private Double speed;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 维度
     */
    private Double latitude;

    /**
     * 目标类别
     */
    private String sclass;

    /**
     * 目标的时间戳
     */
    private Long lastTm;

    /**
     * 目标来源
     */
    private List<String> sources;

    /**
     * 船舶长度
     */
    private Double shipLength;

    /**
     * ais船舶名称
     */
    private String vesselName;

    /**
     * 船名
     */
    private String shipName;

    /**
     * 终端
     */
    private String terminal;

    /**
     * 呼号: AIS呼号
     */
    private String callSign;

    /**
     * IMO编号
     */
    private String imo;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 是否在线
     */
    private String isOnline;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 船舶的最大长度
     */
    private Double shipLengthMax;

    /**
     * 长度
     */
    private String len;
}
