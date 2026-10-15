package com.chinatower.common.core.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: WarningInterceptVo.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/27 09:42
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class WarningInterceptVO implements Serializable {

    /**
     * 主键
     */
    private String id;

    /**
     * 第三方任务ID
     */
    private String taskId;

    /**
     * 告警类型编码
     */
    private String warningTypeId;

    /**
     * 告警类型名称
     */
    private String warningTypeName;

    /**
     * 形成告警时间
     */
    private Date alarmTime;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 地市编码
     */
    private String cityCode;

    /**
     * 县编码
     */
    private String countyCode;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 告警内容
     */
    private String alarmBody;

    /**
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * P位置
     */
    private String ptzP;

    /**
     * T位置
     */
    private String ptzT;

    /**
     * Z位置
     */
    private String ptzZ;

    /**
     * 告警编码
     */
    private String alarmCode;

    /**
     * 算法编码
     */
    private String algorithmCode;

    /**
     * 客户编号
     */
    private String customerCode;

    /**
     * 视频路径
     */
    private String videoUrl;

    /**
     * 原图
     */
    private String fileImgUrlSrc;

    /**
     * 框图
     */
    private String fileImgUrlIcon;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 0正常 1删除
     */
    private String deleteFlag;

    /**
     * 告警重要等级 1:1级,2:2级
     */
    private String importanceLevel;

    private String importanceLevelName;
    /**
     * 拦截原因：1.置信度阈值，2.告警间隔，3.告警运行时间
     */
    private String intercept;

    /**
     * 上一次告警时间
     */
    private Date prevAlarmTime;

    /**
     * 本次告警置信度阈值
     */
    private Float thisThresh;

    /**
     * 算法配置置信度阈值
     */
    private Float thresh;

    /**
     * 算法配置重复告警间隔
     */
    private Integer alarmInterval;

    /**
     * 算法配置重复告警间隔单位:秒s,分M,时h,天d
     */
    private String alarmintervalType;

    /**
     * 算法配置算法运行时间
     */
    private String algorithmTime;
}
