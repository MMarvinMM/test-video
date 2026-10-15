package com.chinatower.event.entity.vo;

import com.chinatower.common.core.vo.BaseAlarmVO;
import lombok.Data;

import java.util.Map;

/**
 * @Auther: luo.zhenghong
 * @Date: 2022-7-25
 * @Description:
 * 物联设备告警接收请求实体
 */
@Data
public class ReceiveIotAlarmVO extends BaseAlarmVO {
    private static final long serialVersionUID = 1L;
    /**
     * 告警时间
     * @required true
     */
/*    private String alarmTime;
    *//**
     * 设备编码
     * @required true
     *//*
    private String deviceCode;
    *//**
     * 设备名称
     * @required true
     *//*
    private String deviceName;
    *//**
     * 告警编码
     * @required true
     *//*
    private String alarmCode;*/
    /**
     * 其他备注
     * @required false
     */
    private String remark;
    /**
     * 1-1级，需应用在告警列表展示，默认值为1；2-2级，需在工作台内静默展示；
     * @required true
     */
    private String importanceLevel;
    /**
     * 告警内容
     * @required true
     */
    //private String alarmBody;

    /**null - 实时
     1 - 分
     2 - 时
     3 - 天
     * 时间频次
     * @required true
     */
    private String timeFrequency;

    /**
     * 物联因子上报检测因子值,key为检测因子编码,value为检测值,只上传有检测值的物联因子
     * @required true
     */
    private Map<String, Object> monitor;

}
