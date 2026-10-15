package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InspectionRouteRelLayerSdkVO extends BaseDataScopeEntity implements Serializable {

    /**
     *图层编码
     * @required true
     */
    private String layerId;
    /**
     *图层名称
     */
    private String layerName;
    /**
     * 图斑编码
     * @required true
     */
    private String featureId;
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
     * 通道名稱
     */
    private String channelName;
    /**
     * 预置位编码
     */
    private String presetId;
    /**
     * 停留时长
     */
    private Integer durationTime;
    /**
     * 停留时长单位
     */
    private String durationUnit;

    /**
     * 摄像机经度
     */
    private String longitude;
    /**
     * 摄像机纬度
     */
    private String latitude;
    /**
     * 电子围栏点集合 json
     */
    private String polygonPoints;

    /**
     * 地类名称
     */
    private String landName;

    /**
     * 坐落单位
     */
    private String locationUnit;

    /**
     * 框图
     */
    private String frontImgUrl;

    /**
     * 巡检路线编码
     */
    private Long inspectionRouteId;

    /**
     * 巡检路线下具体某一点位编码
     */
    private Long inspectionRouteSubId;

    /**
     * 排序序号
     */
    private Integer sortSeq;

    /**
     * 巡检状态 1-开始 2-更新 3-暂停 4-恢复 5-重置 6-结束
     */
    private Integer status;

    /**
     * 图斑编码
     */
    private String tbbh;
}
