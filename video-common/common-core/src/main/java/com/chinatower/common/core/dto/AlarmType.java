package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description 告警类型
 * @author wjd
 * @Date 15:42 2021/7/7
 * @Param
 **/
@Data
public class AlarmType implements Serializable {

    private String alarmTypeId;

    private String alarmTypeClassId;

    private String typeName;

    private String typeValue;

    private String createBy;

    private Date createTime;

    private String deleteFlag;

    private List<AlarmType> typeList;

    private String typeClassId;

    /** 租户 */
    private String tenantor;

    /** 租户ID */
    private String tenantId;

    /** 来源 */
    private String source;

    private String icon;

    private String typeId;
}
