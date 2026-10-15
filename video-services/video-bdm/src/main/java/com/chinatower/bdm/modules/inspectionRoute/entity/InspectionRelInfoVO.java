package com.chinatower.bdm.modules.inspectionRoute.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 巡检路线点实体类
 */
@Data
public class InspectionRelInfoVO implements Serializable {

    /**
     * 自增ID
     */
    private Long id;

    /**
     * 巡航线路id
     */
    private Long inspectionRouteId;

    /**
     * 图层编码
     */
    private String layerId;

    /**
     * 图斑编号
     */
    private String featureId;

    /**
     * 电子围栏编码
     */
    private String polygonId;

    /**
     * 摄像机编号
     */
    private String deviceCode;

    /**
     * 通道编号
     */
    private String channelCode;

    /**
     * 预置位ID
     */
    private String presetId;

    /**
     * 巡航路线状态
     */
    private Integer routeStatus;

    /**
     * 巡航开始时间
     */
    private Date inspectionStartTime;

    /**
     * 暂停开始时间
     */
    private Date pauseStartTime;

    /**
     * 停留时长
     */
    private Integer durationTime;

    /**
     * 停留时长单位
     */
    private String durationUnit;

    /**
     * 巡检路线排序序号
     */
    private Integer sortSeq;

    /**
     * 客户编码
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String modifier;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标识
     */
    private Integer deleteFlag;

    /**
     * 暂停时长
     */
    private Integer pauseTime;
}
