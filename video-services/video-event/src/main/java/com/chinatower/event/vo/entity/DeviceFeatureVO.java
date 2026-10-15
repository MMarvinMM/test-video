package com.chinatower.event.vo.entity;

import lombok.Data;

/**
 * 摄像机告警特征库实体类
 * @author liusq
 */
@Data
public class DeviceFeatureVO {
    private Long id;
    private String deviceCode; //设备编码
    private String devName; //设备名称
    private String warningOrderId;  // 告警事件ID

    private String formattedAddress; // 地址

    private float longitude; //经度
    private float latitude; //纬度

    private float ptzP;
    private float ptzT;

    private float pMax;
    private float pMin;
    private float tMax;
    private float tMin;

    private float horiAzimuthAngle; //水平方位角

    private int status;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 最后修改人
     */
    private String updateBy;

    /**
     * 最后修改时间
     */
    private String updateTime;

    private Integer pageNum;
    private Integer pageSize;

}
