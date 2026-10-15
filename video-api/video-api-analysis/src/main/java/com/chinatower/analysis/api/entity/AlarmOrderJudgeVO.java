package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @className: AlarmOrderJudgeVO
 * @description:
 * @author: yongqi.han
 * @date: 2022/6/20
 * @requestID 需求单号及需求名称
 */
@Data
public class AlarmOrderJudgeVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String provinceId;
    private String cityId;
    private String countyId;
    private String warningTypeId;
    private String algorithmCode;
    private String warningSource;
    private String deviceCode;
    private String tenantId;
    private String tenantName;
    private int todayAll1;
    private int stayJudgeNum1;
    private int alreadyJudgeNum1;
    private int totalJudgeNum1;
    private int errorJudgeNum1;
    private int realJudgeNum1;
    private int repeatJudgeNum1;

    private int todayAll2;
    private int stayJudgeNum2;
    private int serialStayJudgeNum2;
    private int parallelStayJudgeNum2;
    private int alreadyJudgeNum2;
    private int serialAlreadyJudgeNum2;
    private int serialAlreadyRealJudgeNum2;
    private int parallelAlreadyJudgeNum2;
    private int serialAlarmNum2;
    private int parallelAlarmNum2;

    private int serialTotalJudgeNum2;
    private int parallelTotalJudgeNum2;
    private int serialErrorJudgeNum2;
    private int parallelErrorJudgeNum2;
    private int serialRealJudgeNum2;
    private int parallelRealJudgeNum2;
    private int repeat_judge_num2;

    //运营研判统计输出信息
    private int totalAlarmNum;
    private int notOpenAlarmNum;
    private int serialAlarmNum;
    private int parallelAlarmNum;
    private int totalJudgeNum;
    private int notOpenJudgeNum;
    private int serialJudgeNum;
    private int parallelJudgeNum;
    private int totalRealAlarmNum;
    private int notOpenRealAlarmNum;
    private int serialRealAlarmNum;
    private int parallelRealAlarmNum;
    private int totalFalseAlarmNum;
    private int notOpenFalseAlarmNum;
    private int serialFalseAlarmNum;
    private int parallelFalseAlarmNum;

    private String dateTime;
    private String yearStr;
    private String monthStr;
    private String dayStr;
    /**
     * 属地编码
     */
    private String colonyCode;
    /**
     * 属地名称
     */
    private String colonyName;
    //算法名称
    private String algorithmName;
    //厂家名称
    private String productName;
    //算法类别名称
    private String functionCodeName;
    //省名称
    private String provinceName;
    //市名称
    private String cityName;
    //告警类型名称
    private String warningTypeName;
    public AlarmOrderJudgeVO(){};

    public AlarmOrderJudgeVO(String provinceId, String cityId, String countyId, String warningTypeId, String algorithmCode, String warningSource, String deviceCode, String tenantId, String tenantName,
                             int totalAlarmNum, int notOpenAlarmNum, int serialAlarmNum, int parallelAlarmNum,
                             int totalJudgeNum, int notOpenJudgeNum, int serialJudgeNum, int parallelJudgeNum,
                             int totalRealAlarmNum, int notOpenRealAlarmNum, int serialRealAlarmNum, int parallelRealAlarmNum,
                             int totalFalseAlarmNum, int notOpenFalseAlarmNum, int serialFalseAlarmNum, int parallelFalseAlarmNum) {
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.warningTypeId = warningTypeId;
        this.algorithmCode = algorithmCode;
        this.warningSource = warningSource;
        this.deviceCode = deviceCode;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.totalAlarmNum = totalAlarmNum;
        this.notOpenAlarmNum = notOpenAlarmNum;
        this.serialAlarmNum = serialAlarmNum;
        this.parallelAlarmNum = parallelAlarmNum;
        this.totalJudgeNum = totalJudgeNum;
        this.notOpenJudgeNum = notOpenJudgeNum;
        this.serialJudgeNum = serialJudgeNum;
        this.parallelJudgeNum = parallelJudgeNum;
        this.totalRealAlarmNum = totalRealAlarmNum;
        this.notOpenRealAlarmNum = notOpenRealAlarmNum;
        this.serialRealAlarmNum = serialRealAlarmNum;
        this.parallelRealAlarmNum = parallelRealAlarmNum;
        this.totalFalseAlarmNum = totalFalseAlarmNum;
        this.notOpenFalseAlarmNum = notOpenFalseAlarmNum;
        this.serialFalseAlarmNum = serialFalseAlarmNum;
        this.parallelFalseAlarmNum = parallelFalseAlarmNum;
    }

    public AlarmOrderJudgeVO(String provinceId, String cityId, String countyId, String warningTypeId, String algorithmCode, String warningSource, String deviceCode, String tenantId, String tenantName,
                             int totalAlarmNum, int notOpenAlarmNum, int serialAlarmNum, int parallelAlarmNum,
                             int totalJudgeNum, int notOpenJudgeNum, int serialJudgeNum, int parallelJudgeNum,
                             int totalRealAlarmNum, int notOpenRealAlarmNum, int serialRealAlarmNum, int parallelRealAlarmNum,
                             int totalFalseAlarmNum, int notOpenFalseAlarmNum, int serialFalseAlarmNum, int parallelFalseAlarmNum,
                             String dateTime, String yearStr, String monthStr, String dayStr) {
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.warningTypeId = warningTypeId;
        this.algorithmCode = algorithmCode;
        this.warningSource = warningSource;
        this.deviceCode = deviceCode;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.totalAlarmNum = totalAlarmNum;
        this.notOpenAlarmNum = notOpenAlarmNum;
        this.serialAlarmNum = serialAlarmNum;
        this.parallelAlarmNum = parallelAlarmNum;
        this.totalJudgeNum = totalJudgeNum;
        this.notOpenJudgeNum = notOpenJudgeNum;
        this.serialJudgeNum = serialJudgeNum;
        this.parallelJudgeNum = parallelJudgeNum;
        this.totalRealAlarmNum = totalRealAlarmNum;
        this.notOpenRealAlarmNum = notOpenRealAlarmNum;
        this.serialRealAlarmNum = serialRealAlarmNum;
        this.parallelRealAlarmNum = parallelRealAlarmNum;
        this.totalFalseAlarmNum = totalFalseAlarmNum;
        this.notOpenFalseAlarmNum = notOpenFalseAlarmNum;
        this.serialFalseAlarmNum = serialFalseAlarmNum;
        this.parallelFalseAlarmNum = parallelFalseAlarmNum;
        this.dateTime = dateTime;
        this.yearStr = yearStr;
        this.monthStr = monthStr;
        this.dayStr = dayStr;
    }

}
