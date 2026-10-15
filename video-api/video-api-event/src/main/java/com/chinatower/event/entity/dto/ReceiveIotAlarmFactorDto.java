package com.chinatower.event.entity.dto;


import lombok.Data;
import java.io.Serializable;

@Data
public class ReceiveIotAlarmFactorDto implements Serializable {
    /**
     * 物联告警规则id
     */
    private String groupId;

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
     * 物联因子值
     */
    private String factorValue;

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

    /**物联因子 0:检测值  1:累加值  2:监测均值*/
    private String valueType;

    /**mongo累加值*/
    private String sumMongoValue;

    /**累加值时间*/
    private String sumValue;

    /**累加值单位 天时分*/
    private String sumValueUnit;

    /**物联告警类型*/
    private String iotAlarmType;

    /**物联因子关系 0:且  1:或*/
    private String relation;

    /**监测因子单位*/
    private String monitorUnits;

    /**因子是否符合阈值范围 1:符合  2:不符合*/
    private boolean isTrue;


    private String configId;

    /**
     * 传感器编码
     */
    private String sensorCode;

    /**
     * 传感器名称
     */
    private String sensorName;

    /**
     * 传感器编码
     */
    private String sensorTypeCode;


}
