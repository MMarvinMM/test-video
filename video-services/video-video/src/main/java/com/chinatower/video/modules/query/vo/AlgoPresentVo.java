package com.chinatower.video.modules.query.vo;

import com.alibaba.fastjson.JSONArray;
import com.chinatower.platform.entity.device.AlgorithmMonitorAreaVO;
import com.chinatower.platform.entity.device.OilGasPipelinesVO;
import com.chinatower.platform.entity.device.TextLabelVO;
import com.chinatower.platform.entity.device.TripThreadVO;
import com.chinatower.video.modules.query.dto.AlarmAreaPoint;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgoPresentVo implements Serializable {

    /**
     * 1-算法相关  2-告警屏蔽区域
     */
    private String type;

    private List<String> ids;

    private String deviceCode;

    private String channelCode;

    /**
     * p值 设备
     */
    private String pointsP;
    /**
     * t值 设备
     */
    private String pointsT;
    /**
     * z值 设备
     */
    private String pointsZ;

    /**
     * area_id
     */
    private Integer areaId;

    private String points;

    /**
     * 根据点位及摄像机ptz所计算出的经纬度
     */
    private String positionInformation;

    private String positionInformationOld;

    private JSONArray positionInformationList;

    private String businessId;

    private String configId;

    /**
     * 识别区域、屏蔽区域、基准区域 经纬度点
     */
    private String pointsLonLat;


    /**
     * 绊线经纬度点
     */
    private String tripThreadLonLat;

    /**
     * 管道经纬度点
     */
    private String oilGasPipelinesLonLat;

    /**
     * 文本经纬度点
     */
    private String textLabelsLonLat;

    private List<AlarmAreaPoint> pointsAlarmLonLatList;

    /** 关联区域 */
    List<AlgorithmMonitorAreaVO> monitorArea;

    /** 绊线集合 */
    private List<TripThreadVO> tripThreadList;

    /** 油气管线集合 */
    private List<OilGasPipelinesVO> oilGasPipelinesList;

    /**
     * 文本标签集合
     */
    private List<TextLabelVO> textLabelList;
}
