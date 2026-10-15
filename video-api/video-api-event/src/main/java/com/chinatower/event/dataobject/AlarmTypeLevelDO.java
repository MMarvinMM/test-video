package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@TableName("sys_alarm_type_level")
public class AlarmTypeLevelDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    private Long id;

    /**
     * 告警类型编码
     */
    private String typeValue;

    /**
     * 告警等级 字典枚举：alarm_level
     */
    private String alarmLevel;

    /**
     * 客户ID-客户视角人工上报告警类型用到
     */
    private String tenantId;

    /**
     * 行业编码-客户视角人工上报告警类型用到
     */
    private String industryCode;

    /**
     * 行业应用版本
     */
    private String appVerCode;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /**
     * 更新人
     */
    private String modifier;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    /**
     * 删除标志 0正常 1删除
     */
    private Integer deleteFlag;
}
