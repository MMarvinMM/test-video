package com.chinatower.common.core.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 告警类型阈值参数
 * @author chmod
 * @version 1.0
 * @date 2022/3/11 11:22
 */
@Data
public class AlarmClassParamVO implements Serializable {

    private String alarmTypeClassId; //类型分类ID

    private String alarmTypeId;//告警类型id

    private String thresh;//置信度

    private String alarmInterval;//重复告警间隔

    private String alarmIntervalType;//重复告警间隔单位:秒s,分M,时h,天d

    private String[] algorithmTime; //算法运行时间

    private String algorithmTimeJosn;

    private String tenantId; //租户Id
}
