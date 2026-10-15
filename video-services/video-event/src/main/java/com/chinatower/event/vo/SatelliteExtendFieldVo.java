package com.chinatower.event.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SatelliteExtendFieldVo  implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 火区 id */
    private String fireAreaId;
    /** 火点面积 */
    private String fireArea;
    /** 分布图 */
    private String relativePathPose;
    /** 专题图 */
    private String relativePathThem;
    /** 红外图 */
    private String relativePathMir;
    /** 下垫面类型 */
    private String underlyingSurfaceType;

    private String dataSource;

    /**
     * 联动摄像机code
     */
    private String realDeviceCode;

    /**
     * 抓图时间
     */
    private String snapShotTime;


    /**
     * 前类型
     */
    private String preType;
    /**
     * 后类型
     */
    private String postType;
    /**
     * 告警来源
     */
    private String alarmSource;
    /**
     * 告警类型
     */
    private String warningTypeId;
    /**
     * 告警原因
     */
    private String alarmReason;
    /**
     * 告警时间
     */
    private String alarmTime;
    /**
     * 图斑中心经度
     */
    private String spotCenterLongitude;
    /**
     * 图斑中心唯独
     */
    private String spotCenterLatitude;
    /**
     * 备注
     */
    private String remark;
    /**
     * 图斑编号
     */
    private String spotNo;
    /**
     * 城市名
     */
    private String cityName;
    /**
     * 区县名
     */
    private String countyName;
    /**
     * 乡镇名
     */
    private String townName;
    /**
     * 村名
     */
    private String villageName;
    /**
     * 卫片来源
     */
    private String regionSource;
    /**
     * 前时相
     */
    private String preChronology;
    /**
     * 后时相
     */
    private String postChronology;
    /**
     * 图版面积
     */
    private String spotArea;
    /**
     * 图版周长
     */
    private String spotPerimeter;
    /**
     * 变化类型
     */
    private String changeType;
    /**
     * 事件描述
     */
    private String description;
    /**
     * 面积
     */
    private String area;
    /**
     * 土地利用类型
     */
    private String landUseType;
    /**
     * 影像时间
     */
    private String imageTime;
    /**
     * 是否质检
     */
    private String detectionFlag;

    /**
     * 租户ID
     */
    private String tenantId;

    /** 客户名称 */
    private String tenantName;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 版本号
     */
    private String appVerCode;

    /**
     * 图层id
     */
    private String layerId;

    /**
     * 图斑唯一标识
     */
    private String bsm;

}
