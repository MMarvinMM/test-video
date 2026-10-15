package com.chinatower.platform.modules.statistics.entity;

import lombok.Data;

import java.util.Date;

/**
 * 告警标签实体
 */
@Data
public class WarningOrderTagVo {
    //租户ID
    private String tenantId;
    //区县编码
    private String countyCode;
    //设备编号
    private String deviceCode;
    //应用版本
    private String appVerCode;
    //省编码
    private String provinceCode;
    //市编码
    private String cityCode;
    private String yearStr;
    private String monthStr;
    private String dayStr;
    //告警时间
    private String alarmTime;
    private Date handleDateTime;
    //标签ID
    private String tagId;
    //标签名称
    private String tagName;
    //总数量
    private int num;
    //AI告警数量
    private int aiNum;
    //研判数量
    private int judgeNum;
    //真警数量
    private int realNum;
    //假警数量
    private int falseNum;
    //重复告警数量
    private int repeatCompleteNum;
    //告警类型ID
    private String warningTypeId;
    //告警来源
    private String warningSource;
    //行业编码
    private String industryCode;
    //网格ID
    private String gridId;
    //告警编号
    private String warningOrderId;
    private int orderCompleteRealNum;

    public WarningOrderTagVo() {
    }

    public WarningOrderTagVo(String tenantId, String countyCode, String appVerCode, String provinceCode, String cityCode, String deviceCode, String warningSource, String yearStr, String monthStr, String dayStr, String alarmTime, Date handleDateTime, String tagId, int num, int aiNum, int judgeNum, int realNum, int falseNum,int repeatCompleteNum,int orderCompleteRealNum, String industryCode,String gridId,String tagName) {
        this.tenantId = tenantId;
        this.countyCode = countyCode;
        this.appVerCode = appVerCode;
        this.provinceCode = provinceCode;
        this.cityCode = cityCode;
        this.deviceCode = deviceCode;
        this.warningSource = warningSource;
        this.yearStr = yearStr;
        this.monthStr = monthStr;
        this.dayStr = dayStr;
        this.alarmTime = alarmTime;
        this.handleDateTime = handleDateTime;
        this.tagId = tagId;
        this.num = num;
        this.aiNum = aiNum;
        this.judgeNum = judgeNum;
        this.realNum = realNum;
        this.falseNum = falseNum;
        this.repeatCompleteNum = repeatCompleteNum;
        this.industryCode = industryCode;
        this.gridId = gridId;
        this.tagName = tagName;
        this.orderCompleteRealNum = orderCompleteRealNum;
    }
    public WarningOrderTagVo(String countyCode, String provinceCode, String cityCode, String yearStr, String monthStr, String dayStr, String alarmTime, Date handleDateTime, String tagId, int num, int aiNum, int judgeNum, int realNum, int falseNum,int repeatCompleteNum,String tagName) {
        this.countyCode = countyCode;
        this.provinceCode = provinceCode;
        this.cityCode = cityCode;
        this.yearStr = yearStr;
        this.monthStr = monthStr;
        this.dayStr = dayStr;
        this.alarmTime = alarmTime;
        this.handleDateTime = handleDateTime;
        this.tagId = tagId;
        this.num = num;
        this.aiNum = aiNum;
        this.judgeNum = judgeNum;
        this.realNum = realNum;
        this.falseNum = falseNum;
        this.repeatCompleteNum = repeatCompleteNum;
        this.tagName = tagName;
    }
}
