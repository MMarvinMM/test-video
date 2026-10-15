package com.chinatower.event.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 公园关联摄像机对象 vm_park_device_info
 *
 * @author yhb
 * @date 2022-04-02
 */
@Data
@ToString
@ApiModel(value = "", description = "公园表关联摄像机")
public class ParkDeviceInfoVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 公园编号 */
    private Long parkCode;

    /** 公园名称 */
    private String parkName;

    /** 公园承载量 */
    private String parkCarryingCapacity;

    /** 今日人数统计 */
    private String parkTrafficCount;

    /** 今日人数统计/公园承载量 百分比 */
    private String parkPercent;
    /** 摄像机关联数 */
    private String parkDeviceCodes;

    /** 摄像机名称 */
    private String devName;

    /** 公园描述 */
    private String parkDesc;

    /** 创建人 */
    private String creator;

    /** 创建时间 */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creatorDate;

    /** 修改人 */
    private String modifier;

    /** 修改时间 */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifierDate;

    /** 当日人数修改时间 */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifierCountDate;

    /** 0正常 1删除 */
    private Integer deleteFlag;

    /** 租户编码 */
    private String tenantId;
    /** 行业编码*/
    private String industryCode;
    /**行业版本*/
    private String appVerCode;

}
