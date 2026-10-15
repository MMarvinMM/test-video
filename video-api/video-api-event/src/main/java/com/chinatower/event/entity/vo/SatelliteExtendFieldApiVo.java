package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SatelliteExtendFieldApiVo implements Serializable {
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
}
