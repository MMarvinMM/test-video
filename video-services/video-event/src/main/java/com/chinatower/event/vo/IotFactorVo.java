package com.chinatower.event.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class IotFactorVo implements Serializable {

    /**
     * 物联告警配置id
     */
    private String groupId;

    /**
     * 物联因子ID
     */
    private String factorId;

    /**
     * 物联因子编码
     */
    private String factorCode;

    /**
     * 物联因子名字
     */
    private String factorName;

    /**
     * 物联设备编码
     */
    private String deviceCode;

    /**
     * 是否被选中 0否 1是
     */
    private String isChoose;

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

    /**监测因子单位*/
    private String monitorUnits;
    /**
     * 传感器类型编码
     */
    private String sensorTypeCode;

    /**
     * 传感器类型名称
     */
    private String sensorTypeName;


    /**
     * 传感器
     */
    private String sensorCode;

    /**
     * 传感器名称
     */
    private String sensorName;

}
