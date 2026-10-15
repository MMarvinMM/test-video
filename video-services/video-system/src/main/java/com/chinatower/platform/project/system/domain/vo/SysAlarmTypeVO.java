package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
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
public class SysAlarmTypeVO extends BaseDataScopeEntity implements Serializable {

    private Integer alarmTypeId;

    private String alarmTypeClassId;
    /** 告警类型名称 */
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
    
    private String icon;

    private String appVerCode;

    private int pageSize;

    private int pageNum;

    private String createBeginTime;

    private String createEndTime;

    private String updateBeginTime;

    private String updateEndTime;

    /**
     * 拼接权限校验
     */
    private String dataScope;

//    private List<SysAlarmTypeVO> sysAlarmTypeList;
}