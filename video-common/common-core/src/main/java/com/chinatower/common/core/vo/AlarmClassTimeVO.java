package com.chinatower.common.core.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class AlarmClassTimeVO implements Serializable {

    private String alarmTypeClassId; //类型分类ID

    private String alarmTypeId;//告警类型id

    private String alarmTypeName;//告警类型id

    private String icon;//告警类型id

    private String processLink; //流程环节

    private Integer timeLimit;  //时限

    private String tenantId; //租户Id

    private List<AlarmClassTimeVO> alarmClassTimeVOS;

    private String typeValue; //事件类型值

}
