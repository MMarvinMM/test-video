package com.chinatower.platform.project.system.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Name SysAlarmType
 * @Description 告警类型
 * @Author zl
 * @Date 2022/7/13
 */
@Data
public class SysAlarmType implements Serializable {

    private Integer alarmTypeId;

    private String alarmTypeClassId;

    private String typeName;

    private Integer typeValue;

    private String typeCode;

    private String alarmSource;

    /** 租户 */
    private String tenantor;

    /** 来源 */
    private String source;

    private String ifFree;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date updateTime;

    private int deleteFlag;

    private String icon;

    private String appVerCode;

}