package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @className: JudgeVO
 * @description: 告警研判统计表
 * @author: yongqi.han
 * @date: 2022/6/20
 * @requestID 需求单号及需求名称
 */
@Data
public class JudgeVO implements Serializable {
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
}
