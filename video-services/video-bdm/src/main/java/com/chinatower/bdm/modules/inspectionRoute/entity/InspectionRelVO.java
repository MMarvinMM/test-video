package com.chinatower.bdm.modules.inspectionRoute.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 巡检路线关联电子围栏实体类
 */
@Data
public class InspectionRelVO implements Serializable {

    /**
     * 自增ID
     */
    private Long id;

    /**
     * 电子围栏ID
     */
    private String polygonId;

    /**
     * 巡检路线ID
     */
    private Long inspectionRouteId;

    /**
     * 巡检路线排序序号
     */
    private Integer sortSeq;

    /**
     * 图层编码
     */
    private String layerId;

    /**
     * 图层名称
     */
    private String layerName;

    /**
     * 图斑ID
     */
    private String featureId;

    /**
     * 图斑面积
     */
    private String plotArea;

    /**
     * 图斑编号
     */
    private String tbbh;

    /**
     * 摄像机编号
     */
    private String deviceCode;

    /**
     * 摄像机名称
     */
    private String deviceName;

    /**
     * 通道编号
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 预位置名称
     */
    private String presetName;

    /**
     * 预置位ID
     */
    private String presetId;

    /**
     * 电子围栏名称
     */
    private String polygonName;

    /**
     * 电子围栏排序
     */
    private Integer polygonNo;

    /**
     * 电子围栏前端排序id
     */
    private String monitorAreaId;

    /**
     * 停留时长
     */
    private Integer durationTime;

    /**
     * 停留时长单位
     */
    private String durationUnit;

    /**
     * 图层geoserver地址
     */
    private String geoserverUrl;

    /**
     * 图层文件地址
     */
    private String fileUrl;

    /**
     * 图层最大 X
     */
    private Double maxx;

    /**
     * 图层最大 Y
     */
    private Double maxy;

    /**
     * 图层最小 X
     */
    private Double minx;

    /**
     * 图层最小 Y
     */
    private Double miny;

    /**
     * 图斑元数据
     */
    private String properties;

    /**
     * 图斑GeoJson字符串
     */
    private String geometry;

    /**
     * 电子围栏区域点集合 json
     */
    private String polygonPoints;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;

    private String tenantId;
    private String industryCode;
    private String appVerCode;

    private List<LayerTreeVO> layerInfoList;


}
