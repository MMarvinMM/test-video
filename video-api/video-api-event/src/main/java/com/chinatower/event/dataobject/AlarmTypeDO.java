package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 告警类型表
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-26
 */
@Getter
@Setter
@TableName("sys_alarm_type")
public class AlarmTypeDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 告警类型ID
     */
    @TableId(value = "alarm_type_id", type = IdType.AUTO)
    private Long alarmTypeId;

    /**
     * 类型分类表ID 1-算法告警；2-设备告警
     */
    private String alarmTypeClassId;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 类型值
     */
    private String typeValue;

    /**
     * 类型编码
     */
    private String typeCode;

    /**
     * 告警来源
     */
    private String alarmSource;

    /**
     * 租户-- 客户视角新增告警类型用到
     */
    private String tenantId;

    /**
     * 来源: 0-初始化;1-操作-- 未用到
     */
    private String source;

    /**
     * 是否赠送 0:否  1:是 -- 未用到
     */
    private String ifFree;

    /**
     * 行业编码
     */
    private String appVerCode;

    /**
     * 图标
     */
    private String icon;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标志
     */
    private Integer deleteFlag;


    //行业编码
    private String industryCode;

}
