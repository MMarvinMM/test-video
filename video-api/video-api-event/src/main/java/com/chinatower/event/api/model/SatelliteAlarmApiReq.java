package com.chinatower.event.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SatelliteAlarmApiReq{

    /**
     *  置信度最小值
     *  @required false
     */
    private String confidenceLevelMin;

    /**
     *  置信度最大值
     *  @required false
     */
    private String confidenceLevelMax;

    /**
     * 市编码
     * @required false
     */
    private String cityId;

    /**
     * 县编码
     * @required false
     */
    private String countyId;

    /**
     * 省编码
     * @required false
     */
    private String provinceId;

    /**
     * 告警类型集合
     * @required false
     */
    private List<String> warningTypeId;

    /**
     * 是否推送客户（0：不推送  1：推送）
     * @required false
     */
    private String pushCustomerFlag;

    /**
     * 事件标题
     * @required false
     */
    private String alarmBody;

    /**
     * 告警编码
     * @required false
     */
    private String alarmCode;

    /**
     * 告警结束时间
     * @required true
     */
    private String alarmTimeEnd;

    /**
     * 告警开始时间
     * @required true
     */
    private String alarmTimeStart;

    /**
     *  置信度最小值符号
     *  @required true
     */
    private String minChar;

    /**
     *  置信度最大值符号
     *  @required true
     */
    private String maxChar;

    /**
     * 要过滤管理区域表的别名（铁塔视角用）
     * @required true
     */
    private List<String> areaPro;

    /**
     * 要过滤管理区域表的别名（铁塔视角用）
     * @required true
     */
    private List<String> areaCity;

    private String excelFlag;

    /**
     * 告警编码集合
     * @required false
     */
    private List<String> alarmCodeList;
}
