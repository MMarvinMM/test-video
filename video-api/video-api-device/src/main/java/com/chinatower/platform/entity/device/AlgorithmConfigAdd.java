package com.chinatower.platform.entity.device;

import lombok.Data;

/**
 * 算法配置实体类
 * 对应数据库表：vm_algorithm_config
 */
@Data
public class AlgorithmConfigAdd {
    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 位置类型：1通道，2预置位
     */
    private Integer posType;

    /**
     * 预置位编号
     */
    private Long presetId;

    /**
     * 预置位名称
     */
    private String presetName;

    /**
     * 算法编号
     */
    private String algorithmCode;

    /**
     * 点位json
     */
    private String points;

    /**
     * 截图
     */
    private String img;

    /**
     * 算法名称
     */
    private String algorithmName;


    /**
     * 置信度阈值
     */
    private Float thresh;

    /**
     * 重复告警间隔
     */
    private Integer alarmInterval;

    /**
     * 重复告警间隔单位:秒s,分M,时h,天d
     */
    private String alarmIntervalType;

    /**
     * 算法运行时间
     */
    private String algorithmTime;

    /**
     * 配置参数
     */
    private String modelConfig;

    /**
     * 绊线
     */
    private String tripThread;

    /**
     * 算法分析时段
     */
    private String analysisPeriod;

    /**
     * 油气管线
     */
    private String oilGasPipelines;

    /**
     * 任务抽帧频率，单位：秒
     */
    private Integer timeInterval;

    /**
     * 抽帧频率单位,1:小时 2:分钟 3:秒
     */
    private Integer timeIntervalUnit;

    /**
     * 文本标签
     */
    private String textLabels;

    /**
     * 是否联动算法 0：否，1：是
     */
    private String isLinkageAlgorithm;

    /**
     * 联动算法信息
     */
    private String linkageAlgorithm;

    /**
     * 算法扩展字段json串，alarmFrameColor：告警框颜色
     */
    private String extendJsonAlgo;

    // 点位 json - 经纬度
    private String pointsLonLat;

    // 绊线 - 经纬度
    private String tripThreadLonLat;

    // 油气管线 - 经纬度
    private String oilGasPipelinesLonLat;

    // 文本标签 - 经纬度
    private String textLabelsLonLat;
}
