package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vm_iot_alarm_factor_t")
public class IotAlarmFactorDo implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 物联告警配置id
     */
    private String configId;

    /**
     * 物联因子id
     */
    private String factorId;

    /**物联因子名称
     *
     */
    private String factorName;

    /**
     * 物联因子code
     */
    private String factorCode;

    /**
     * 最小值符号（<   <=）
     */
    private String minChar;

    /**
     * 最小值
     */
    private String minNum;

    /**
     * 最大值符号（<   <=）
     */
    private String maxChar;

    /**
     * 最大值
     */
    private String maxNum;


    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标识（1已删除 0未删除）
     */
    private Integer deleteFlag;

    /**物联因子 0:检测值  1:累加值*/
    private String valueType;

    /**累加值*/
    private String sumValue;

    /**累加值单位 天时分*/
    private String sumValueUnit;

    /**告警等级 字典配置*/
    private String alarmLevel;

    /**告警防抖类型 1:触发次数 2:持续超标 3:突然超标*/
    private String antiShakeType;

    /**告警防抖次数*/
    private String antiShakeNum;

    /**告警防抖时间*/
    private String antiShakeTime;

    /**告警防抖时间单位  时分秒*/
    private String antiShakeTimeUnit;

    /**告警压制时间*/
    private String interceptTime;

    /**告警压制时间单位  时分秒*/
    private String interceptTimeUnit;


}
