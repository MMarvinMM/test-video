package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;


/**
 * 4.10.中心和属地设备/通道同步-到其他系统-接口拉取 请求实体类
 * 4.26.设备状态同步-到其他系统-接口拉取
 */
@Data
public class AuditDeviceReq implements Serializable{
    /**
     * 开始时间时间戳
     */
    private Long startTime;

    /**
     * 结束时间时间戳
     */
    private Long endTime;

    /**
     * 上次查询出的结果最后一条主数据的唯一ID
     */
    private Long lastDataId;

    /**
     * 业务类型
     * 1 备电
     * 2 换电
     * 5 视联类
     * 7 光伏
     * 0 发电
     * 4 低速充电
     * 6 车电
     * 8 汽车充电
     * 9 边缘网关
     */
    private Integer businessType = 5;

    /**
     * 平台编码
     */
    private String platformCode;
}

